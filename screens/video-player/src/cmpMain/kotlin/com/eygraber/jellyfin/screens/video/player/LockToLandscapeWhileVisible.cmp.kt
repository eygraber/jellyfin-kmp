package com.eygraber.jellyfin.screens.video.player

import androidx.compose.runtime.Composable

@Composable
internal actual fun LockToLandscapeWhileVisible() {
  // No-op on non-Android platforms.
  // iOS orientation locking is tracked in #241; Desktop/Web have no Activity to rotate.
}
