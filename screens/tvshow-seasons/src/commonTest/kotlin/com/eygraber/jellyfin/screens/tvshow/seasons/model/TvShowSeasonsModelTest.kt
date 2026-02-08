package com.eygraber.jellyfin.screens.tvshow.seasons.model

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

class TvShowSeasonsModelTest {
  private lateinit var fakeRepository: FakeItemsRepository
  private lateinit var fakeLibraryService: FakeSeasonsLibraryService
  private lateinit var model: TvShowSeasonsModel

  @BeforeTest
  fun setUp() {
    fakeRepository = FakeItemsRepository()
    fakeLibraryService = FakeSeasonsLibraryService()
    model = TvShowSeasonsModel(
      itemsRepository = fakeRepository,
      libraryService = fakeLibraryService,
    )
  }

  @Test
  fun loadSeasons_maps_items_to_season_items() {
    runTest {
      fakeRepository.getItemResult = JellyfinResult.Success(
        createLibraryItem(id = "series-1", name = "Breaking Bad"),
      )
      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(
          items = listOf(
            createLibraryItem(
              id = "season-1",
              name = "Season 1",
              productionYear = 1,
              childCount = 7,
              primaryImageTag = "tag1",
            ),
            createLibraryItem(
              id = "season-2",
              name = "Season 2",
              productionYear = 2,
              childCount = 13,
              primaryImageTag = "tag2",
            ),
          ),
          totalRecordCount = 2,
          startIndex = 0,
        ),
      )

      model.loadSeasons("series-1")

      val state = model.stateForTest
      state.isLoading.shouldBeFalse()
      state.error.shouldBeNull()
      state.showName shouldBe "Breaking Bad"
      state.seasons.size shouldBe 2

      val season1 = state.seasons[0]
      season1.id shouldBe "season-1"
      season1.name shouldBe "Season 1"
      season1.seasonNumber shouldBe 1
      season1.episodeCount shouldBe 7
      season1.imageUrl.shouldNotBeNull()
    }
  }

  @Test
  fun loadSeasons_with_error_sets_error_state() {
    runTest {
      fakeRepository.getItemResult = JellyfinResult.Success(
        createLibraryItem(id = "series-1", name = "Breaking Bad"),
      )
      fakeRepository.getItemsResult = JellyfinResult.Error(
        message = "Server error",
        isEphemeral = true,
      )

      model.loadSeasons("series-1")

      val state = model.stateForTest
      state.isLoading.shouldBeFalse()
      state.error shouldBe TvShowSeasonsModelError.LoadFailed
    }
  }

  @Test
  fun loadSeasons_with_empty_result_shows_empty() {
    runTest {
      fakeRepository.getItemResult = JellyfinResult.Success(
        createLibraryItem(id = "series-1", name = "Breaking Bad"),
      )
      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(
          items = emptyList(),
          totalRecordCount = 0,
          startIndex = 0,
        ),
      )

      model.loadSeasons("series-1")

      val state = model.stateForTest
      state.isLoading.shouldBeFalse()
      state.error.shouldBeNull()
      state.seasons.size shouldBe 0
    }
  }

  @Test
  fun season_without_image_tag_has_null_image_url() {
    runTest {
      fakeRepository.getItemResult = JellyfinResult.Success(
        createLibraryItem(id = "series-1", name = "Test Show"),
      )
      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(
          items = listOf(
            createLibraryItem(id = "season-1", name = "Season 1", primaryImageTag = null),
          ),
          totalRecordCount = 1,
          startIndex = 0,
        ),
      )

      model.loadSeasons("series-1")

      val state = model.stateForTest
      state.seasons[0].imageUrl.shouldBeNull()
    }
  }

  private fun createLibraryItem(
    id: String,
    name: String,
    productionYear: Int? = null,
    childCount: Int? = null,
    primaryImageTag: String? = null,
  ) = LibraryItem(
    id = id,
    name = name,
    sortName = null,
    type = "Season",
    overview = null,
    productionYear = productionYear,
    communityRating = null,
    officialRating = null,
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

private class FakeSeasonsLibraryService : JellyfinLibraryService {
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
