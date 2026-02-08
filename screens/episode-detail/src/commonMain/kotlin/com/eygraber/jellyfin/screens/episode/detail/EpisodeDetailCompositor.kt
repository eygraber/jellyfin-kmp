package com.eygraber.jellyfin.screens.episode.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.eygraber.jellyfin.screens.episode.detail.model.EpisodeDetailModel
import com.eygraber.jellyfin.screens.episode.detail.model.EpisodeDetailModelError
import com.eygraber.vice.ViceCompositor
import dev.zacsweers.metro.Inject

@Inject
class EpisodeDetailCompositor(
  private val key: EpisodeDetailKey,
  private val navigator: EpisodeDetailNavigator,
  private val episodeModel: EpisodeDetailModel,
) : ViceCompositor<EpisodeDetailIntent, EpisodeDetailViewState> {

  @Composable
  override fun composite(): EpisodeDetailViewState {
    val modelState = episodeModel.currentState()

    LaunchedEffect(Unit) {
      episodeModel.loadEpisode(key.episodeId)
    }

    return EpisodeDetailViewState(
      episode = modelState.episode,
      isLoading = modelState.isLoading,
      error = modelState.error?.toViewError(),
    )
  }

  override suspend fun onIntent(intent: EpisodeDetailIntent) {
    when(intent) {
      EpisodeDetailIntent.RetryLoad -> episodeModel.loadEpisode(key.episodeId)
      EpisodeDetailIntent.NavigateBack -> navigator.navigateBack()
    }
  }

  private fun EpisodeDetailModelError.toViewError(): EpisodeDetailError = when(this) {
    EpisodeDetailModelError.LoadFailed -> EpisodeDetailError.Network()
  }
}
