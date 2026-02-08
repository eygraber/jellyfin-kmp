package com.eygraber.jellyfin.screens.tvshow.episodes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.eygraber.jellyfin.screens.tvshow.episodes.model.TvShowEpisodesModel
import com.eygraber.jellyfin.screens.tvshow.episodes.model.TvShowEpisodesModelError
import com.eygraber.vice.ViceCompositor
import dev.zacsweers.metro.Inject

@Inject
class TvShowEpisodesCompositor(
  private val key: TvShowEpisodesKey,
  private val navigator: TvShowEpisodesNavigator,
  private val episodesModel: TvShowEpisodesModel,
) : ViceCompositor<TvShowEpisodesIntent, TvShowEpisodesViewState> {

  @Composable
  override fun composite(): TvShowEpisodesViewState {
    val modelState = episodesModel.currentState()

    LaunchedEffect(Unit) {
      episodesModel.loadEpisodes(key.seasonId)
    }

    return TvShowEpisodesViewState(
      seasonName = modelState.seasonName,
      episodes = modelState.episodes,
      isLoading = modelState.isLoading,
      error = modelState.error?.toViewError(),
      isEmpty = !modelState.isLoading && modelState.error == null && modelState.episodes.isEmpty(),
    )
  }

  override suspend fun onIntent(intent: TvShowEpisodesIntent) {
    when(intent) {
      TvShowEpisodesIntent.RetryLoad -> episodesModel.loadEpisodes(key.seasonId)
      is TvShowEpisodesIntent.SelectEpisode -> navigator.navigateToEpisodeDetail(intent.episodeId)
      TvShowEpisodesIntent.NavigateBack -> navigator.navigateBack()
    }
  }

  private fun TvShowEpisodesModelError.toViewError(): TvShowEpisodesError = when(this) {
    TvShowEpisodesModelError.LoadFailed -> TvShowEpisodesError.Network()
  }
}
