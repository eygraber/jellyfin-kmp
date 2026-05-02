package com.eygraber.jellyfin.screens.video.player

import android.content.pm.ActivityInfo
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalConfiguration

private const val TABLET_SMALLEST_WIDTH_DP = 600

@Composable
internal actual fun LockToLandscapeWhileVisible() {
  val activity = LocalActivity.current ?: return
  if(LocalConfiguration.current.smallestScreenWidthDp >= TABLET_SMALLEST_WIDTH_DP) return

  DisposableEffect(activity) {
    val original = activity.requestedOrientation
    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    onDispose {
      activity.requestedOrientation = original
    }
  }
}
