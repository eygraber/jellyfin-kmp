package com.eygraber.jellyfin.nav.dev

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.eygraber.jellyfin.nav.NavShortcuts
import com.eygraber.jellyfin.nav.RealNavShortcutManager
import com.eygraber.jellyfin.screens.dev.settings.DevSettingsKey

internal fun RealNavShortcutManager.handleEnvironmentKeyEvent(event: KeyEvent): NavShortcuts? =
  when {
    event.isCtrlPressed && event.key == Key.M -> NavShortcuts.DevSettings
    else -> null
  }

internal fun NavShortcuts.handleEnvironment(
  backStack: NavBackStack<NavKey>,
) = when(this) {
  NavShortcuts.DevSettings -> {
    backStack.add(DevSettingsKey)
    true
  }
}
