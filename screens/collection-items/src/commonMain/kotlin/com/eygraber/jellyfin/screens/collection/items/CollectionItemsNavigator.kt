package com.eygraber.jellyfin.screens.collection.items

class CollectionItemsNavigator(
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
