package com.eygraber.jellyfin.screens.library.movies

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eygraber.jellyfin.screens.library.movies.model.MoviesLibraryModel
import com.eygraber.jellyfin.screens.library.movies.model.MoviesLibraryModelError
import com.eygraber.jellyfin.ui.library.controls.LibrarySortConfig
import com.eygraber.jellyfin.ui.library.controls.LibraryViewMode
import com.eygraber.vice.ViceCompositor
import dev.zacsweers.metro.Inject

@Inject
class MoviesLibraryCompositor(
  private val key: MoviesLibraryKey,
  private val navigator: MoviesLibraryNavigator,
  private val moviesModel: MoviesLibraryModel,
) : ViceCompositor<MoviesLibraryIntent, MoviesLibraryViewState> {
  private var isFilterSheetVisible by mutableStateOf(false)
  private var viewMode by mutableStateOf(LibraryViewMode.Grid)

  @Composable
  override fun composite(): MoviesLibraryViewState {
    val modelState = moviesModel.currentState()

    LaunchedEffect(Unit) {
      moviesModel.loadInitial(key.libraryId)
      moviesModel.loadAvailableFilters(key.libraryId)
    }

    return MoviesLibraryViewState(
      items = modelState.items,
      isLoading = modelState.isLoading,
      isLoadingMore = modelState.isLoadingMore,
      error = modelState.error?.toViewError(),
      hasMore = modelState.hasMore,
      isEmpty = !modelState.isLoading && modelState.error == null && modelState.items.isEmpty(),
      sortConfig = modelState.sortConfig,
      filters = modelState.filters,
      viewMode = viewMode,
      availableGenres = modelState.availableGenres,
      availableYears = modelState.availableYears,
      isFilterSheetVisible = isFilterSheetVisible,
    )
  }

  override suspend fun onIntent(intent: MoviesLibraryIntent) {
    when(intent) {
      MoviesLibraryIntent.LoadMore -> moviesModel.loadMore(key.libraryId)
      MoviesLibraryIntent.Refresh -> moviesModel.refresh(key.libraryId)
      MoviesLibraryIntent.RetryLoad -> moviesModel.loadInitial(key.libraryId)
      is MoviesLibraryIntent.SelectMovie -> navigator.navigateToMovieDetail(intent.movieId)

      is MoviesLibraryIntent.ChangeSortOption -> {
        moviesModel.updateSortConfig(
          LibrarySortConfig(sortBy = intent.sortBy, sortOrder = intent.sortOrder),
        )
        moviesModel.loadInitial(key.libraryId)
      }

      is MoviesLibraryIntent.ChangeFilters -> {
        moviesModel.updateFilters(intent.filters)
        moviesModel.loadInitial(key.libraryId)
      }

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
