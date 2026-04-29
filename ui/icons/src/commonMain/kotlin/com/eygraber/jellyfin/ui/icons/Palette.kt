package com.eygraber.jellyfin.ui.icons

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val JellyfinIcons.Palette: ImageVector
  get() {
    if(internalPalette != null) {
      return requireNotNull(internalPalette)
    }
    internalPalette = materialIcon(name = "Filled.Palette") {
      materialPath {
        moveTo(12.0f, 3.0f)
        curveToRelative(-4.97f, 0.0f, -9.0f, 4.03f, -9.0f, 9.0f)
        reflectiveCurveToRelative(4.03f, 9.0f, 9.0f, 9.0f)
        curveToRelative(0.83f, 0.0f, 1.5f, -0.67f, 1.5f, -1.5f)
        curveToRelative(0.0f, -0.39f, -0.15f, -0.74f, -0.39f, -1.01f)
        curveToRelative(-0.23f, -0.26f, -0.38f, -0.61f, -0.38f, -0.99f)
        curveToRelative(0.0f, -0.83f, 0.67f, -1.5f, 1.5f, -1.5f)
        horizontalLineTo(16.0f)
        curveToRelative(2.76f, 0.0f, 5.0f, -2.24f, 5.0f, -5.0f)
        curveToRelative(0.0f, -4.42f, -4.03f, -8.0f, -9.0f, -8.0f)
        close()
        moveTo(6.5f, 12.0f)
        curveTo(5.67f, 12.0f, 5.0f, 11.33f, 5.0f, 10.5f)
        reflectiveCurveTo(5.67f, 9.0f, 6.5f, 9.0f)
        reflectiveCurveTo(8.0f, 9.67f, 8.0f, 10.5f)
        reflectiveCurveTo(7.33f, 12.0f, 6.5f, 12.0f)
        close()
        moveTo(9.5f, 8.0f)
        curveTo(8.67f, 8.0f, 8.0f, 7.33f, 8.0f, 6.5f)
        reflectiveCurveTo(8.67f, 5.0f, 9.5f, 5.0f)
        reflectiveCurveTo(11.0f, 5.67f, 11.0f, 6.5f)
        reflectiveCurveTo(10.33f, 8.0f, 9.5f, 8.0f)
        close()
        moveTo(14.5f, 8.0f)
        curveTo(13.67f, 8.0f, 13.0f, 7.33f, 13.0f, 6.5f)
        reflectiveCurveTo(13.67f, 5.0f, 14.5f, 5.0f)
        reflectiveCurveTo(16.0f, 5.67f, 16.0f, 6.5f)
        reflectiveCurveTo(15.33f, 8.0f, 14.5f, 8.0f)
        close()
        moveTo(17.5f, 12.0f)
        curveToRelative(-0.83f, 0.0f, -1.5f, -0.67f, -1.5f, -1.5f)
        reflectiveCurveTo(16.67f, 9.0f, 17.5f, 9.0f)
        reflectiveCurveToRelative(1.5f, 0.67f, 1.5f, 1.5f)
        reflectiveCurveTo(18.33f, 12.0f, 17.5f, 12.0f)
        close()
      }
    }
    return requireNotNull(internalPalette)
  }

private var internalPalette: ImageVector? = null
