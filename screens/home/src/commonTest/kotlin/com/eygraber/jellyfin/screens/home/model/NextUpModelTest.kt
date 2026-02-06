package com.eygraber.jellyfin.screens.home.model

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.screens.home.NextUpState
import com.eygraber.jellyfin.sdk.core.model.BaseItemDto
import com.eygraber.jellyfin.sdk.core.model.ImageType
import com.eygraber.jellyfin.sdk.core.model.ItemsResult
import com.eygraber.jellyfin.services.sdk.JellyfinLibraryService
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class NextUpModelTest {
  private lateinit var fakeLibraryService: FakeNextUpLibraryService
  private lateinit var model: NextUpModel

  @BeforeTest
  fun setUp() {
    fakeLibraryService = FakeNextUpLibraryService()
    model = NextUpModel(libraryService = fakeLibraryService)
  }

  @Test
  fun refresh_with_episodes_returns_loaded_state() {
    runTest {
      fakeLibraryService.nextUpResult = JellyfinResult.Success(
        ItemsResult(
          items = listOf(
            BaseItemDto(
              id = "ep-1",
              name = "The One Where They All Get Coffee",
              seriesName = "Friends",
              seasonName = "Season 3",
              indexNumber = 5,
              parentIndexNumber = 3,
            ),
          ),
          totalRecordCount = 1,
        ),
      )

      model.refresh()

      val loaded = model.stateForTest.shouldBeInstanceOf<NextUpState.Loaded>()
      loaded.items.size shouldBe 1
      loaded.items[0].id shouldBe "ep-1"
      loaded.items[0].seriesName shouldBe "Friends"
      loaded.items[0].indexNumber shouldBe 5
    }
  }

  @Test
  fun refresh_with_empty_returns_empty_state() {
    runTest {
      fakeLibraryService.nextUpResult = JellyfinResult.Success(
        ItemsResult(items = emptyList(), totalRecordCount = 0),
      )

      model.refresh()

      model.stateForTest.shouldBeInstanceOf<NextUpState.Empty>()
    }
  }

  @Test
  fun refresh_with_error_returns_error_state() {
    runTest {
      fakeLibraryService.nextUpResult = JellyfinResult.Error(
        message = "Server error",
        isEphemeral = true,
      )

      model.refresh()

      model.stateForTest.shouldBeInstanceOf<NextUpState.Error>()
    }
  }

  @Test
  fun items_without_id_are_filtered_out() {
    runTest {
      fakeLibraryService.nextUpResult = JellyfinResult.Success(
        ItemsResult(
          items = listOf(
            BaseItemDto(id = null, name = "No ID"),
            BaseItemDto(id = "valid", name = "Valid Episode"),
          ),
          totalRecordCount = 2,
        ),
      )

      model.refresh()

      val loaded = model.stateForTest.shouldBeInstanceOf<NextUpState.Loaded>()
      loaded.items.size shouldBe 1
      loaded.items[0].id shouldBe "valid"
    }
  }
}

private class FakeNextUpLibraryService : JellyfinLibraryService {
  var nextUpResult: JellyfinResult<ItemsResult> = JellyfinResult.Success(
    ItemsResult(items = emptyList(), totalRecordCount = 0),
  )

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
  ): JellyfinResult<ItemsResult> = nextUpResult

  override suspend fun getLatestItems(
    parentId: String?,
    includeItemTypes: List<String>?,
    limit: Int?,
    fields: List<String>?,
  ): JellyfinResult<List<BaseItemDto>> = JellyfinResult.Success(emptyList())

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
