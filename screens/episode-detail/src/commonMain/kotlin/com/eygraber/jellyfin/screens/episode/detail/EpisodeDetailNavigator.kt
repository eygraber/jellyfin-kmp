package com.eygraber.jellyfin.screens.episode.detail

class EpisodeDetailNavigator(
  private val onNavigateBack: () -> Unit,
) {
  fun navigateBack() {
    onNavigateBack()
  }
}
