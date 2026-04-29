package com.eygraber.jellyfin.screens.settings

class SettingsNavigator(
  private val onNavigateBack: () -> Unit,
  private val onNavigateToCategory: (SettingsCategory) -> Unit,
  private val onNavigateToWelcome: () -> Unit,
) {
  fun navigateBack() {
    onNavigateBack()
  }

  fun navigateToCategory(category: SettingsCategory) {
    onNavigateToCategory(category)
  }

  fun navigateToWelcome() {
    onNavigateToWelcome()
  }
}
