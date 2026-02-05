package com.eygraber.jellyfin.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

@Composable
fun WithMaxDensity(
  maxDensity: Float = -1F,
  maxFontScale: Float = -1F,
  content: @Composable () -> Unit,
) {
  val current = LocalDensity.current

  CompositionLocalProvider(
    LocalDensity provides Density(
      density = when(maxDensity) {
        -1F -> current.density
        else -> minOf(current.density, maxDensity)
      },
      fontScale = when(maxFontScale) {
        -1F -> current.fontScale
        else -> minOf(current.fontScale, maxFontScale)
      },
    ),
    content = content,
  )
}
