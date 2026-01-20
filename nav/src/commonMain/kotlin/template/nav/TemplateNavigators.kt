package template.nav

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import template.screens.root.RootNavigator
import template.screens.welcome.WelcomeKey
import template.screens.welcome.WelcomeNavigator

internal object TemplateNavigators {
  fun root(
    backStack: NavBackStack<NavKey>,
  ) = RootNavigator(
    onNavigateToOnboarding = {
      backStack.replaceWith(WelcomeKey)
    },
  )

  fun welcome(
    backStack: NavBackStack<NavKey>,
  ) = WelcomeNavigator(
    onNavigateToSignUp = { backStack.add(TemplateNavKeys.ComingSoon("SignUp")) },
    onNavigateToLogin = { backStack.add(TemplateNavKeys.ComingSoon("Login")) },
  )
}
