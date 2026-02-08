package com.eygraber.jellyfin.ui.icons

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val JellyfinIcons.Shuffle: ImageVector
  get() {
    if(internalShuffle != null) {
      return requireNotNull(internalShuffle)
    }
    internalShuffle = materialIcon(name = "Filled.Shuffle") {
      materialPath {
        moveTo(10.59f, 9.17f)
        lineTo(5.41f, 4.0f)
        lineTo(4.0f, 5.41f)
        lineToRelative(5.17f, 5.17f)
        lineToRelative(1.42f, -1.41f)
        close()
        moveTo(14.5f, 4.0f)
        lineToRelative(2.04f, 2.04f)
        lineTo(4.0f, 18.59f)
        lineTo(5.41f, 20.0f)
        lineTo(17.96f, 7.46f)
        lineTo(20.0f, 9.5f)
        verticalLineTo(4.0f)
        horizontalLineToRelative(-5.5f)
        close()
        moveTo(14.5f, 16.88f)
        lineTo(13.09f, 15.47f)
        lineToRelative(2.88f, -2.88f)
        lineToRelative(1.42f, 1.41f)
        lineTo(20.0f, 11.5f)
        verticalLineTo(20.0f)
        horizontalLineToRelative(-5.5f)
        lineToRelative(2.04f, -2.04f)
        lineToRelative(-2.04f, -1.08f)
        close()
      }
    }
    return requireNotNull(internalShuffle)
  }

private var internalShuffle: ImageVector? = null
