package com.eygraber.jellyfin.screens.movie.detail.model

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

class MovieDetailModelTest {
  private lateinit var fakeRepository: FakeItemsRepository
  private lateinit var fakeLibraryService: FakeLibraryService
  private lateinit var model: MovieDetailModel

  @BeforeTest
  fun setUp() {
    fakeRepository = FakeItemsRepository()
    fakeLibraryService = FakeLibraryService()
    model = MovieDetailModel(
      itemsRepository = fakeRepository,
      libraryService = fakeLibraryService,
    )
  }

  @Test
  fun loadMovie_maps_item_to_movie_detail() {
    runTest {
      fakeRepository.getItemResult = JellyfinResult.Success(
        createLibraryItem(
          id = "movie-1",
          name = "Inception",
          overview = "A thief who steals corporate secrets",
          productionYear = 2010,
          communityRating = 8.8F,
          officialRating = "PG-13",
          runTimeTicks = 88_800_000_000L,
          primaryImageTag = "poster-tag",
          backdropImageTags = listOf("backdrop-tag"),
        ),
      )

      model.loadMovie(movieId = "movie-1")

      val state = model.stateForTest
      state.isLoading.shouldBeFalse()
      state.error.shouldBeNull()

      val movie = state.movie
      movie.shouldNotBeNull()
      movie.id shouldBe "movie-1"
      movie.name shouldBe "Inception"
      movie.overview shouldBe "A thief who steals corporate secrets"
      movie.productionYear shouldBe 2010
      movie.communityRating shouldBe 8.8F
      movie.officialRating shouldBe "PG-13"
      movie.runtimeMinutes shouldBe 148
      movie.posterImageUrl.shouldNotBeNull()
      movie.backdropImageUrl.shouldNotBeNull()
    }
  }

  @Test
  fun loadMovie_with_error_sets_error_state() {
    runTest {
      fakeRepository.getItemResult = JellyfinResult.Error(
        message = "Server error",
        isEphemeral = true,
      )

      model.loadMovie(movieId = "movie-1")

      val state = model.stateForTest
      state.isLoading.shouldBeFalse()
      state.error shouldBe MovieDetailModelError.LoadFailed
      state.movie.shouldBeNull()
    }
  }

  @Test
  fun loadMovie_without_image_tags_has_null_urls() {
    runTest {
      fakeRepository.getItemResult = JellyfinResult.Success(
        createLibraryItem(
          id = "movie-1",
          name = "No Images Movie",
          primaryImageTag = null,
          backdropImageTags = emptyList(),
        ),
      )

      model.loadMovie(movieId = "movie-1")

      val state = model.stateForTest
      val movie = state.movie
      movie.shouldNotBeNull()
      movie.posterImageUrl.shouldBeNull()
      movie.backdropImageUrl.shouldBeNull()
    }
  }

  @Test
  fun loadMovie_without_runtime_has_null_minutes() {
    runTest {
      fakeRepository.getItemResult = JellyfinResult.Success(
        createLibraryItem(
          id = "movie-1",
          name = "No Runtime Movie",
          runTimeTicks = null,
        ),
      )

      model.loadMovie(movieId = "movie-1")

      val state = model.stateForTest
      val movie = state.movie
      movie.shouldNotBeNull()
      movie.runtimeMinutes.shouldBeNull()
    }
  }

  @Test
  fun loadMovie_clears_error_on_retry() {
    runTest {
      fakeRepository.getItemResult = JellyfinResult.Error(
        message = "Server error",
        isEphemeral = true,
      )

      model.loadMovie(movieId = "movie-1")
      model.stateForTest.error shouldBe MovieDetailModelError.LoadFailed

      fakeRepository.getItemResult = JellyfinResult.Success(
        createLibraryItem(id = "movie-1", name = "Inception"),
      )

      model.loadMovie(movieId = "movie-1")

      val state = model.stateForTest
      state.error.shouldBeNull()
      state.movie.shouldNotBeNull()
      state.isLoading.shouldBeFalse()
    }
  }

  @Test
  fun initial_state_is_loading() {
    val state = model.stateForTest
    state.isLoading.shouldBeTrue()
    state.movie.shouldBeNull()
    state.error.shouldBeNull()
  }

  @Suppress("LongParameterList")
  private fun createLibraryItem(
    id: String,
    name: String,
    overview: String? = null,
    productionYear: Int? = null,
    communityRating: Float? = null,
    officialRating: String? = null,
    runTimeTicks: Long? = null,
    primaryImageTag: String? = null,
    backdropImageTags: List<String> = emptyList(),
  ) = LibraryItem(
    id = id,
    name = name,
    sortName = null,
    type = "Movie",
    overview = overview,
    productionYear = productionYear,
    communityRating = communityRating,
    officialRating = officialRating,
    primaryImageTag = primaryImageTag,
    backdropImageTags = backdropImageTags,
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
