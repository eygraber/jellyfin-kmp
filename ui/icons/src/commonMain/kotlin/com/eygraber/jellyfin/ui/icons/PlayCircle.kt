package com.eygraber.jellyfin.ui.icons

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val JellyfinIcons.PlayCircle: ImageVector
  get() {
    if(internalPlayCircle != null) {
      return requireNotNull(internalPlayCircle)
    }
    internalPlayCircle = materialIcon(name = "Filled.PlayCircle") {
      materialPath {
        moveTo(12.0f, 2.0f)
        curveTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
        reflectiveCurveToRelative(4.48f, 10.0f, 10.0f, 10.0f)
        reflectiveCurveToRelative(10.0f, -4.48f, 10.0f, -10.0f)
        reflectiveCurveTo(17.52f, 2.0f, 12.0f, 2.0f)
        close()
        moveTo(10.0f, 16.5f)
        verticalLineToRelative(-9.0f)
        lineToRelative(6.0f, 4.5f)
        lineToRelative(-6.0f, 4.5f)
        close()
      }
    }
    return requireNotNull(internalPlayCircle)
  }

private var internalPlayCircle: ImageVector? = null
