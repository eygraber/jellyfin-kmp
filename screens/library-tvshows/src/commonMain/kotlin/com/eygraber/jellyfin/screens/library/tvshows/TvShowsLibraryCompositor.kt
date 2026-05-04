package com.eygraber.jellyfin.screens.library.tvshows

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eygraber.jellyfin.screens.library.tvshows.model.TvShowsLibraryModel
import com.eygraber.jellyfin.screens.library.tvshows.model.TvShowsLibraryModelError
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
class TvShowsLibraryCompositor(
  private val key: TvShowsLibraryKey,
  private val navigator: TvShowsLibraryNavigator,
  private val tvShowsModel: TvShowsLibraryModel,
) : ViceCompositor<TvShowsLibraryIntent, TvShowsLibraryViewState> {
  private var isFilterSheetVisible by mutableStateOf(false)
  private var viewMode by mutableStateOf(LibraryViewMode.Grid)

  // Sort/filter live in `composite()` via rememberSaveable so they survive composition disposal
  // when the user navigates into an item and back. `onIntent` writes through these channels and
  // the drain coroutines update the saved state inside the composition.
  private val sortMutations = Channel<LibrarySortConfig>(Channel.CONFLATED)
  private val filterMutations = Channel<LibraryFilters>(Channel.CONFLATED)

  @Volatile private var currentSortConfig: LibrarySortConfig = LibrarySortConfig()
  @Volatile private var currentFilters: LibraryFilters = LibraryFilters()

  @Composable
  override fun composite(): TvShowsLibraryViewState {
    var sortConfig by rememberLibrarySortConfig()
    var filters by rememberLibraryFilters()

    LaunchedEffect(Unit) {
      for(next in sortMutations) sortConfig = next
    }
    LaunchedEffect(Unit) {
      for(next in filterMutations) filters = next
    }

    val modelState = tvShowsModel.currentState()

    LaunchedEffect(Unit) {
      tvShowsModel.loadAvailableFilters(key.libraryId)
    }
    LaunchedEffect(sortConfig, filters) {
      currentSortConfig = sortConfig
      currentFilters = filters
      tvShowsModel.loadInitial(key.libraryId, sortConfig, filters)
    }

    return TvShowsLibraryViewState(
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

  override suspend fun onIntent(intent: TvShowsLibraryIntent) {
    when(intent) {
      TvShowsLibraryIntent.LoadMore -> tvShowsModel.loadMore(
        libraryId = key.libraryId,
        sortConfig = currentSortConfig,
        filters = currentFilters,
      )

      TvShowsLibraryIntent.Refresh -> tvShowsModel.loadInitial(
        libraryId = key.libraryId,
        sortConfig = currentSortConfig,
        filters = currentFilters,
      )

      TvShowsLibraryIntent.RetryLoad -> tvShowsModel.loadInitial(
        libraryId = key.libraryId,
        sortConfig = currentSortConfig,
        filters = currentFilters,
      )

      is TvShowsLibraryIntent.SelectShow -> navigator.navigateToShowSeasons(intent.showId)

      is TvShowsLibraryIntent.ChangeSortOption ->
        sortMutations.send(LibrarySortConfig(sortBy = intent.sortBy, sortOrder = intent.sortOrder))

      is TvShowsLibraryIntent.ChangeFilters ->
        filterMutations.send(intent.filters)

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
