package com.eygraber.jellyfin.screens.library.collections

class CollectionsLibraryNavigator(
  private val onNavigateBack: () -> Unit,
  private val onNavigateToCollectionItems: (collectionId: String) -> Unit,
) {
  fun navigateBack() {
    onNavigateBack()
  }

  fun navigateToCollectionItems(collectionId: String) {
    onNavigateToCollectionItems(collectionId)
  }
}
