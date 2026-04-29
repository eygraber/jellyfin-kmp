package com.eygraber.jellyfin.screens.root

class RootNavigator(
  private val onNavigateToOnboarding: () -> Unit,
  private val onNavigateToHome: () -> Unit,
) {
  fun navigateToOnboarding() {
    onNavigateToOnboarding()
  }

  fun navigateToHome() {
    onNavigateToHome()
  }
}
