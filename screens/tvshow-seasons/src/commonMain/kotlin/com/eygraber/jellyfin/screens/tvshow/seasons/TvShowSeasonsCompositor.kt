package com.eygraber.jellyfin.screens.tvshow.seasons

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.eygraber.jellyfin.screens.tvshow.seasons.model.TvShowSeasonsModel
import com.eygraber.jellyfin.screens.tvshow.seasons.model.TvShowSeasonsModelError
import com.eygraber.vice.ViceCompositor
import dev.zacsweers.metro.Inject

@Inject
class TvShowSeasonsCompositor(
  private val key: TvShowSeasonsKey,
  private val navigator: TvShowSeasonsNavigator,
  private val seasonsModel: TvShowSeasonsModel,
) : ViceCompositor<TvShowSeasonsIntent, TvShowSeasonsViewState> {

  @Composable
  override fun composite(): TvShowSeasonsViewState {
    val modelState = seasonsModel.currentState()

    LaunchedEffect(Unit) {
      seasonsModel.loadSeasons(key.seriesId)
    }

    return TvShowSeasonsViewState(
      showName = modelState.showName,
      seasons = modelState.seasons,
      isLoading = modelState.isLoading,
      error = modelState.error?.toViewError(),
      isEmpty = !modelState.isLoading && modelState.error == null && modelState.seasons.isEmpty(),
    )
  }

  override suspend fun onIntent(intent: TvShowSeasonsIntent) {
    when(intent) {
      TvShowSeasonsIntent.RetryLoad -> seasonsModel.loadSeasons(key.seriesId)
      is TvShowSeasonsIntent.SelectSeason -> navigator.navigateToSeasonEpisodes(
        seriesId = key.seriesId,
        seasonId = intent.seasonId,
      )
      TvShowSeasonsIntent.NavigateBack -> navigator.navigateBack()
    }
  }

  private fun TvShowSeasonsModelError.toViewError(): TvShowSeasonsError = when(this) {
    TvShowSeasonsModelError.LoadFailed -> TvShowSeasonsError.Network()
  }
}
