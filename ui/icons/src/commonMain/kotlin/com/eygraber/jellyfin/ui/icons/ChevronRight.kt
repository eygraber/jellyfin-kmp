package com.eygraber.jellyfin.ui.icons

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val JellyfinIcons.ChevronRight: ImageVector
  get() {
    if(internalChevronRight != null) {
      return requireNotNull(internalChevronRight)
    }
    internalChevronRight = materialIcon(name = "Filled.ChevronRight") {
      materialPath {
        moveTo(10.0f, 6.0f)
        lineToRelative(-1.41f, 1.41f)
        lineTo(13.17f, 12.0f)
        lineToRelative(-4.58f, 4.59f)
        lineTo(10.0f, 18.0f)
        lineToRelative(6.0f, -6.0f)
        close()
      }
    }
    return requireNotNull(internalChevronRight)
  }

private var internalChevronRight: ImageVector? = null
