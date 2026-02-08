package com.eygraber.jellyfin.screens.music.artist.albums.model

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.data.items.ItemSortBy
import com.eygraber.jellyfin.data.items.ItemsRepository
import com.eygraber.jellyfin.data.items.LibraryItem
import com.eygraber.jellyfin.data.items.PaginatedResult
import com.eygraber.jellyfin.data.items.SortOrder
import com.eygraber.jellyfin.sdk.core.model.ImageType
import com.eygraber.jellyfin.services.sdk.JellyfinLibraryService
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class ArtistAlbumsModelTest {
  private lateinit var fakeRepository: FakeItemsRepository
  private lateinit var fakeLibraryService: FakeArtistAlbumsLibraryService
  private lateinit var model: ArtistAlbumsModel

  @BeforeTest
  fun setUp() {
    fakeRepository = FakeItemsRepository()
    fakeLibraryService = FakeArtistAlbumsLibraryService()
    model = ArtistAlbumsModel(
      itemsRepository = fakeRepository,
      libraryService = fakeLibraryService,
    )
  }

  @Test
  fun loadAlbums_success_populates_artist_detail_and_albums() {
    runTest {
      fakeRepository.getItemResult = JellyfinResult.Success(
        createLibraryItem(
          id = "artist-1",
          name = "Pink Floyd",
          type = "MusicArtist",
          overview = "English rock band formed in London.",
          officialRating = "Progressive Rock",
          primaryImageTag = "artist-tag",
        ),
      )

      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(
          items = listOf(
            createLibraryItem(
              id = "album-1",
              name = "The Dark Side of the Moon",
              type = "MusicAlbum",
              productionYear = 1973,
              childCount = 10,
              primaryImageTag = "tag1",
            ),
            createLibraryItem(
              id = "album-2",
              name = "Wish You Were Here",
              type = "MusicAlbum",
              productionYear = 1975,
              childCount = 5,
              primaryImageTag = null,
            ),
          ),
          totalRecordCount = 2,
          startIndex = 0,
        ),
      )

      model.loadAlbums("artist-1")

      val state = model.stateForTest
      state.isLoading.shouldBeFalse()
      state.error.shouldBeNull()

      val artist = state.artist
      artist.shouldNotBeNull()
      artist.name shouldBe "Pink Floyd"
      artist.overview shouldBe "English rock band formed in London."
      artist.genre shouldBe "Progressive Rock"
      artist.imageUrl.shouldNotBeNull()

      state.albums.size shouldBe 2
      state.albums[0].name shouldBe "The Dark Side of the Moon"
      state.albums[0].productionYear shouldBe 1973
      state.albums[0].trackCount shouldBe 10
      state.albums[0].imageUrl.shouldNotBeNull()
      state.albums[1].name shouldBe "Wish You Were Here"
      state.albums[1].imageUrl.shouldBeNull()
    }
  }

  @Test
  fun loadAlbums_error_sets_error_state() {
    runTest {
      fakeRepository.getItemResult = JellyfinResult.Success(
        createLibraryItem(id = "artist-1", name = "Artist", type = "MusicArtist"),
      )

      fakeRepository.getItemsResult = JellyfinResult.Error(
        message = "Server error",
        isEphemeral = true,
      )

      model.loadAlbums("artist-1")

      val state = model.stateForTest
      state.isLoading.shouldBeFalse()
      state.error shouldBe ArtistAlbumsModelError.LoadFailed
    }
  }

  @Test
  fun album_without_image_tag_has_null_image_url() {
    runTest {
      fakeRepository.getItemResult = JellyfinResult.Success(
        createLibraryItem(id = "artist-1", name = "Artist", type = "MusicArtist"),
      )

      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(
          items = listOf(
            createLibraryItem(
              id = "album-1",
              name = "Album",
              type = "MusicAlbum",
              primaryImageTag = null,
            ),
          ),
          totalRecordCount = 1,
          startIndex = 0,
        ),
      )

      model.loadAlbums("artist-1")

      val state = model.stateForTest
      state.albums[0].imageUrl.shouldBeNull()
    }
  }

  @Test
  fun artist_without_image_tag_has_null_image_url() {
    runTest {
      fakeRepository.getItemResult = JellyfinResult.Success(
        createLibraryItem(
          id = "artist-1",
          name = "Artist",
          type = "MusicArtist",
          primaryImageTag = null,
        ),
      )

      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(items = emptyList(), totalRecordCount = 0, startIndex = 0),
      )

      model.loadAlbums("artist-1")

      val state = model.stateForTest
      val artist = state.artist
      artist.shouldNotBeNull()
      artist.imageUrl.shouldBeNull()
    }
  }

  @Test
  fun artist_overview_is_populated_when_available() {
    runTest {
      fakeRepository.getItemResult = JellyfinResult.Success(
        createLibraryItem(
          id = "artist-1",
          name = "Artist",
          type = "MusicArtist",
          overview = "A great band with many hits.",
        ),
      )

      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(items = emptyList(), totalRecordCount = 0, startIndex = 0),
      )

      model.loadAlbums("artist-1")

      val state = model.stateForTest
      val artist = state.artist
      artist.shouldNotBeNull()
      artist.overview shouldBe "A great band with many hits."
    }
  }

  @Suppress("LongParameterList")
  private fun createLibraryItem(
    id: String,
    name: String,
    type: String = "MusicAlbum",
    productionYear: Int? = null,
    primaryImageTag: String? = null,
    childCount: Int? = null,
    overview: String? = null,
    officialRating: String? = null,
  ) = LibraryItem(
    id = id,
    name = name,
    sortName = null,
    type = type,
    overview = overview,
    productionYear = productionYear,
    communityRating = null,
    officialRating = officialRating,
    primaryImageTag = primaryImageTag,
    backdropImageTags = emptyList(),
    seriesName = null,
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

private class FakeArtistAlbumsLibraryService : JellyfinLibraryService {
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
