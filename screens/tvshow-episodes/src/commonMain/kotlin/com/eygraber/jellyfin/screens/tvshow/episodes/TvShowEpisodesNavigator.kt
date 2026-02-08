package com.eygraber.jellyfin.screens.tvshow.episodes

class TvShowEpisodesNavigator(
  private val onNavigateBack: () -> Unit,
  private val onNavigateToEpisodeDetail: (episodeId: String) -> Unit,
) {
  fun navigateBack() {
    onNavigateBack()
  }

  fun navigateToEpisodeDetail(episodeId: String) {
    onNavigateToEpisodeDetail(episodeId)
  }
}
