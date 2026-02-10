package com.eygraber.jellyfin.screens.login

class LoginNavigator(
  private val onNavigateToHome: () -> Unit,
  private val onNavigateBack: () -> Unit,
) {
  fun navigateToHome() {
    onNavigateToHome()
  }

  fun navigateBack() {
    onNavigateBack()
  }
}
