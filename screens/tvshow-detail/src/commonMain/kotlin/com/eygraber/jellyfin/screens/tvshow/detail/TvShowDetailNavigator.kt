package com.eygraber.jellyfin.screens.tvshow.detail

class TvShowDetailNavigator(
  private val onNavigateBack: () -> Unit,
  private val onNavigateToSeasonEpisodes: (seriesId: String, seasonId: String) -> Unit,
) {
  fun navigateBack() {
    onNavigateBack()
  }

  fun navigateToSeasonEpisodes(seriesId: String, seasonId: String) {
    onNavigateToSeasonEpisodes(seriesId, seasonId)
  }
}
