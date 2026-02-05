package com.eygraber.jellyfin.apps.shared

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.eygraber.jellyfin.nav.JellyfinNav
import com.eygraber.jellyfin.nav.JellyfinNavGraph
import com.eygraber.jellyfin.ui.material.theme.JellyfinTheme
import com.eygraber.jellyfin.ui.material.theme.SystemDarkModeOverride

@Composable
fun JellyfinAppSession(
  onDarkMode: @Composable (Boolean) -> Unit,
  navGraph: JellyfinNavGraph,
  modifier: Modifier = Modifier,
) {
  val isDarkMode = when(SystemDarkModeOverride.rememberState()) {
    SystemDarkModeOverride.None -> isSystemInDarkTheme()
    SystemDarkModeOverride.Dark -> true
    SystemDarkModeOverride.Light -> false
  }

  onDarkMode(isDarkMode)

  Content(
    isDarkMode = isDarkMode,
    navGraph = navGraph,
    modifier = modifier,
  )
}

@Suppress("ModifierNotUsedAtRoot")
@Composable
private fun Content(
  isDarkMode: Boolean,
  navGraph: JellyfinNavGraph,
  modifier: Modifier = Modifier,
) {
  JellyfinTheme(
    isDarkMode = isDarkMode,
  ) {
    Surface(
      modifier = Modifier.fillMaxSize(),
    ) {
      Box(modifier = modifier) {
        JellyfinNav(
          navGraph = navGraph,
        )
      }
    }
  }
}
