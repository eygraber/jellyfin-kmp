package com.eygraber.jellyfin.ui.icons

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val JellyfinIcons.ClosedCaption: ImageVector
  get() {
    if(internalClosedCaption != null) {
      return requireNotNull(internalClosedCaption)
    }
    internalClosedCaption = materialIcon(name = "Filled.ClosedCaption") {
      materialPath {
        moveTo(19.0f, 4.0f)
        horizontalLineTo(5.0f)
        curveToRelative(-1.11f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
        verticalLineToRelative(12.0f)
        curveToRelative(0.0f, 1.1f, 0.89f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(14.0f)
        curveToRelative(1.11f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        verticalLineTo(6.0f)
        curveTo(21.0f, 4.9f, 20.11f, 4.0f, 19.0f, 4.0f)
        close()
        moveTo(11.0f, 11.0f)
        horizontalLineTo(9.5f)
        verticalLineToRelative(-0.5f)
        horizontalLineToRelative(-2.0f)
        verticalLineToRelative(3.0f)
        horizontalLineToRelative(2.0f)
        verticalLineTo(13.0f)
        horizontalLineTo(11.0f)
        verticalLineToRelative(1.0f)
        curveToRelative(0.0f, 0.55f, -0.45f, 1.0f, -1.0f, 1.0f)
        horizontalLineTo(7.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, -0.45f, -1.0f, -1.0f)
        verticalLineToRelative(-4.0f)
        curveToRelative(0.0f, -0.55f, 0.45f, -1.0f, 1.0f, -1.0f)
        horizontalLineToRelative(3.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, 0.45f, 1.0f, 1.0f)
        verticalLineTo(11.0f)
        close()
        moveTo(18.0f, 11.0f)
        horizontalLineToRelative(-1.5f)
        verticalLineToRelative(-0.5f)
        horizontalLineToRelative(-2.0f)
        verticalLineToRelative(3.0f)
        horizontalLineToRelative(2.0f)
        verticalLineTo(13.0f)
        horizontalLineTo(18.0f)
        verticalLineToRelative(1.0f)
        curveToRelative(0.0f, 0.55f, -0.45f, 1.0f, -1.0f, 1.0f)
        horizontalLineToRelative(-3.0f)
        curveToRelative(-0.55f, 0.0f, -1.0f, -0.45f, -1.0f, -1.0f)
        verticalLineToRelative(-4.0f)
        curveToRelative(0.0f, -0.55f, 0.45f, -1.0f, 1.0f, -1.0f)
        horizontalLineToRelative(3.0f)
        curveToRelative(0.55f, 0.0f, 1.0f, 0.45f, 1.0f, 1.0f)
        verticalLineTo(11.0f)
        close()
      }
    }
    return requireNotNull(internalClosedCaption)
  }

private var internalClosedCaption: ImageVector? = null
