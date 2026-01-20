package template.screens.root

class RootNavigator(
  private val onNavigateToOnboarding: () -> Unit,
) {
  fun navigateToOnboarding() {
    onNavigateToOnboarding()
  }
}
