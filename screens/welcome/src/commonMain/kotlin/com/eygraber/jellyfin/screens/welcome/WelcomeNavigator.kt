package com.eygraber.jellyfin.screens.welcome

class WelcomeNavigator(
  private val onNavigateToSignUp: () -> Unit,
  private val onNavigateToLogin: () -> Unit,
) {
  fun navigateToSignUp() {
    onNavigateToSignUp()
  }

  fun navigateToLogin() {
    onNavigateToLogin()
  }
}
