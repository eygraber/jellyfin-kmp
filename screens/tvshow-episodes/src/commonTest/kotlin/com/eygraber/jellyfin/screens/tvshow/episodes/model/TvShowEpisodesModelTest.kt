package com.eygraber.jellyfin.screens.tvshow.episodes.model

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

class TvShowEpisodesModelTest {
  private lateinit var fakeRepository: FakeItemsRepository
  private lateinit var fakeLibraryService: FakeEpisodesLibraryService
  private lateinit var model: TvShowEpisodesModel

  @BeforeTest
  fun setUp() {
    fakeRepository = FakeItemsRepository()
    fakeLibraryService = FakeEpisodesLibraryService()
    model = TvShowEpisodesModel(
      itemsRepository = fakeRepository,
      libraryService = fakeLibraryService,
    )
  }

  @Test
  fun loadEpisodes_maps_items_to_episode_items() {
    runTest {
      fakeRepository.getItemResult = JellyfinResult.Success(
        createLibraryItem(id = "season-1", name = "Season 1"),
      )
      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(
          items = listOf(
            createLibraryItem(
              id = "ep-1",
              name = "Pilot",
              productionYear = 1,
              overview = "The first episode",
              runTimeTicks = 34_800_000_000L,
              primaryImageTag = "tag1",
            ),
          ),
          totalRecordCount = 1,
          startIndex = 0,
        ),
      )

      model.loadEpisodes("season-1")

      val state = model.stateForTest
      state.isLoading.shouldBeFalse()
      state.error.shouldBeNull()
      state.seasonName shouldBe "Season 1"
      state.episodes.size shouldBe 1

      val episode = state.episodes[0]
      episode.id shouldBe "ep-1"
      episode.name shouldBe "Pilot"
      episode.episodeNumber shouldBe 1
      episode.overview shouldBe "The first episode"
      episode.runtimeMinutes shouldBe 58
      episode.imageUrl.shouldNotBeNull()
    }
  }

  @Test
  fun loadEpisodes_with_error_sets_error_state() {
    runTest {
      fakeRepository.getItemResult = JellyfinResult.Success(
        createLibraryItem(id = "season-1", name = "Season 1"),
      )
      fakeRepository.getItemsResult = JellyfinResult.Error(
        message = "Server error",
        isEphemeral = true,
      )

      model.loadEpisodes("season-1")

      val state = model.stateForTest
      state.isLoading.shouldBeFalse()
      state.error shouldBe TvShowEpisodesModelError.LoadFailed
    }
  }

  @Test
  fun loadEpisodes_with_empty_result_shows_empty() {
    runTest {
      fakeRepository.getItemResult = JellyfinResult.Success(
        createLibraryItem(id = "season-1", name = "Season 1"),
      )
      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(
          items = emptyList(),
          totalRecordCount = 0,
          startIndex = 0,
        ),
      )

      model.loadEpisodes("season-1")

      val state = model.stateForTest
      state.isLoading.shouldBeFalse()
      state.error.shouldBeNull()
      state.episodes.size shouldBe 0
    }
  }

  @Test
  fun episode_without_image_tag_has_null_image_url() {
    runTest {
      fakeRepository.getItemResult = JellyfinResult.Success(
        createLibraryItem(id = "season-1", name = "Season 1"),
      )
      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(
          items = listOf(
            createLibraryItem(id = "ep-1", name = "Episode 1", primaryImageTag = null),
          ),
          totalRecordCount = 1,
          startIndex = 0,
        ),
      )

      model.loadEpisodes("season-1")

      val state = model.stateForTest
      state.episodes[0].imageUrl.shouldBeNull()
    }
  }

  @Test
  fun episode_without_runtime_has_null_minutes() {
    runTest {
      fakeRepository.getItemResult = JellyfinResult.Success(
        createLibraryItem(id = "season-1", name = "Season 1"),
      )
      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(
          items = listOf(
            createLibraryItem(id = "ep-1", name = "Episode 1", runTimeTicks = null),
          ),
          totalRecordCount = 1,
          startIndex = 0,
        ),
      )

      model.loadEpisodes("season-1")

      val state = model.stateForTest
      state.episodes[0].runtimeMinutes.shouldBeNull()
    }
  }

  private fun createLibraryItem(
    id: String,
    name: String,
    productionYear: Int? = null,
    overview: String? = null,
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
    seriesName = null,
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

private class FakeEpisodesLibraryService : JellyfinLibraryService {
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
