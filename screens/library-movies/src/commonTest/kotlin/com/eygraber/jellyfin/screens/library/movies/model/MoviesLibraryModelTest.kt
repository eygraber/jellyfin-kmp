package com.eygraber.jellyfin.screens.library.movies.model

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

class MoviesLibraryModelTest {
  private lateinit var fakeRepository: FakeItemsRepository
  private lateinit var fakeLibraryService: FakeMoviesLibraryService
  private lateinit var model: MoviesLibraryModel

  @BeforeTest
  fun setUp() {
    fakeRepository = FakeItemsRepository()
    fakeLibraryService = FakeMoviesLibraryService()
    model = MoviesLibraryModel(
      itemsRepository = fakeRepository,
      libraryService = fakeLibraryService,
    )
  }

  @Test
  fun loadInitial_maps_items_to_movie_items() {
    runTest {
      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(
          items = listOf(
            createLibraryItem(
              id = "movie-1",
              name = "Inception",
              productionYear = 2010,
              communityRating = 8.8F,
              officialRating = "PG-13",
              primaryImageTag = "tag1",
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

      val movie = state.items[0]
      movie.id shouldBe "movie-1"
      movie.name shouldBe "Inception"
      movie.productionYear shouldBe 2010
      movie.communityRating shouldBe 8.8F
      movie.officialRating shouldBe "PG-13"
      movie.imageUrl.shouldNotBeNull()
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
      state.error shouldBe MoviesLibraryModelError.LoadFailed
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
  fun loadInitial_sets_hasMore_when_more_items_available() {
    runTest {
      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(
          items = List(50) { index ->
            createLibraryItem(id = "movie-$index", name = "Movie $index")
          },
          totalRecordCount = 100,
          startIndex = 0,
        ),
      )

      model.loadInitial("lib-1")

      val state = model.stateForTest
      state.hasMore.shouldBeTrue()
      state.items.size shouldBe 50
    }
  }

  @Test
  fun loadMore_appends_items() {
    runTest {
      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(
          items = listOf(createLibraryItem(id = "movie-1", name = "Movie 1")),
          totalRecordCount = 2,
          startIndex = 0,
        ),
      )

      model.loadInitial("lib-1")

      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(
          items = listOf(createLibraryItem(id = "movie-2", name = "Movie 2")),
          totalRecordCount = 2,
          startIndex = 1,
        ),
      )

      model.loadMore("lib-1")

      val state = model.stateForTest
      state.items.size shouldBe 2
      state.items[0].id shouldBe "movie-1"
      state.items[1].id shouldBe "movie-2"
      state.hasMore.shouldBeFalse()
    }
  }

  @Test
  fun loadMore_does_nothing_when_no_more_items() {
    runTest {
      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(
          items = listOf(createLibraryItem(id = "movie-1", name = "Movie 1")),
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
  fun movie_without_image_tag_has_null_image_url() {
    runTest {
      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(
          items = listOf(createLibraryItem(id = "movie-1", name = "Movie 1", primaryImageTag = null)),
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
          items = listOf(createLibraryItem(id = "movie-1", name = "Movie 1")),
          totalRecordCount = 1,
          startIndex = 0,
        ),
      )

      model.loadInitial("lib-1")

      fakeRepository.getItemsResult = JellyfinResult.Success(
        PaginatedResult(
          items = listOf(
            createLibraryItem(id = "movie-1", name = "Movie 1"),
            createLibraryItem(id = "movie-2", name = "Movie 2"),
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
  ) = LibraryItem(
    id = id,
    name = name,
    sortName = null,
    type = "Movie",
    overview = null,
    productionYear = productionYear,
    communityRating = communityRating,
    officialRating = officialRating,
    primaryImageTag = primaryImageTag,
    backdropImageTags = emptyList(),
    seriesName = null,
    seriesId = null,
    childCount = null,
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

private class FakeMoviesLibraryService : JellyfinLibraryService {
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
