package com.eygraber.jellyfin.screens.tvshow.detail.model

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.data.items.ItemSortBy
import com.eygraber.jellyfin.data.items.ItemsRepository
import com.eygraber.jellyfin.data.items.LibraryItem
import com.eygraber.jellyfin.data.items.PaginatedResult
import com.eygraber.jellyfin.data.items.SortOrder
import com.eygraber.jellyfin.sdk.core.model.ImageType
import com.eygraber.jellyfin.services.sdk.JellyfinLibraryService
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class TvShowDetailModelTest {
  private lateinit var fakeRepository: FakeItemsRepository
  private lateinit var fakeLibraryService: FakeLibraryService
  private lateinit var model: TvShowDetailModel

  @BeforeTest
  fun setUp() {
    fakeRepository = FakeItemsRepository()
    fakeLibraryService = FakeLibraryService()
    model = TvShowDetailModel(
      itemsRepository = fakeRepository,
      libraryService = fakeLibraryService,
    )
  }

  @Test
  fun loadShow_maps_item_to_show_detail() {
    runTest {
      fakeRepository.getItemResult = JellyfinResult.Success(
        createLibraryItem(
          id = "show-1",
          name = "Breaking Bad",
          overview = "A chemistry teacher turns to crime",
          productionYear = 2008,
          communityRating = 9.5F,
          officialRating = "TV-MA",
          primaryImageTag = "poster-tag",
          backdropImageTags = listOf("backdrop-tag"),
        ),
      )
      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(
          items = listOf(
            createLibraryItem(id = "s1", name = "Season 1", childCount = 7),
            createLibraryItem(id = "s2", name = "Season 2", childCount = 13),
          ),
          totalRecordCount = 2,
          startIndex = 0,
        ),
      )

      model.loadShow(seriesId = "show-1")

      val state = model.stateForTest
      state.isLoading.shouldBeFalse()
      state.error.shouldBeNull()

      val show = state.show
      show.shouldNotBeNull()
      show.id shouldBe "show-1"
      show.name shouldBe "Breaking Bad"
      show.overview shouldBe "A chemistry teacher turns to crime"
      show.productionYear shouldBe 2008
      show.communityRating shouldBe 9.5F
      show.officialRating shouldBe "TV-MA"
      show.seasonCount shouldBe 2
      show.posterImageUrl.shouldNotBeNull()
      show.backdropImageUrl.shouldNotBeNull()

      state.seasons.size shouldBe 2
      state.seasons[0].name shouldBe "Season 1"
      state.seasons[0].episodeCount shouldBe 7
      state.seasons[1].name shouldBe "Season 2"
      state.seasons[1].episodeCount shouldBe 13
    }
  }

  @Test
  fun loadShow_with_error_sets_error_state() {
    runTest {
      fakeRepository.getItemResult = JellyfinResult.Error(
        message = "Server error",
        isEphemeral = true,
      )

      model.loadShow(seriesId = "show-1")

      val state = model.stateForTest
      state.isLoading.shouldBeFalse()
      state.error shouldBe TvShowDetailModelError.LoadFailed
      state.show.shouldBeNull()
    }
  }

  @Test
  fun loadShow_with_seasons_error_shows_empty_seasons() {
    runTest {
      fakeRepository.getItemResult = JellyfinResult.Success(
        createLibraryItem(id = "show-1", name = "Breaking Bad"),
      )
      fakeRepository.getItemsResult = JellyfinResult.Error(
        message = "Server error",
        isEphemeral = true,
      )

      model.loadShow(seriesId = "show-1")

      val state = model.stateForTest
      state.isLoading.shouldBeFalse()
      state.error.shouldBeNull()
      val show = state.show
      show.shouldNotBeNull()
      state.seasons.size shouldBe 0
      show.seasonCount shouldBe 0
    }
  }

  @Test
  fun loadShow_clears_error_on_retry() {
    runTest {
      fakeRepository.getItemResult = JellyfinResult.Error(
        message = "Server error",
        isEphemeral = true,
      )

      model.loadShow(seriesId = "show-1")
      model.stateForTest.error shouldBe TvShowDetailModelError.LoadFailed

      fakeRepository.getItemResult = JellyfinResult.Success(
        createLibraryItem(id = "show-1", name = "Breaking Bad"),
      )
      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(items = emptyList(), totalRecordCount = 0, startIndex = 0),
      )

      model.loadShow(seriesId = "show-1")

      val state = model.stateForTest
      state.error.shouldBeNull()
      state.show.shouldNotBeNull()
      state.isLoading.shouldBeFalse()
    }
  }

  @Test
  fun initial_state_is_loading() {
    val state = model.stateForTest
    state.isLoading.shouldBeTrue()
    state.show.shouldBeNull()
    state.error.shouldBeNull()
    state.seasons.size shouldBe 0
  }

  @Suppress("LongParameterList")
  private fun createLibraryItem(
    id: String,
    name: String,
    overview: String? = null,
    productionYear: Int? = null,
    communityRating: Float? = null,
    officialRating: String? = null,
    primaryImageTag: String? = null,
    backdropImageTags: List<String> = emptyList(),
    childCount: Int? = null,
  ) = LibraryItem(
    id = id,
    name = name,
    sortName = null,
    type = "Series",
    overview = overview,
    productionYear = productionYear,
    communityRating = communityRating,
    officialRating = officialRating,
    primaryImageTag = primaryImageTag,
    backdropImageTags = backdropImageTags,
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

private class FakeLibraryService : JellyfinLibraryService {
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
