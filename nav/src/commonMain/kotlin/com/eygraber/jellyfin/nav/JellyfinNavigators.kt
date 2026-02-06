package com.eygraber.jellyfin.nav

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.eygraber.jellyfin.screens.home.HomeNavigator
import com.eygraber.jellyfin.screens.root.RootNavigator
import com.eygraber.jellyfin.screens.welcome.WelcomeKey
import com.eygraber.jellyfin.screens.welcome.WelcomeNavigator

internal object JellyfinNavigators {
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
    onNavigateToSignUp = { backStack.add(JellyfinNavKeys.ComingSoon("SignUp")) },
    onNavigateToLogin = { backStack.add(JellyfinNavKeys.ComingSoon("Login")) },
  )

  fun home(
    backStack: NavBackStack<NavKey>,
  ) = HomeNavigator(
    onNavigateBack = { backStack.removeLastOrNull() },
    onNavigateToItemDetail = { itemId ->
      backStack.add(JellyfinNavKeys.ComingSoon("Item Detail ($itemId)"))
    },
  )
}
