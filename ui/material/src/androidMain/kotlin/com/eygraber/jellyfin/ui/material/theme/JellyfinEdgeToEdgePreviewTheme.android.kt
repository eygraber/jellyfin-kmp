package com.eygraber.jellyfin.ui.material.theme

import androidx.compose.runtime.Composable
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import de.drick.compose.edgetoedgepreviewlib.NavigationMode

@Composable
actual fun JellyfinEdgeToEdgePreviewTheme(
  isDarkMode: Boolean,
  content: @Composable () -> Unit,
) {
  EdgeToEdgeTemplate(
    navMode = NavigationMode.Gesture,
    isDarkMode = isDarkMode,
    showInsetsBorder = false,
  ) {
    JellyfinPreviewTheme(
      isDarkMode = isDarkMode,
      content = content,
    )
  }
}
