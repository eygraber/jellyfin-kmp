package template.nav.dev

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import template.nav.pop
import template.screens.dev.settings.DevSettingsNavigator

internal fun devSettings(
  backStack: NavBackStack<NavKey>,
) = DevSettingsNavigator(
  onNavigateBack = { backStack.pop() },
)
