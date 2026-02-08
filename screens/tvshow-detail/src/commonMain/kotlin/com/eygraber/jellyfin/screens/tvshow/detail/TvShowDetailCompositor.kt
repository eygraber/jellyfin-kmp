package com.eygraber.jellyfin.screens.tvshow.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.eygraber.jellyfin.screens.tvshow.detail.model.TvShowDetailModel
import com.eygraber.jellyfin.screens.tvshow.detail.model.TvShowDetailModelError
import com.eygraber.vice.ViceCompositor
import dev.zacsweers.metro.Inject

@Inject
class TvShowDetailCompositor(
  private val key: TvShowDetailKey,
  private val navigator: TvShowDetailNavigator,
  private val showModel: TvShowDetailModel,
) : ViceCompositor<TvShowDetailIntent, TvShowDetailViewState> {

  @Composable
  override fun composite(): TvShowDetailViewState {
    val modelState = showModel.currentState()

    LaunchedEffect(Unit) {
      showModel.loadShow(key.seriesId)
    }

    return TvShowDetailViewState(
      show = modelState.show,
      seasons = modelState.seasons,
      isLoading = modelState.isLoading,
      error = modelState.error?.toViewError(),
    )
  }

  override suspend fun onIntent(intent: TvShowDetailIntent) {
    when(intent) {
      TvShowDetailIntent.RetryLoad -> showModel.loadShow(key.seriesId)
      is TvShowDetailIntent.SelectSeason -> navigator.navigateToSeasonEpisodes(
        seriesId = key.seriesId,
        seasonId = intent.seasonId,
      )
      TvShowDetailIntent.NavigateBack -> navigator.navigateBack()
    }
  }

  private fun TvShowDetailModelError.toViewError(): TvShowDetailError = when(this) {
    TvShowDetailModelError.LoadFailed -> TvShowDetailError.Network()
  }
}
