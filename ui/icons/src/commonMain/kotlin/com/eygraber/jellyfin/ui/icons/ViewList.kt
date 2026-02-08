package com.eygraber.jellyfin.ui.icons

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val JellyfinIcons.ViewList: ImageVector
  get() {
    if(internalViewList != null) {
      return requireNotNull(internalViewList)
    }
    internalViewList = materialIcon(name = "Filled.ViewList") {
      materialPath {
        moveTo(4.0f, 14.0f)
        horizontalLineToRelative(4.0f)
        verticalLineToRelative(-4.0f)
        lineTo(4.0f, 10.0f)
        verticalLineToRelative(4.0f)
        close()
        moveTo(4.0f, 19.0f)
        horizontalLineToRelative(4.0f)
        verticalLineToRelative(-4.0f)
        lineTo(4.0f, 15.0f)
        verticalLineToRelative(4.0f)
        close()
        moveTo(4.0f, 9.0f)
        horizontalLineToRelative(4.0f)
        lineTo(8.0f, 5.0f)
        lineTo(4.0f, 5.0f)
        verticalLineToRelative(4.0f)
        close()
        moveTo(9.0f, 14.0f)
        horizontalLineToRelative(12.0f)
        verticalLineToRelative(-4.0f)
        lineTo(9.0f, 10.0f)
        verticalLineToRelative(4.0f)
        close()
        moveTo(9.0f, 19.0f)
        horizontalLineToRelative(12.0f)
        verticalLineToRelative(-4.0f)
        lineTo(9.0f, 15.0f)
        verticalLineToRelative(4.0f)
        close()
        moveTo(9.0f, 5.0f)
        verticalLineToRelative(4.0f)
        horizontalLineToRelative(12.0f)
        lineTo(21.0f, 5.0f)
        lineTo(9.0f, 5.0f)
        close()
      }
    }
    return requireNotNull(internalViewList)
  }

private var internalViewList: ImageVector? = null
