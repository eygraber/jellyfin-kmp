package com.eygraber.jellyfin.screens.home

class HomeNavigator(
  private val onNavigateBack: () -> Unit,
  private val onNavigateToItemDetail: (itemId: String) -> Unit,
  private val onNavigateToLibrary: (libraryId: String) -> Unit,
) {
  fun navigateBack() {
    onNavigateBack()
  }

  fun navigateToItemDetail(itemId: String) {
    onNavigateToItemDetail(itemId)
  }

  fun navigateToLibrary(libraryId: String) {
    onNavigateToLibrary(libraryId)
  }
}
