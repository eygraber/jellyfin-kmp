package com.eygraber.jellyfin.screens.movie.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.eygraber.jellyfin.screens.movie.detail.model.MovieDetailModel
import com.eygraber.jellyfin.screens.movie.detail.model.MovieDetailModelError
import com.eygraber.vice.ViceCompositor
import dev.zacsweers.metro.Inject

@Inject
class MovieDetailCompositor(
  private val key: MovieDetailKey,
  private val navigator: MovieDetailNavigator,
  private val movieModel: MovieDetailModel,
) : ViceCompositor<MovieDetailIntent, MovieDetailViewState> {

  @Composable
  override fun composite(): MovieDetailViewState {
    val modelState = movieModel.currentState()

    LaunchedEffect(Unit) {
      movieModel.loadMovie(key.movieId)
    }

    return MovieDetailViewState(
      movie = modelState.movie,
      cast = modelState.cast,
      crew = modelState.crew,
      similarItems = modelState.similarItems,
      isLoading = modelState.isLoading,
      error = modelState.error?.toViewError(),
    )
  }

  override suspend fun onIntent(intent: MovieDetailIntent) {
    when(intent) {
      MovieDetailIntent.RetryLoad -> movieModel.loadMovie(key.movieId)
      is MovieDetailIntent.SelectSimilarItem -> navigator.navigateToSimilarItem(intent.itemId)
      MovieDetailIntent.NavigateBack -> navigator.navigateBack()
    }
  }

  private fun MovieDetailModelError.toViewError(): MovieDetailError = when(this) {
    MovieDetailModelError.LoadFailed -> MovieDetailError.Network()
  }
}
