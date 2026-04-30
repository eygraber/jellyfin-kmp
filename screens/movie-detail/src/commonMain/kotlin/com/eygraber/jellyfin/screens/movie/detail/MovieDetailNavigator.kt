package com.eygraber.jellyfin.screens.movie.detail

class MovieDetailNavigator(
  private val onNavigateBack: () -> Unit,
  private val onNavigateToSimilarItem: (itemId: String) -> Unit,
  private val onNavigateToPlayer: (itemId: String, itemName: String?) -> Unit,
) {
  fun navigateBack() {
    onNavigateBack()
  }

  fun navigateToSimilarItem(itemId: String) {
    onNavigateToSimilarItem(itemId)
  }

  fun navigateToPlayer(itemId: String, itemName: String?) {
    onNavigateToPlayer(itemId, itemName)
  }
}
