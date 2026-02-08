package com.eygraber.jellyfin.ui.icons

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val JellyfinIcons.MusicNote: ImageVector
  get() {
    if(internalMusicNote != null) {
      return requireNotNull(internalMusicNote)
    }
    internalMusicNote = materialIcon(name = "Filled.MusicNote") {
      materialPath {
        moveTo(12.0f, 3.0f)
        verticalLineToRelative(10.55f)
        curveToRelative(-0.59f, -0.34f, -1.27f, -0.55f, -2.0f, -0.55f)
        curveToRelative(-2.21f, 0.0f, -4.0f, 1.79f, -4.0f, 4.0f)
        reflectiveCurveToRelative(1.79f, 4.0f, 4.0f, 4.0f)
        reflectiveCurveToRelative(4.0f, -1.79f, 4.0f, -4.0f)
        verticalLineTo(7.0f)
        horizontalLineToRelative(4.0f)
        verticalLineTo(3.0f)
        horizontalLineToRelative(-6.0f)
        close()
      }
    }
    return requireNotNull(internalMusicNote)
  }

private var internalMusicNote: ImageVector? = null
