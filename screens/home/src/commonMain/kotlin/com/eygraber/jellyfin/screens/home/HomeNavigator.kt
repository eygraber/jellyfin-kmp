package com.eygraber.jellyfin.screens.home

class HomeNavigator(
  private val onNavigateBack: () -> Unit,
  private val onNavigateToItemDetail: (itemId: String) -> Unit,
  private val onNavigateToLibrary: (libraryId: String, collectionType: CollectionType) -> Unit,
) {
  fun navigateBack() {
    onNavigateBack()
  }

  fun navigateToItemDetail(itemId: String) {
    onNavigateToItemDetail(itemId)
  }

  fun navigateToLibrary(libraryId: String, collectionType: CollectionType) {
    onNavigateToLibrary(libraryId, collectionType)
  }
}
