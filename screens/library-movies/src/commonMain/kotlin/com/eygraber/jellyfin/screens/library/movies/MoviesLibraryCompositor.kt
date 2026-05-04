package com.eygraber.jellyfin.screens.library.movies

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eygraber.jellyfin.screens.library.movies.model.MoviesLibraryModel
import com.eygraber.jellyfin.screens.library.movies.model.MoviesLibraryModelError
import com.eygraber.jellyfin.ui.library.controls.LibraryFilters
import com.eygraber.jellyfin.ui.library.controls.LibrarySortConfig
import com.eygraber.jellyfin.ui.library.controls.LibraryViewMode
import com.eygraber.jellyfin.ui.library.controls.rememberLibraryFilters
import com.eygraber.jellyfin.ui.library.controls.rememberLibrarySortConfig
import com.eygraber.vice.ViceCompositor
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.channels.Channel
import kotlin.concurrent.Volatile

@Inject
class MoviesLibraryCompositor(
  private val key: MoviesLibraryKey,
  private val navigator: MoviesLibraryNavigator,
  private val moviesModel: MoviesLibraryModel,
) : ViceCompositor<MoviesLibraryIntent, MoviesLibraryViewState> {
  private var isFilterSheetVisible by mutableStateOf(false)
  private var viewMode by mutableStateOf(LibraryViewMode.Grid)

  // Sort/filter live in `composite()` via rememberSaveable so they survive composition disposal
  // when the user navigates into an item and back. `onIntent` writes through these channels and
  // the drain coroutines update the saved state inside the composition.
  private val sortMutations = Channel<LibrarySortConfig>(Channel.CONFLATED)
  private val filterMutations = Channel<LibraryFilters>(Channel.CONFLATED)

  // Plain mirrors of the latest sort/filter, kept in sync from within `composite()` so non-Compose
  // callers (Refresh / RetryLoad / LoadMore intents) can read the current values without observers.
  @Volatile private var currentSortConfig: LibrarySortConfig = LibrarySortConfig()
  @Volatile private var currentFilters: LibraryFilters = LibraryFilters()

  @Composable
  override fun composite(): MoviesLibraryViewState {
    var sortConfig by rememberLibrarySortConfig()
    var filters by rememberLibraryFilters()

    LaunchedEffect(Unit) {
      for(next in sortMutations) sortConfig = next
    }
    LaunchedEffect(Unit) {
      for(next in filterMutations) filters = next
    }

    val modelState = moviesModel.currentState()

    LaunchedEffect(Unit) {
      moviesModel.loadAvailableFilters(key.libraryId)
    }
    LaunchedEffect(sortConfig, filters) {
      currentSortConfig = sortConfig
      currentFilters = filters
      moviesModel.loadInitial(key.libraryId, sortConfig, filters)
    }

    return MoviesLibraryViewState(
      items = modelState.items,
      isLoading = modelState.isLoading,
      isLoadingMore = modelState.isLoadingMore,
      error = modelState.error?.toViewError(),
      hasMore = modelState.hasMore,
      isEmpty = !modelState.isLoading && modelState.error == null && modelState.items.isEmpty(),
      sortConfig = sortConfig,
      filters = filters,
      viewMode = viewMode,
      availableGenres = modelState.availableGenres,
      availableYears = modelState.availableYears,
      isFilterSheetVisible = isFilterSheetVisible,
    )
  }

  override suspend fun onIntent(intent: MoviesLibraryIntent) {
    when(intent) {
      MoviesLibraryIntent.LoadMore -> moviesModel.loadMore(
        libraryId = key.libraryId,
        sortConfig = currentSortConfig,
        filters = currentFilters,
      )

      MoviesLibraryIntent.Refresh -> moviesModel.loadInitial(
        libraryId = key.libraryId,
        sortConfig = currentSortConfig,
        filters = currentFilters,
      )

      MoviesLibraryIntent.RetryLoad -> moviesModel.loadInitial(
        libraryId = key.libraryId,
        sortConfig = currentSortConfig,
        filters = currentFilters,
      )

      is MoviesLibraryIntent.SelectMovie -> navigator.navigateToMovieDetail(intent.movieId)

      is MoviesLibraryIntent.ChangeSortOption ->
        sortMutations.send(LibrarySortConfig(sortBy = intent.sortBy, sortOrder = intent.sortOrder))

      is MoviesLibraryIntent.ChangeFilters ->
        filterMutations.send(intent.filters)

      is MoviesLibraryIntent.ChangeViewMode ->
        viewMode = intent.viewMode

      MoviesLibraryIntent.ToggleFilterSheet ->
        isFilterSheetVisible = !isFilterSheetVisible

      MoviesLibraryIntent.NavigateBack -> navigator.navigateBack()
    }
  }

  private fun MoviesLibraryModelError.toViewError(): MoviesLibraryError = when(this) {
    MoviesLibraryModelError.LoadFailed -> MoviesLibraryError.Network()
  }
}
