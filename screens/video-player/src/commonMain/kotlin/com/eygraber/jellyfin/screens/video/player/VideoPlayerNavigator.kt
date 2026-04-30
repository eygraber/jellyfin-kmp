package com.eygraber.jellyfin.screens.video.player

class VideoPlayerNavigator(
  private val onNavigateBack: () -> Unit,
) {
  fun navigateBack() {
    onNavigateBack()
  }
}
