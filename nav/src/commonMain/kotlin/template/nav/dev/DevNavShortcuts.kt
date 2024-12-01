package template.nav.dev

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.navigation.NavController
import template.nav.NavShortcuts
import template.nav.RealNavShortcutManager

internal fun RealNavShortcutManager.handleEnvironmentKeyEvent(event: KeyEvent): NavShortcuts? =
  when {
    event.isCtrlPressed && event.key == Key.M -> NavShortcuts.DevSettings
    else -> null
  }

internal fun NavShortcuts.handleEnvironment(
  navController: NavController,
) = when(this) {
  NavShortcuts.DevSettings -> {
    navController.navigate(TemplateRoutesDevSettings)
    true
  }
}
