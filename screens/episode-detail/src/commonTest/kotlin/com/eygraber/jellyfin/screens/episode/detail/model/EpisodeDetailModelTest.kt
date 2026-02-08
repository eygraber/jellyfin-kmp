package com.eygraber.jellyfin.screens.episode.detail.model

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

class EpisodeDetailModelTest {
  private lateinit var fakeRepository: FakeItemsRepository
  private lateinit var fakeLibraryService: FakeLibraryService
  private lateinit var model: EpisodeDetailModel

  @BeforeTest
  fun setUp() {
    fakeRepository = FakeItemsRepository()
    fakeLibraryService = FakeLibraryService()
    model = EpisodeDetailModel(
      itemsRepository = fakeRepository,
      libraryService = fakeLibraryService,
    )
  }

  @Test
  fun loadEpisode_maps_item_to_episode_detail() {
    runTest {
      fakeRepository.getItemResult = JellyfinResult.Success(
        createLibraryItem(
          id = "ep-1",
          name = "Pilot",
          overview = "Walter White turns to crime",
          seriesName = "Breaking Bad",
          productionYear = 1,
          runTimeTicks = 34_800_000_000L,
          primaryImageTag = "thumb-tag",
        ),
      )

      model.loadEpisode(episodeId = "ep-1")

      val state = model.stateForTest
      state.isLoading.shouldBeFalse()
      state.error.shouldBeNull()

      val episode = state.episode
      episode.shouldNotBeNull()
      episode.id shouldBe "ep-1"
      episode.name shouldBe "Pilot"
      episode.seriesName shouldBe "Breaking Bad"
      episode.seasonEpisodeLabel shouldBe "Episode 1"
      episode.overview shouldBe "Walter White turns to crime"
      episode.runtimeMinutes shouldBe 58
      episode.thumbnailImageUrl.shouldNotBeNull()
    }
  }

  @Test
  fun loadEpisode_with_error_sets_error_state() {
    runTest {
      fakeRepository.getItemResult = JellyfinResult.Error(
        message = "Server error",
        isEphemeral = true,
      )

      model.loadEpisode(episodeId = "ep-1")

      val state = model.stateForTest
      state.isLoading.shouldBeFalse()
      state.error shouldBe EpisodeDetailModelError.LoadFailed
      state.episode.shouldBeNull()
    }
  }

  @Test
  fun loadEpisode_without_image_tag_has_null_url() {
    runTest {
      fakeRepository.getItemResult = JellyfinResult.Success(
        createLibraryItem(
          id = "ep-1",
          name = "Pilot",
          primaryImageTag = null,
        ),
      )

      model.loadEpisode(episodeId = "ep-1")

      val state = model.stateForTest
      val episode = state.episode
      episode.shouldNotBeNull()
      episode.thumbnailImageUrl.shouldBeNull()
    }
  }

  @Test
  fun loadEpisode_without_runtime_has_null_minutes() {
    runTest {
      fakeRepository.getItemResult = JellyfinResult.Success(
        createLibraryItem(
          id = "ep-1",
          name = "Pilot",
          runTimeTicks = null,
        ),
      )

      model.loadEpisode(episodeId = "ep-1")

      val state = model.stateForTest
      val episode = state.episode
      episode.shouldNotBeNull()
      episode.runtimeMinutes.shouldBeNull()
    }
  }

  @Test
  fun loadEpisode_clears_error_on_retry() {
    runTest {
      fakeRepository.getItemResult = JellyfinResult.Error(
        message = "Server error",
        isEphemeral = true,
      )

      model.loadEpisode(episodeId = "ep-1")
      model.stateForTest.error shouldBe EpisodeDetailModelError.LoadFailed

      fakeRepository.getItemResult = JellyfinResult.Success(
        createLibraryItem(id = "ep-1", name = "Pilot"),
      )

      model.loadEpisode(episodeId = "ep-1")

      val state = model.stateForTest
      state.error.shouldBeNull()
      state.episode.shouldNotBeNull()
      state.isLoading.shouldBeFalse()
    }
  }

  @Test
  fun initial_state_is_loading() {
    val state = model.stateForTest
    state.isLoading.shouldBeTrue()
    state.episode.shouldBeNull()
    state.error.shouldBeNull()
  }

  @Suppress("LongParameterList")
  private fun createLibraryItem(
    id: String,
    name: String,
    overview: String? = null,
    seriesName: String? = null,
    productionYear: Int? = null,
    runTimeTicks: Long? = null,
    primaryImageTag: String? = null,
  ) = LibraryItem(
    id = id,
    name = name,
    sortName = null,
    type = "Episode",
    overview = overview,
    productionYear = productionYear,
    communityRating = null,
    officialRating = null,
    primaryImageTag = primaryImageTag,
    backdropImageTags = emptyList(),
    seriesName = seriesName,
    seriesId = null,
    childCount = null,
    runTimeTicks = runTimeTicks,
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
