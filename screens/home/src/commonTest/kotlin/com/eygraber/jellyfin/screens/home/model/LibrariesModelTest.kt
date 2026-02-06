package com.eygraber.jellyfin.screens.home.model

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.screens.home.CollectionType
import com.eygraber.jellyfin.screens.home.LibrariesState
import com.eygraber.jellyfin.sdk.core.model.BaseItemDto
import com.eygraber.jellyfin.sdk.core.model.ImageType
import com.eygraber.jellyfin.sdk.core.model.ItemsResult
import com.eygraber.jellyfin.services.sdk.JellyfinLibraryService
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class LibrariesModelTest {
  private lateinit var fakeLibraryService: FakeLibrariesLibraryService
  private lateinit var model: LibrariesModel

  @BeforeTest
  fun setUp() {
    fakeLibraryService = FakeLibrariesLibraryService()
    model = LibrariesModel(libraryService = fakeLibraryService)
  }

  @Test
  fun refresh_with_libraries_returns_loaded_state() {
    runTest {
      fakeLibraryService.userViewsResult = JellyfinResult.Success(
        ItemsResult(
          items = listOf(
            BaseItemDto(
              id = "lib-1",
              name = "Movies",
              collectionType = "movies",
              imageTags = mapOf("Primary" to "tag1"),
            ),
            BaseItemDto(
              id = "lib-2",
              name = "TV Shows",
              collectionType = "tvshows",
            ),
          ),
          totalRecordCount = 2,
        ),
      )

      model.refresh()

      val loaded = model.stateForTest.shouldBeInstanceOf<LibrariesState.Loaded>()
      loaded.libraries.size shouldBe 2
      loaded.libraries[0].id shouldBe "lib-1"
      loaded.libraries[0].name shouldBe "Movies"
      loaded.libraries[0].collectionType shouldBe CollectionType.Movies
      loaded.libraries[1].id shouldBe "lib-2"
      loaded.libraries[1].name shouldBe "TV Shows"
      loaded.libraries[1].collectionType shouldBe CollectionType.TvShows
    }
  }

  @Test
  fun refresh_with_empty_returns_empty_state() {
    runTest {
      fakeLibraryService.userViewsResult = JellyfinResult.Success(
        ItemsResult(items = emptyList(), totalRecordCount = 0),
      )

      model.refresh()

      model.stateForTest.shouldBeInstanceOf<LibrariesState.Empty>()
    }
  }

  @Test
  fun refresh_with_error_returns_error_state() {
    runTest {
      fakeLibraryService.userViewsResult = JellyfinResult.Error(
        message = "Server error",
        isEphemeral = true,
      )

      model.refresh()

      model.stateForTest.shouldBeInstanceOf<LibrariesState.Error>()
    }
  }

  @Test
  fun items_without_id_are_filtered_out() {
    runTest {
      fakeLibraryService.userViewsResult = JellyfinResult.Success(
        ItemsResult(
          items = listOf(
            BaseItemDto(id = null, name = "No ID Library"),
            BaseItemDto(id = "valid", name = "Valid Library", collectionType = "movies"),
          ),
          totalRecordCount = 2,
        ),
      )

      model.refresh()

      val loaded = model.stateForTest.shouldBeInstanceOf<LibrariesState.Loaded>()
      loaded.libraries.size shouldBe 1
      loaded.libraries[0].id shouldBe "valid"
    }
  }

  @Test
  fun unknown_collection_type_maps_to_unknown() {
    runTest {
      fakeLibraryService.userViewsResult = JellyfinResult.Success(
        ItemsResult(
          items = listOf(
            BaseItemDto(
              id = "lib-1",
              name = "Custom Library",
              collectionType = "somethingCustom",
            ),
          ),
          totalRecordCount = 1,
        ),
      )

      model.refresh()

      val loaded = model.stateForTest.shouldBeInstanceOf<LibrariesState.Loaded>()
      loaded.libraries[0].collectionType shouldBe CollectionType.Unknown
    }
  }

  @Test
  fun null_collection_type_maps_to_unknown() {
    runTest {
      fakeLibraryService.userViewsResult = JellyfinResult.Success(
        ItemsResult(
          items = listOf(
            BaseItemDto(
              id = "lib-1",
              name = "Generic Library",
              collectionType = null,
            ),
          ),
          totalRecordCount = 1,
        ),
      )

      model.refresh()

      val loaded = model.stateForTest.shouldBeInstanceOf<LibrariesState.Loaded>()
      loaded.libraries[0].collectionType shouldBe CollectionType.Unknown
    }
  }

  @Test
  fun library_with_image_tag_has_image_url() {
    runTest {
      fakeLibraryService.userViewsResult = JellyfinResult.Success(
        ItemsResult(
          items = listOf(
            BaseItemDto(
              id = "lib-1",
              name = "Movies",
              collectionType = "movies",
              imageTags = mapOf("Primary" to "abc123"),
            ),
          ),
          totalRecordCount = 1,
        ),
      )

      model.refresh()

      val loaded = model.stateForTest.shouldBeInstanceOf<LibrariesState.Loaded>()
      loaded.libraries[0].imageUrl shouldBe "https://example.com/images/lib-1/Primary"
    }
  }

  @Test
  fun library_without_image_tag_has_null_image_url() {
    runTest {
      fakeLibraryService.userViewsResult = JellyfinResult.Success(
        ItemsResult(
          items = listOf(
            BaseItemDto(
              id = "lib-1",
              name = "Movies",
              collectionType = "movies",
            ),
          ),
          totalRecordCount = 1,
        ),
      )

      model.refresh()

      val loaded = model.stateForTest.shouldBeInstanceOf<LibrariesState.Loaded>()
      loaded.libraries[0].imageUrl shouldBe null
    }
  }
}

private class FakeLibrariesLibraryService : JellyfinLibraryService {
  var userViewsResult: JellyfinResult<ItemsResult> = JellyfinResult.Success(
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
  ): JellyfinResult<ItemsResult> = JellyfinResult.Success(
    ItemsResult(items = emptyList(), totalRecordCount = 0),
  )

  override suspend fun getLatestItems(
    parentId: String?,
    includeItemTypes: List<String>?,
    limit: Int?,
    fields: List<String>?,
  ): JellyfinResult<List<BaseItemDto>> = JellyfinResult.Success(emptyList())

  override suspend fun getUserViews(): JellyfinResult<ItemsResult> = userViewsResult

  override fun getImageUrl(
    itemId: String,
    imageType: ImageType,
    maxWidth: Int?,
    maxHeight: Int?,
    tag: String?,
    imageIndex: Int?,
  ): String = "https://example.com/images/$itemId/${imageType.apiValue}"
}
