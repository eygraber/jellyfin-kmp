package com.eygraber.jellyfin.screens.home.model

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.screens.home.ContinueWatchingState
import com.eygraber.jellyfin.sdk.core.model.BaseItemDto
import com.eygraber.jellyfin.sdk.core.model.ImageType
import com.eygraber.jellyfin.sdk.core.model.ItemsResult
import com.eygraber.jellyfin.sdk.core.model.UserItemDataDto
import com.eygraber.jellyfin.services.sdk.JellyfinLibraryService
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class ContinueWatchingModelTest {
  private lateinit var fakeLibraryService: FakeJellyfinLibraryService
  private lateinit var model: ContinueWatchingModel

  @BeforeTest
  fun setUp() {
    fakeLibraryService = FakeJellyfinLibraryService()
    model = ContinueWatchingModel(libraryService = fakeLibraryService)
  }

  @Test
  fun refresh_with_items_returns_loaded_state() {
    runTest {
      fakeLibraryService.resumeItemsResult = JellyfinResult.Success(
        ItemsResult(
          items = listOf(
            createBaseItemDto(
              id = "movie-1",
              name = "Test Movie",
              type = "Movie",
              runTimeTicks = 100_000L,
              playbackPositionTicks = 50_000L,
            ),
          ),
          totalRecordCount = 1,
        ),
      )

      model.refresh()

      val state = model.stateForTest
      val loaded = state.shouldBeInstanceOf<ContinueWatchingState.Loaded>()
      loaded.items.size shouldBe 1
      loaded.items[0].id shouldBe "movie-1"
      loaded.items[0].name shouldBe "Test Movie"
      loaded.items[0].progressPercent shouldBe 0.5F
    }
  }

  @Test
  fun refresh_with_empty_items_returns_empty_state() {
    runTest {
      fakeLibraryService.resumeItemsResult = JellyfinResult.Success(
        ItemsResult(items = emptyList(), totalRecordCount = 0),
      )

      model.refresh()

      model.stateForTest.shouldBeInstanceOf<ContinueWatchingState.Empty>()
    }
  }

  @Test
  fun refresh_with_error_returns_error_state() {
    runTest {
      fakeLibraryService.resumeItemsResult = JellyfinResult.Error(
        message = "Network error",
        isEphemeral = true,
      )

      model.refresh()

      model.stateForTest.shouldBeInstanceOf<ContinueWatchingState.Error>()
    }
  }

  @Test
  fun items_without_id_are_filtered_out() {
    runTest {
      fakeLibraryService.resumeItemsResult = JellyfinResult.Success(
        ItemsResult(
          items = listOf(
            createBaseItemDto(
              id = null,
              name = "No ID Item",
              type = "Movie",
            ),
            createBaseItemDto(
              id = "valid-id",
              name = "Valid Item",
              type = "Movie",
            ),
          ),
          totalRecordCount = 2,
        ),
      )

      model.refresh()

      val state = model.stateForTest
      val loaded = state.shouldBeInstanceOf<ContinueWatchingState.Loaded>()
      loaded.items.size shouldBe 1
      loaded.items[0].id shouldBe "valid-id"
    }
  }

  @Test
  fun progress_is_clamped_between_0_and_1() {
    runTest {
      fakeLibraryService.resumeItemsResult = JellyfinResult.Success(
        ItemsResult(
          items = listOf(
            createBaseItemDto(
              id = "item-1",
              name = "Item",
              type = "Movie",
              runTimeTicks = 100L,
              playbackPositionTicks = 200L,
            ),
          ),
          totalRecordCount = 1,
        ),
      )

      model.refresh()

      val loaded = model.stateForTest.shouldBeInstanceOf<ContinueWatchingState.Loaded>()
      loaded.items[0].progressPercent shouldBe 1F
    }
  }

  @Test
  fun zero_runtime_gives_zero_progress() {
    runTest {
      fakeLibraryService.resumeItemsResult = JellyfinResult.Success(
        ItemsResult(
          items = listOf(
            createBaseItemDto(
              id = "item-1",
              name = "Item",
              type = "Movie",
              runTimeTicks = 0L,
              playbackPositionTicks = 50L,
            ),
          ),
          totalRecordCount = 1,
        ),
      )

      model.refresh()

      val loaded = model.stateForTest.shouldBeInstanceOf<ContinueWatchingState.Loaded>()
      loaded.items[0].progressPercent shouldBe 0F
    }
  }

  @Test
  fun episode_items_include_series_info() {
    runTest {
      fakeLibraryService.resumeItemsResult = JellyfinResult.Success(
        ItemsResult(
          items = listOf(
            createBaseItemDto(
              id = "ep-1",
              name = "Ozymandias",
              type = "Episode",
              seriesName = "Breaking Bad",
              seasonName = "Season 5",
              indexNumber = 14,
              parentIndexNumber = 5,
            ),
          ),
          totalRecordCount = 1,
        ),
      )

      model.refresh()

      val loaded = model.stateForTest.shouldBeInstanceOf<ContinueWatchingState.Loaded>()
      val item = loaded.items[0]
      item.seriesName shouldBe "Breaking Bad"
      item.seasonName shouldBe "Season 5"
      item.indexNumber shouldBe 14
      item.parentIndexNumber shouldBe 5
    }
  }

  @Test
  fun all_items_without_id_results_in_empty_state() {
    runTest {
      fakeLibraryService.resumeItemsResult = JellyfinResult.Success(
        ItemsResult(
          items = listOf(
            createBaseItemDto(id = null, name = "Item 1", type = "Movie"),
            createBaseItemDto(id = null, name = "Item 2", type = "Movie"),
          ),
          totalRecordCount = 2,
        ),
      )

      model.refresh()

      model.stateForTest.shouldBeInstanceOf<ContinueWatchingState.Empty>()
    }
  }

  private fun createBaseItemDto(
    id: String?,
    name: String,
    type: String,
    runTimeTicks: Long? = null,
    playbackPositionTicks: Long = 0L,
    seriesName: String? = null,
    seasonName: String? = null,
    indexNumber: Int? = null,
    parentIndexNumber: Int? = null,
  ) = BaseItemDto(
    id = id,
    name = name,
    type = type,
    runTimeTicks = runTimeTicks,
    userData = UserItemDataDto(
      playbackPositionTicks = playbackPositionTicks,
    ),
    seriesName = seriesName,
    seasonName = seasonName,
    indexNumber = indexNumber,
    parentIndexNumber = parentIndexNumber,
  )
}

private class FakeJellyfinLibraryService : JellyfinLibraryService {
  var resumeItemsResult: JellyfinResult<ItemsResult> = JellyfinResult.Success(
    ItemsResult(items = emptyList(), totalRecordCount = 0),
  )

  var latestItemsResult: JellyfinResult<List<BaseItemDto>> = JellyfinResult.Success(emptyList())

  override suspend fun getResumeItems(
    limit: Int?,
    mediaTypes: List<String>?,
    fields: List<String>?,
  ): JellyfinResult<ItemsResult> = resumeItemsResult

  override suspend fun getLatestItems(
    parentId: String?,
    includeItemTypes: List<String>?,
    limit: Int?,
    fields: List<String>?,
  ): JellyfinResult<List<BaseItemDto>> = latestItemsResult

  override fun getImageUrl(
    itemId: String,
    imageType: ImageType,
    maxWidth: Int?,
    maxHeight: Int?,
    tag: String?,
    imageIndex: Int?,
  ): String = "https://example.com/images/$itemId/${imageType.apiValue}"
}
