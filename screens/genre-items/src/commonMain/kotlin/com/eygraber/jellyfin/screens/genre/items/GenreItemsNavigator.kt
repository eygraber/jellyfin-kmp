package com.eygraber.jellyfin.screens.genre.items

class GenreItemsNavigator(
  private val onNavigateBack: () -> Unit,
  private val onNavigateToItemDetail: (itemId: String) -> Unit,
) {
  fun navigateBack() {
    onNavigateBack()
  }

  fun navigateToItemDetail(itemId: String) {
    onNavigateToItemDetail(itemId)
  }
}
