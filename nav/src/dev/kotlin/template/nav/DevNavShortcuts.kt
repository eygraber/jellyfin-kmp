package template.nav

import android.view.KeyEvent
import androidx.navigation.NavController

internal fun RealNavShortcutManager.handleEnvironmentKeyEvent(keyCode: Int, event: KeyEvent): NavShortcuts? =
  when {
    keyCode == KeyEvent.KEYCODE_M && event.isCtrlPressed -> NavShortcuts.DevSettings
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
