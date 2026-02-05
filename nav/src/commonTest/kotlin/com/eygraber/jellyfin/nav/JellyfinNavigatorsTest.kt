package com.eygraber.jellyfin.nav

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.eygraber.jellyfin.nav.dev.devSettings
import com.eygraber.jellyfin.screens.dev.settings.DevSettingsKey
import com.eygraber.jellyfin.screens.root.RootKey
import com.eygraber.jellyfin.screens.welcome.WelcomeKey
import io.kotest.matchers.collections.shouldContainExactly
import kotlin.test.Test

class JellyfinNavigatorsTest {
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
    val navigator = JellyfinNavigators.root(backStack)
    navigator.navigateToOnboarding()
    backStack shouldContainExactly listOf(WelcomeKey)
  }

  @Test
  fun `welcomeNavigator - navigateToLogin pushes ComingSoon`() {
    val backStack = NavBackStack<NavKey>().apply {
      push(RootKey)
      push(WelcomeKey)
    }

    val navigator = JellyfinNavigators.welcome(backStack)

    navigator.navigateToLogin()
    backStack shouldContainExactly listOf(
      RootKey,
      WelcomeKey,
      JellyfinNavKeys.ComingSoon("Login"),
    )
  }

  @Test
  fun `welcomeNavigator - navigateToSignUp pushes ComingSoon`() {
    val backStack = NavBackStack<NavKey>().apply {
      push(RootKey)
      push(WelcomeKey)
    }

    val navigator = JellyfinNavigators.welcome(backStack)

    navigator.navigateToSignUp()
    backStack shouldContainExactly listOf(
      RootKey,
      WelcomeKey,
      JellyfinNavKeys.ComingSoon("SignUp"),
    )
  }
}
