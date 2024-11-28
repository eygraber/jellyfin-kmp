package template.nav

import android.view.KeyEvent
import androidx.navigation.NavController

internal fun RealNavShortcutManager.handleEnvironmentKeyEvent(keyCode: Int, event: KeyEvent): NavShortcuts? = null

internal fun NavShortcuts.handleEnvironment(
  navController: NavController,
): Boolean = false
