package com.eygraber.jellyfin.screens.home.model

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.screens.home.RecentlyAddedState
import com.eygraber.jellyfin.sdk.core.model.BaseItemDto
import com.eygraber.jellyfin.sdk.core.model.ImageType
import com.eygraber.jellyfin.sdk.core.model.ItemsResult
import com.eygraber.jellyfin.services.sdk.JellyfinLibraryService
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class RecentlyAddedModelTest {
  private lateinit var fakeLibraryService: FakeRecentLibraryService
  private lateinit var model: RecentlyAddedModel

  @BeforeTest
  fun setUp() {
    fakeLibraryService = FakeRecentLibraryService()
    model = RecentlyAddedModel(libraryService = fakeLibraryService)
  }

  @Test
  fun refresh_with_items_returns_loaded_state() {
    runTest {
      fakeLibraryService.latestItemsResult = JellyfinResult.Success(
        listOf(
          BaseItemDto(
            id = "movie-1",
            name = "Inception",
            type = "Movie",
            productionYear = 2010,
          ),
        ),
      )

      model.refresh()

      val loaded = model.stateForTest.shouldBeInstanceOf<RecentlyAddedState.Loaded>()
      loaded.items.size shouldBe 1
      loaded.items[0].id shouldBe "movie-1"
      loaded.items[0].name shouldBe "Inception"
      loaded.items[0].productionYear shouldBe 2010
    }
  }

  @Test
  fun refresh_with_empty_returns_empty_state() {
    runTest {
      fakeLibraryService.latestItemsResult = JellyfinResult.Success(emptyList())

      model.refresh()

      model.stateForTest.shouldBeInstanceOf<RecentlyAddedState.Empty>()
    }
  }

  @Test
  fun refresh_with_error_returns_error_state() {
    runTest {
      fakeLibraryService.latestItemsResult = JellyfinResult.Error(
        message = "Server error",
        isEphemeral = true,
      )

      model.refresh()

      model.stateForTest.shouldBeInstanceOf<RecentlyAddedState.Error>()
    }
  }

  @Test
  fun items_without_id_are_filtered_out() {
    runTest {
      fakeLibraryService.latestItemsResult = JellyfinResult.Success(
        listOf(
          BaseItemDto(id = null, name = "No ID"),
          BaseItemDto(id = "valid", name = "Valid Movie", type = "Movie"),
        ),
      )

      model.refresh()

      val loaded = model.stateForTest.shouldBeInstanceOf<RecentlyAddedState.Loaded>()
      loaded.items.size shouldBe 1
      loaded.items[0].id shouldBe "valid"
    }
  }

  @Test
  fun episode_items_include_series_name() {
    runTest {
      fakeLibraryService.latestItemsResult = JellyfinResult.Success(
        listOf(
          BaseItemDto(
            id = "ep-1",
            name = "Pilot",
            type = "Episode",
            seriesName = "Lost",
          ),
        ),
      )

      model.refresh()

      val loaded = model.stateForTest.shouldBeInstanceOf<RecentlyAddedState.Loaded>()
      loaded.items[0].seriesName shouldBe "Lost"
    }
  }
}

private class FakeRecentLibraryService : JellyfinLibraryService {
  var latestItemsResult: JellyfinResult<List<BaseItemDto>> = JellyfinResult.Success(emptyList())

  override suspend fun getResumeItems(
    limit: Int?,
    mediaTypes: List<String>?,
    fields: List<String>?,
  ): JellyfinResult<ItemsResult> = JellyfinResult.Success(
    ItemsResult(items = emptyList(), totalRecordCount = 0),
  )

  override suspend fun getNextUpEpisodes(
    limit: Int?,
    fields: List<String>?,
  ): JellyfinResult<ItemsResult> = JellyfinResult.Success(
    ItemsResult(items = emptyList(), totalRecordCount = 0),
  )

  override suspend fun getLatestItems(
    parentId: String?,
    includeItemTypes: List<String>?,
    limit: Int?,
    fields: List<String>?,
  ): JellyfinResult<List<BaseItemDto>> = latestItemsResult

  override suspend fun getUserViews(): JellyfinResult<ItemsResult> = JellyfinResult.Success(
    ItemsResult(items = emptyList(), totalRecordCount = 0),
  )

  override fun getImageUrl(
    itemId: String,
    imageType: ImageType,
    maxWidth: Int?,
    maxHeight: Int?,
    tag: String?,
    imageIndex: Int?,
  ): String = "https://example.com/images/$itemId/${imageType.apiValue}"
}
