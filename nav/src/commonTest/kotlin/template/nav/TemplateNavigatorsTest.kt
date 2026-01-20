package template.nav

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import io.kotest.matchers.collections.shouldContainExactly
import template.nav.dev.devSettings
import template.screens.dev.settings.DevSettingsKey
import template.screens.root.RootKey
import template.screens.welcome.WelcomeKey
import kotlin.test.Test

class TemplateNavigatorsTest {
  @Test
  fun `devSettingsNavigator - navigateBack pops the back stack`() {
    val backStack = NavBackStack<NavKey>().apply {
      push(RootKey)
      push(DevSettingsKey)
    }
    val navigator = devSettings(backStack)
    navigator.navigateBack()
    backStack shouldContainExactly listOf(RootKey)
  }

  @Test
  fun `rootNavigator - navigateToNext replaces the back stack with Key`() {
    val backStack = NavBackStack<NavKey>().apply {
      push(RootKey)
    }
    val navigator = TemplateNavigators.root(backStack)
    navigator.navigateToOnboarding()
    backStack shouldContainExactly listOf(WelcomeKey)
  }

  @Test
  fun `welcomeNavigator - navigateToLogin pushes ComingSoon`() {
    val backStack = NavBackStack<NavKey>().apply {
      push(RootKey)
      push(WelcomeKey)
    }

    val navigator = TemplateNavigators.welcome(backStack)

    navigator.navigateToLogin()
    backStack shouldContainExactly listOf(
      RootKey,
      WelcomeKey,
      TemplateNavKeys.ComingSoon("Login"),
    )
  }

  @Test
  fun `welcomeNavigator - navigateToSignUp pushes ComingSoon`() {
    val backStack = NavBackStack<NavKey>().apply {
      push(RootKey)
      push(WelcomeKey)
    }

    val navigator = TemplateNavigators.welcome(backStack)

    navigator.navigateToSignUp()
    backStack shouldContainExactly listOf(
      RootKey,
      WelcomeKey,
      TemplateNavKeys.ComingSoon("SignUp"),
    )
  }
}
