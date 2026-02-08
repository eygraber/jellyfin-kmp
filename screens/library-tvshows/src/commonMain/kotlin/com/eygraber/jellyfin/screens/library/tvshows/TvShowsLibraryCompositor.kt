package com.eygraber.jellyfin.screens.library.tvshows

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.eygraber.jellyfin.screens.library.tvshows.model.TvShowsLibraryModel
import com.eygraber.jellyfin.screens.library.tvshows.model.TvShowsLibraryModelError
import com.eygraber.vice.ViceCompositor
import dev.zacsweers.metro.Inject

@Inject
class TvShowsLibraryCompositor(
  private val key: TvShowsLibraryKey,
  private val navigator: TvShowsLibraryNavigator,
  private val tvShowsModel: TvShowsLibraryModel,
) : ViceCompositor<TvShowsLibraryIntent, TvShowsLibraryViewState> {

  @Composable
  override fun composite(): TvShowsLibraryViewState {
    val modelState = tvShowsModel.currentState()

    LaunchedEffect(Unit) {
      tvShowsModel.loadInitial(key.libraryId)
    }

    return TvShowsLibraryViewState(
      items = modelState.items,
      isLoading = modelState.isLoading,
      isLoadingMore = modelState.isLoadingMore,
      error = modelState.error?.toViewError(),
      hasMore = modelState.hasMore,
      isEmpty = !modelState.isLoading && modelState.error == null && modelState.items.isEmpty(),
    )
  }

  override suspend fun onIntent(intent: TvShowsLibraryIntent) {
    when(intent) {
      TvShowsLibraryIntent.LoadMore -> tvShowsModel.loadMore(key.libraryId)
      TvShowsLibraryIntent.Refresh -> tvShowsModel.refresh(key.libraryId)
      TvShowsLibraryIntent.RetryLoad -> tvShowsModel.loadInitial(key.libraryId)
      is TvShowsLibraryIntent.SelectShow -> navigator.navigateToShowSeasons(intent.showId)
      TvShowsLibraryIntent.NavigateBack -> navigator.navigateBack()
    }
  }

  private fun TvShowsLibraryModelError.toViewError(): TvShowsLibraryError = when(this) {
    TvShowsLibraryModelError.LoadFailed -> TvShowsLibraryError.Network()
  }
}
