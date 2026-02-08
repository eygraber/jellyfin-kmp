package com.eygraber.jellyfin.ui.icons

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val JellyfinIcons.GridView: ImageVector
  get() {
    if(internalGridView != null) {
      return requireNotNull(internalGridView)
    }
    internalGridView = materialIcon(name = "Filled.GridView") {
      materialPath {
        moveTo(3.0f, 3.0f)
        verticalLineToRelative(8.0f)
        horizontalLineToRelative(8.0f)
        lineTo(11.0f, 3.0f)
        lineTo(3.0f, 3.0f)
        close()
        moveTo(13.0f, 3.0f)
        verticalLineToRelative(8.0f)
        horizontalLineToRelative(8.0f)
        lineTo(21.0f, 3.0f)
        horizontalLineToRelative(-8.0f)
        close()
        moveTo(3.0f, 13.0f)
        verticalLineToRelative(8.0f)
        horizontalLineToRelative(8.0f)
        verticalLineToRelative(-8.0f)
        lineTo(3.0f, 13.0f)
        close()
        moveTo(13.0f, 13.0f)
        verticalLineToRelative(8.0f)
        horizontalLineToRelative(8.0f)
        verticalLineToRelative(-8.0f)
        horizontalLineToRelative(-8.0f)
        close()
      }
    }
    return requireNotNull(internalGridView)
  }

private var internalGridView: ImageVector? = null
