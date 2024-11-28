package template.destinations.root

class RootNavigator(
  private val onNavigateToOnboarding: () -> Unit,
) {
  fun navigateToOnboarding() {
    onNavigateToOnboarding()
  }
}
