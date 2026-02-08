package com.eygraber.jellyfin.screens.library.movies.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.data.items.ItemSortBy
import com.eygraber.jellyfin.data.items.ItemsRepository
import com.eygraber.jellyfin.data.items.LibraryItem
import com.eygraber.jellyfin.data.items.SortOrder
import com.eygraber.jellyfin.screens.library.movies.MovieItem
import com.eygraber.jellyfin.sdk.core.model.ImageType
import com.eygraber.jellyfin.services.sdk.JellyfinLibraryService
import com.eygraber.jellyfin.ui.library.controls.LibraryFilters
import com.eygraber.jellyfin.ui.library.controls.LibrarySortConfig
import com.eygraber.vice.ViceSource
import dev.zacsweers.metro.Inject

data class MoviesLibraryState(
  val items: List<MovieItem> = emptyList(),
  val isLoading: Boolean = true,
  val isLoadingMore: Boolean = false,
  val hasMore: Boolean = false,
  val error: MoviesLibraryModelError? = null,
  val sortConfig: LibrarySortConfig = LibrarySortConfig(),
  val filters: LibraryFilters = LibraryFilters(),
  val availableGenres: List<String> = emptyList(),
  val availableYears: List<Int> = emptyList(),
)

enum class MoviesLibraryModelError {
  LoadFailed,
}

@Inject
class MoviesLibraryModel(
  private val itemsRepository: ItemsRepository,
  private val libraryService: JellyfinLibraryService,
) : ViceSource<MoviesLibraryState> {
  private var state by mutableStateOf(MoviesLibraryState())
  private var currentStartIndex = 0

  internal val stateForTest: MoviesLibraryState get() = state

  @Composable
  override fun currentState(): MoviesLibraryState = state

  suspend fun loadInitial(libraryId: String) {
    currentStartIndex = 0
    state = state.copy(isLoading = true, error = null, items = emptyList())

    val result = itemsRepository.getItems(
      parentId = libraryId,
      includeItemTypes = listOf("Movie"),
      sortBy = state.sortConfig.sortBy,
      sortOrder = state.sortConfig.sortOrder,
      startIndex = 0,
      limit = PAGE_SIZE,
      genres = state.filters.genres.ifEmpty { null },
      years = state.filters.years.ifEmpty { null },
    )

    state = if(result.isSuccess()) {
      val paginatedResult = result.value
      val movieItems = paginatedResult.items.map { item -> item.toMovieItem() }
      currentStartIndex = movieItems.size

      state.copy(
        items = movieItems,
        isLoading = false,
        hasMore = paginatedResult.hasMore,
        error = null,
      )
    }
    else {
      state.copy(
        isLoading = false,
        error = MoviesLibraryModelError.LoadFailed,
      )
    }
  }

  suspend fun loadMore(libraryId: String) {
    if(state.isLoadingMore || !state.hasMore) return

    state = state.copy(isLoadingMore = true)

    val result = itemsRepository.getItems(
      parentId = libraryId,
      includeItemTypes = listOf("Movie"),
      sortBy = state.sortConfig.sortBy,
      sortOrder = state.sortConfig.sortOrder,
      startIndex = currentStartIndex,
      limit = PAGE_SIZE,
      genres = state.filters.genres.ifEmpty { null },
      years = state.filters.years.ifEmpty { null },
    )

    state = if(result.isSuccess()) {
      val paginatedResult = result.value
      val newItems = paginatedResult.items.map { item -> item.toMovieItem() }
      currentStartIndex += newItems.size

      state.copy(
        items = state.items + newItems,
        isLoadingMore = false,
        hasMore = paginatedResult.hasMore,
      )
    }
    else {
      state.copy(isLoadingMore = false)
    }
  }

  suspend fun refresh(libraryId: String) {
    loadInitial(libraryId)
  }

  fun updateSortConfig(sortConfig: LibrarySortConfig) {
    state = state.copy(sortConfig = sortConfig)
  }

  fun updateFilters(filters: LibraryFilters) {
    state = state.copy(filters = filters)
  }

  suspend fun loadAvailableFilters(libraryId: String) {
    val genresResult = itemsRepository.getItems(
      parentId = libraryId,
      includeItemTypes = listOf("Genre"),
      sortBy = ItemSortBy.SortName,
      sortOrder = SortOrder.Ascending,
      limit = FILTER_LIMIT,
    )

    if(genresResult.isSuccess()) {
      state = state.copy(
        availableGenres = genresResult.value.items.map { it.name },
      )
    }
  }

  private fun LibraryItem.toMovieItem(): MovieItem = MovieItem(
    id = id,
    name = name,
    productionYear = productionYear,
    communityRating = communityRating,
    officialRating = officialRating,
    imageUrl = primaryImageTag?.let { tag ->
      libraryService.getImageUrl(
        itemId = id,
        imageType = ImageType.Primary,
        maxWidth = POSTER_MAX_WIDTH,
        tag = tag,
      )
    },
  )

  companion object {
    private const val PAGE_SIZE = 50
    private const val POSTER_MAX_WIDTH = 300
    private const val FILTER_LIMIT = 200
  }
}
