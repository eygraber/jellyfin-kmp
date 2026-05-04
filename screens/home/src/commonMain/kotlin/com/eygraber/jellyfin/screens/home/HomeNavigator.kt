package com.eygraber.jellyfin.screens.home

class HomeNavigator(
  private val onNavigateBack: () -> Unit,
  private val onNavigateToItemDetail: (itemId: String, itemType: String) -> Unit,
  private val onNavigateToLibrary: (libraryId: String, collectionType: CollectionType) -> Unit,
  private val onNavigateToSearch: () -> Unit,
  private val onNavigateToSettings: () -> Unit,
) {
  fun navigateBack() {
    onNavigateBack()
  }

  fun navigateToItemDetail(itemId: String, itemType: String) {
    onNavigateToItemDetail(itemId, itemType)
  }

  fun navigateToLibrary(libraryId: String, collectionType: CollectionType) {
    onNavigateToLibrary(libraryId, collectionType)
  }

  fun navigateToSearch() {
    onNavigateToSearch()
  }

  fun navigateToSettings() {
    onNavigateToSettings()
  }
}
