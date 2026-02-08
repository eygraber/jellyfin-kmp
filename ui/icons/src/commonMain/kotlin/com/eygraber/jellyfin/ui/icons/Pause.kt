package com.eygraber.jellyfin.ui.icons

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val JellyfinIcons.Pause: ImageVector
  get() {
    if(internalPause != null) {
      return requireNotNull(internalPause)
    }
    internalPause = materialIcon(name = "Filled.Pause") {
      materialPath {
        moveTo(6.0f, 19.0f)
        horizontalLineToRelative(4.0f)
        verticalLineTo(5.0f)
        horizontalLineTo(6.0f)
        verticalLineToRelative(14.0f)
        close()
        moveTo(14.0f, 5.0f)
        verticalLineToRelative(14.0f)
        horizontalLineToRelative(4.0f)
        verticalLineTo(5.0f)
        horizontalLineToRelative(-4.0f)
        close()
      }
    }
    return requireNotNull(internalPause)
  }

private var internalPause: ImageVector? = null
