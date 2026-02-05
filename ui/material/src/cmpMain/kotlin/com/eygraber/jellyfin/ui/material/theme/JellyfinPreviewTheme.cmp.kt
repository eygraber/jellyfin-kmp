package com.eygraber.jellyfin.ui.material.theme

import androidx.compose.runtime.Composable

@Composable
actual fun JellyfinEdgeToEdgePreviewTheme(
  isDarkMode: Boolean,
  content: @Composable () -> Unit,
) {
  JellyfinPreviewTheme(
    isDarkMode = isDarkMode,
    content = content,
  )
}
