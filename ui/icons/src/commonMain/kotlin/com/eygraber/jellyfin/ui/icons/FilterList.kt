package com.eygraber.jellyfin.ui.icons

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val JellyfinIcons.FilterList: ImageVector
  get() {
    if(internalFilterList != null) {
      return requireNotNull(internalFilterList)
    }
    internalFilterList = materialIcon(name = "Filled.FilterList") {
      materialPath {
        moveTo(10.0f, 18.0f)
        horizontalLineToRelative(4.0f)
        verticalLineToRelative(-2.0f)
        horizontalLineToRelative(-4.0f)
        verticalLineToRelative(2.0f)
        close()
        moveTo(3.0f, 6.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(18.0f)
        lineTo(21.0f, 6.0f)
        lineTo(3.0f, 6.0f)
        close()
        moveTo(6.0f, 13.0f)
        horizontalLineToRelative(12.0f)
        verticalLineToRelative(-2.0f)
        lineTo(6.0f, 11.0f)
        verticalLineToRelative(2.0f)
        close()
      }
    }
    return requireNotNull(internalFilterList)
  }

private var internalFilterList: ImageVector? = null
