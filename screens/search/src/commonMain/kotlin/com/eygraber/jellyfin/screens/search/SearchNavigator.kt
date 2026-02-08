package com.eygraber.jellyfin.screens.search

class SearchNavigator(
  private val onNavigateBack: () -> Unit,
  private val onNavigateToItemDetail: (itemId: String, itemType: String) -> Unit,
) {
  fun navigateBack() {
    onNavigateBack()
  }

  fun navigateToItemDetail(itemId: String, itemType: String) {
    onNavigateToItemDetail(itemId, itemType)
  }
}
