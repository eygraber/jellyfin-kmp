package com.eygraber.jellyfin.ui.icons

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val JellyfinIcons.PlayArrow: ImageVector
  get() {
    if(internalPlayArrow != null) {
      return requireNotNull(internalPlayArrow)
    }
    internalPlayArrow = materialIcon(name = "Filled.PlayArrow") {
      materialPath {
        moveTo(8.0f, 5.0f)
        verticalLineToRelative(14.0f)
        lineToRelative(11.0f, -7.0f)
        close()
      }
    }
    return requireNotNull(internalPlayArrow)
  }

private var internalPlayArrow: ImageVector? = null
