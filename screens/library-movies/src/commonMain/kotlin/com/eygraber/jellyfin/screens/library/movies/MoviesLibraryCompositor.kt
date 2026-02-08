package com.eygraber.jellyfin.screens.library.movies

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.eygraber.jellyfin.screens.library.movies.model.MoviesLibraryModel
import com.eygraber.jellyfin.screens.library.movies.model.MoviesLibraryModelError
import com.eygraber.vice.ViceCompositor
import dev.zacsweers.metro.Inject

@Inject
class MoviesLibraryCompositor(
  private val key: MoviesLibraryKey,
  private val navigator: MoviesLibraryNavigator,
  private val moviesModel: MoviesLibraryModel,
) : ViceCompositor<MoviesLibraryIntent, MoviesLibraryViewState> {

  @Composable
  override fun composite(): MoviesLibraryViewState {
    val modelState = moviesModel.currentState()

    LaunchedEffect(Unit) {
      moviesModel.loadInitial(key.libraryId)
    }

    return MoviesLibraryViewState(
      items = modelState.items,
      isLoading = modelState.isLoading,
      isLoadingMore = modelState.isLoadingMore,
      error = modelState.error?.toViewError(),
      hasMore = modelState.hasMore,
      isEmpty = !modelState.isLoading && modelState.error == null && modelState.items.isEmpty(),
    )
  }

  override suspend fun onIntent(intent: MoviesLibraryIntent) {
    when(intent) {
      MoviesLibraryIntent.LoadMore -> moviesModel.loadMore(key.libraryId)
      MoviesLibraryIntent.Refresh -> moviesModel.refresh(key.libraryId)
      MoviesLibraryIntent.RetryLoad -> moviesModel.loadInitial(key.libraryId)
      is MoviesLibraryIntent.SelectMovie -> navigator.navigateToMovieDetail(intent.movieId)
      MoviesLibraryIntent.NavigateBack -> navigator.navigateBack()
    }
  }

  private fun MoviesLibraryModelError.toViewError(): MoviesLibraryError = when(this) {
    MoviesLibraryModelError.LoadFailed -> MoviesLibraryError.Network()
  }
}
