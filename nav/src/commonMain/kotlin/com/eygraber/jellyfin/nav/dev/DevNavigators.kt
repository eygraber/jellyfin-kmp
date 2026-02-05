package com.eygraber.jellyfin.nav.dev

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.eygraber.jellyfin.nav.pop
import com.eygraber.jellyfin.screens.dev.settings.DevSettingsNavigator

internal fun devSettings(
  backStack: NavBackStack<NavKey>,
) = DevSettingsNavigator(
  onNavigateBack = { backStack.pop() },
)
