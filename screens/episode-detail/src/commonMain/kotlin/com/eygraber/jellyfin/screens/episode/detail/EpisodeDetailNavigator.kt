package com.eygraber.jellyfin.screens.episode.detail

class EpisodeDetailNavigator(
  private val onNavigateBack: () -> Unit,
  private val onNavigateToPlayer: (itemId: String, itemName: String?) -> Unit,
) {
  fun navigateBack() {
    onNavigateBack()
  }

  fun navigateToPlayer(itemId: String, itemName: String?) {
    onNavigateToPlayer(itemId, itemName)
  }
}
