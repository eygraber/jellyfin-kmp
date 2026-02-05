package com.eygraber.jellyfin.screens.dev.settings

class DevSettingsNavigator(
  private val onNavigateBack: () -> Unit,
) {
  fun navigateBack() {
    onNavigateBack()
  }
}
