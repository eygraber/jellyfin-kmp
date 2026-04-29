package com.eygraber.jellyfin.ui.icons

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val JellyfinIcons.BugReport: ImageVector
  get() {
    if(internalBugReport != null) {
      return requireNotNull(internalBugReport)
    }
    internalBugReport = materialIcon(name = "Filled.BugReport") {
      materialPath {
        moveTo(20.0f, 8.0f)
        horizontalLineToRelative(-2.81f)
        curveToRelative(-0.45f, -0.78f, -1.07f, -1.45f, -1.82f, -1.96f)
        lineTo(17.0f, 4.41f)
        lineTo(15.59f, 3.0f)
        lineToRelative(-2.17f, 2.17f)
        curveTo(12.96f, 5.06f, 12.49f, 5.0f, 12.0f, 5.0f)
        reflectiveCurveToRelative(-0.96f, 0.06f, -1.41f, 0.17f)
        lineTo(8.41f, 3.0f)
        lineTo(7.0f, 4.41f)
        lineToRelative(1.62f, 1.63f)
        curveTo(7.88f, 6.55f, 7.26f, 7.22f, 6.81f, 8.0f)
        horizontalLineTo(4.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(2.09f)
        curveToRelative(-0.05f, 0.33f, -0.09f, 0.66f, -0.09f, 1.0f)
        verticalLineToRelative(1.0f)
        horizontalLineTo(4.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(1.0f)
        curveToRelative(0.0f, 0.34f, 0.04f, 0.67f, 0.09f, 1.0f)
        horizontalLineTo(4.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(2.81f)
        curveToRelative(1.04f, 1.79f, 2.97f, 3.0f, 5.19f, 3.0f)
        reflectiveCurveToRelative(4.15f, -1.21f, 5.19f, -3.0f)
        horizontalLineTo(20.0f)
        verticalLineToRelative(-2.0f)
        horizontalLineToRelative(-2.09f)
        curveToRelative(0.05f, -0.33f, 0.09f, -0.66f, 0.09f, -1.0f)
        verticalLineToRelative(-1.0f)
        horizontalLineToRelative(2.0f)
        verticalLineToRelative(-2.0f)
        horizontalLineToRelative(-2.0f)
        verticalLineToRelative(-1.0f)
        curveToRelative(0.0f, -0.34f, -0.04f, -0.67f, -0.09f, -1.0f)
        horizontalLineTo(20.0f)
        verticalLineTo(8.0f)
        close()
        moveTo(14.0f, 16.0f)
        horizontalLineToRelative(-4.0f)
        verticalLineToRelative(-2.0f)
        horizontalLineToRelative(4.0f)
        verticalLineTo(16.0f)
        close()
        moveTo(14.0f, 12.0f)
        horizontalLineToRelative(-4.0f)
        verticalLineToRelative(-2.0f)
        horizontalLineToRelative(4.0f)
        verticalLineTo(12.0f)
        close()
      }
    }
    return requireNotNull(internalBugReport)
  }

private var internalBugReport: ImageVector? = null
