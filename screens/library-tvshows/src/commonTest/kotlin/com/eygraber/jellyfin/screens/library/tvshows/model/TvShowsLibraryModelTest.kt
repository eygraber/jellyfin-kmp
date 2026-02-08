package com.eygraber.jellyfin.screens.library.tvshows.model

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

class TvShowsLibraryModelTest {
  private lateinit var fakeRepository: FakeItemsRepository
  private lateinit var fakeLibraryService: FakeTvShowsLibraryService
  private lateinit var model: TvShowsLibraryModel

  @BeforeTest
  fun setUp() {
    fakeRepository = FakeItemsRepository()
    fakeLibraryService = FakeTvShowsLibraryService()
    model = TvShowsLibraryModel(
      itemsRepository = fakeRepository,
      libraryService = fakeLibraryService,
    )
  }

  @Test
  fun loadInitial_maps_items_to_tv_show_items() {
    runTest {
      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(
          items = listOf(
            createLibraryItem(
              id = "show-1",
              name = "Breaking Bad",
              productionYear = 2008,
              communityRating = 9.5F,
              officialRating = "TV-MA",
              primaryImageTag = "tag1",
              childCount = 5,
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
      state.items.size shouldBe 1

      val show = state.items[0]
      show.id shouldBe "show-1"
      show.name shouldBe "Breaking Bad"
      show.productionYear shouldBe 2008
      show.communityRating shouldBe 9.5F
      show.officialRating shouldBe "TV-MA"
      show.seasonCount shouldBe 5
      show.imageUrl.shouldNotBeNull()
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
      state.error shouldBe TvShowsLibraryModelError.LoadFailed
      state.items.size shouldBe 0
    }
  }

  @Test
  fun loadInitial_with_empty_result_shows_empty_items() {
    runTest {
      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(
          items = emptyList(),
          totalRecordCount = 0,
          startIndex = 0,
        ),
      )

      model.loadInitial("lib-1")

      val state = model.stateForTest
      state.isLoading.shouldBeFalse()
      state.error.shouldBeNull()
      state.items.size shouldBe 0
      state.hasMore.shouldBeFalse()
    }
  }

  @Test
  fun loadMore_appends_items() {
    runTest {
      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(
          items = listOf(createLibraryItem(id = "show-1", name = "Show 1")),
          totalRecordCount = 2,
          startIndex = 0,
        ),
      )

      model.loadInitial("lib-1")

      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(
          items = listOf(createLibraryItem(id = "show-2", name = "Show 2")),
          totalRecordCount = 2,
          startIndex = 1,
        ),
      )

      model.loadMore("lib-1")

      val state = model.stateForTest
      state.items.size shouldBe 2
      state.items[0].id shouldBe "show-1"
      state.items[1].id shouldBe "show-2"
      state.hasMore.shouldBeFalse()
    }
  }

  @Test
  fun loadMore_does_nothing_when_no_more_items() {
    runTest {
      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(
          items = listOf(createLibraryItem(id = "show-1", name = "Show 1")),
          totalRecordCount = 1,
          startIndex = 0,
        ),
      )

      model.loadInitial("lib-1")

      model.loadMore("lib-1")

      val state = model.stateForTest
      state.items.size shouldBe 1
      state.isLoadingMore.shouldBeFalse()
    }
  }

  @Test
  fun show_without_image_tag_has_null_image_url() {
    runTest {
      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(
          items = listOf(createLibraryItem(id = "show-1", name = "Show 1", primaryImageTag = null)),
          totalRecordCount = 1,
          startIndex = 0,
        ),
      )

      model.loadInitial("lib-1")

      val state = model.stateForTest
      state.items[0].imageUrl.shouldBeNull()
    }
  }

  @Test
  fun refresh_reloads_items_from_beginning() {
    runTest {
      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(
          items = listOf(createLibraryItem(id = "show-1", name = "Show 1")),
          totalRecordCount = 1,
          startIndex = 0,
        ),
      )

      model.loadInitial("lib-1")

      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(
          items = listOf(
            createLibraryItem(id = "show-1", name = "Show 1"),
            createLibraryItem(id = "show-2", name = "Show 2"),
          ),
          totalRecordCount = 2,
          startIndex = 0,
        ),
      )

      model.refresh("lib-1")

      val state = model.stateForTest
      state.items.size shouldBe 2
    }
  }

  private fun createLibraryItem(
    id: String,
    name: String,
    productionYear: Int? = null,
    communityRating: Float? = null,
    officialRating: String? = null,
    primaryImageTag: String? = null,
    childCount: Int? = null,
  ) = LibraryItem(
    id = id,
    name = name,
    sortName = null,
    type = "Series",
    overview = null,
    productionYear = productionYear,
    communityRating = communityRating,
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

private class FakeTvShowsLibraryService : JellyfinLibraryService {
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
