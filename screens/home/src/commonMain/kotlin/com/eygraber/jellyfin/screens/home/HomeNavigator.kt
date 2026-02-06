package com.eygraber.jellyfin.screens.home

class HomeNavigator(
  private val onNavigateBack: () -> Unit,
) {
  fun navigateBack() {
    onNavigateBack()
  }
}
