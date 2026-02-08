package com.eygraber.jellyfin.screens.library.tvshows

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eygraber.jellyfin.screens.library.tvshows.model.TvShowsLibraryModel
import com.eygraber.jellyfin.screens.library.tvshows.model.TvShowsLibraryModelError
import com.eygraber.jellyfin.ui.library.controls.LibrarySortConfig
import com.eygraber.jellyfin.ui.library.controls.LibraryViewMode
import com.eygraber.vice.ViceCompositor
import dev.zacsweers.metro.Inject

@Inject
class TvShowsLibraryCompositor(
  private val key: TvShowsLibraryKey,
  private val navigator: TvShowsLibraryNavigator,
  private val tvShowsModel: TvShowsLibraryModel,
) : ViceCompositor<TvShowsLibraryIntent, TvShowsLibraryViewState> {
  private var isFilterSheetVisible by mutableStateOf(false)
  private var viewMode by mutableStateOf(LibraryViewMode.Grid)

  @Composable
  override fun composite(): TvShowsLibraryViewState {
    val modelState = tvShowsModel.currentState()

    LaunchedEffect(Unit) {
      tvShowsModel.loadInitial(key.libraryId)
      tvShowsModel.loadAvailableFilters(key.libraryId)
    }

    return TvShowsLibraryViewState(
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

  override suspend fun onIntent(intent: TvShowsLibraryIntent) {
    when(intent) {
      TvShowsLibraryIntent.LoadMore -> tvShowsModel.loadMore(key.libraryId)
      TvShowsLibraryIntent.Refresh -> tvShowsModel.refresh(key.libraryId)
      TvShowsLibraryIntent.RetryLoad -> tvShowsModel.loadInitial(key.libraryId)
      is TvShowsLibraryIntent.SelectShow -> navigator.navigateToShowSeasons(intent.showId)

      is TvShowsLibraryIntent.ChangeSortOption -> {
        tvShowsModel.updateSortConfig(
          LibrarySortConfig(sortBy = intent.sortBy, sortOrder = intent.sortOrder),
        )
        tvShowsModel.loadInitial(key.libraryId)
      }

      is TvShowsLibraryIntent.ChangeFilters -> {
        tvShowsModel.updateFilters(intent.filters)
        tvShowsModel.loadInitial(key.libraryId)
      }

      is TvShowsLibraryIntent.ChangeViewMode ->
        viewMode = intent.viewMode

      TvShowsLibraryIntent.ToggleFilterSheet ->
        isFilterSheetVisible = !isFilterSheetVisible

      TvShowsLibraryIntent.NavigateBack -> navigator.navigateBack()
    }
  }

  private fun TvShowsLibraryModelError.toViewError(): TvShowsLibraryError = when(this) {
    TvShowsLibraryModelError.LoadFailed -> TvShowsLibraryError.Network()
  }
}
