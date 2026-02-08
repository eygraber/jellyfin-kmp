package com.eygraber.jellyfin.screens.library.music.model

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.data.items.ItemSortBy
import com.eygraber.jellyfin.data.items.ItemsRepository
import com.eygraber.jellyfin.data.items.LibraryItem
import com.eygraber.jellyfin.data.items.PaginatedResult
import com.eygraber.jellyfin.data.items.SortOrder
import com.eygraber.jellyfin.screens.library.music.MusicTab
import com.eygraber.jellyfin.sdk.core.model.ImageType
import com.eygraber.jellyfin.services.sdk.JellyfinLibraryService
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class MusicLibraryModelTest {
  private lateinit var fakeRepository: FakeItemsRepository
  private lateinit var fakeLibraryService: FakeMusicLibraryService
  private lateinit var model: MusicLibraryModel

  @BeforeTest
  fun setUp() {
    fakeRepository = FakeItemsRepository()
    fakeLibraryService = FakeMusicLibraryService()
    model = MusicLibraryModel(
      itemsRepository = fakeRepository,
      libraryService = fakeLibraryService,
    )
  }

  @Test
  fun loadInitial_loads_artists_by_default() {
    runTest {
      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(
          items = listOf(
            createLibraryItem(
              id = "artist-1",
              name = "Pink Floyd",
              type = "MusicArtist",
              childCount = 15,
              primaryImageTag = "tag1",
            ),
          ),
          totalRecordCount = 1,
          startIndex = 0,
        ),
      )

      model.loadInitial("lib-1")

      val state = model.stateForTest
      state.isLoading.shouldBeFalse()
      state.error.shouldBeNull()
      state.selectedTab shouldBe MusicTab.Artists
      state.artists.size shouldBe 1
      state.artists[0].name shouldBe "Pink Floyd"
      state.artists[0].albumCount shouldBe 15
      state.artists[0].imageUrl.shouldNotBeNull()
    }
  }

  @Test
  fun switchTab_to_albums_loads_albums() {
    runTest {
      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(
          items = listOf(createLibraryItem(id = "artist-1", name = "Artist", type = "MusicArtist")),
          totalRecordCount = 1,
          startIndex = 0,
        ),
      )
      model.loadInitial("lib-1")

      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(
          items = listOf(
            createLibraryItem(
              id = "album-1",
              name = "The Dark Side of the Moon",
              type = "MusicAlbum",
              productionYear = 1973,
              seriesName = "Pink Floyd",
            ),
          ),
          totalRecordCount = 1,
          startIndex = 0,
        ),
      )

      model.switchTab("lib-1", MusicTab.Albums)

      val state = model.stateForTest
      state.isLoading.shouldBeFalse()
      state.selectedTab shouldBe MusicTab.Albums
      state.albums.size shouldBe 1
      state.albums[0].name shouldBe "The Dark Side of the Moon"
      state.albums[0].artistName shouldBe "Pink Floyd"
      state.albums[0].productionYear shouldBe 1973
    }
  }

  @Test
  fun loadInitial_with_error_sets_error_state() {
    runTest {
      fakeRepository.getItemsResult = JellyfinResult.Error(
        message = "Server error",
        isEphemeral = true,
      )

      model.loadInitial("lib-1")

      val state = model.stateForTest
      state.isLoading.shouldBeFalse()
      state.error shouldBe MusicLibraryModelError.LoadFailed
    }
  }

  @Test
  fun artist_without_image_tag_has_null_image_url() {
    runTest {
      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(
          items = listOf(
            createLibraryItem(
              id = "artist-1",
              name = "Artist",
              type = "MusicArtist",
              primaryImageTag = null,
            ),
          ),
          totalRecordCount = 1,
          startIndex = 0,
        ),
      )

      model.loadInitial("lib-1")

      val state = model.stateForTest
      state.artists[0].imageUrl.shouldBeNull()
    }
  }

  private fun createLibraryItem(
    id: String,
    name: String,
    type: String = "MusicArtist",
    productionYear: Int? = null,
    primaryImageTag: String? = null,
    childCount: Int? = null,
    seriesName: String? = null,
  ) = LibraryItem(
    id = id,
    name = name,
    sortName = null,
    type = type,
    overview = null,
    productionYear = productionYear,
    communityRating = null,
    officialRating = null,
    primaryImageTag = primaryImageTag,
    backdropImageTags = emptyList(),
    seriesName = seriesName,
    seriesId = null,
    childCount = childCount,
    runTimeTicks = null,
  )
}

@Suppress("LongParameterList")
private class FakeItemsRepository : ItemsRepository {
  var getItemsResult: JellyfinResult<PaginatedResult<LibraryItem>> = JellyfinResult.Success(
    PaginatedResult(items = emptyList(), totalRecordCount = 0, startIndex = 0),
  )

  var getItemResult: JellyfinResult<LibraryItem> = JellyfinResult.Error(
    message = "Not configured",
    isEphemeral = true,
  )

  var getSimilarItemsResult: JellyfinResult<List<LibraryItem>> = JellyfinResult.Success(emptyList())

  override suspend fun getItems(
    parentId: String,
    includeItemTypes: List<String>?,
    sortBy: ItemSortBy,
    sortOrder: SortOrder,
    startIndex: Int,
    limit: Int,
    genres: List<String>?,
    years: List<Int>?,
    searchTerm: String?,
    fields: List<String>?,
  ): JellyfinResult<PaginatedResult<LibraryItem>> = getItemsResult

  override suspend fun getItem(itemId: String): JellyfinResult<LibraryItem> = getItemResult

  override suspend fun getSimilarItems(
    itemId: String,
    limit: Int?,
  ): JellyfinResult<List<LibraryItem>> = getSimilarItemsResult
}

private class FakeMusicLibraryService : JellyfinLibraryService {
  override fun getImageUrl(
    itemId: String,
    imageType: ImageType,
    maxWidth: Int?,
    maxHeight: Int?,
    tag: String?,
    imageIndex: Int?,
  ): String = "https://example.com/images/$itemId/${imageType.apiValue}"

  override suspend fun getResumeItems(
    limit: Int?,
    mediaTypes: List<String>?,
    fields: List<String>?,
  ) = error("Not used in tests")

  override suspend fun getLatestItems(
    parentId: String?,
    includeItemTypes: List<String>?,
    limit: Int?,
    fields: List<String>?,
  ) = error("Not used in tests")

  override suspend fun getNextUpEpisodes(
    limit: Int?,
    fields: List<String>?,
  ) = error("Not used in tests")

  override suspend fun getUserViews() = error("Not used in tests")

  @Suppress("LongParameterList")
  override suspend fun getItems(
    parentId: String?,
    includeItemTypes: List<String>?,
    sortBy: List<String>?,
    sortOrder: String?,
    startIndex: Int?,
    limit: Int?,
    recursive: Boolean?,
    genres: List<String>?,
    years: List<Int>?,
    searchTerm: String?,
    fields: List<String>?,
  ) = error("Not used in tests")

  override suspend fun getItem(itemId: String) = error("Not used in tests")

  override suspend fun getSimilarItems(
    itemId: String,
    limit: Int?,
  ) = error("Not used in tests")
}
