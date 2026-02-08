package com.eygraber.jellyfin.screens.music.album.tracks

class AlbumTracksNavigator(
  private val onNavigateBack: () -> Unit,
  private val onNavigateToTrackPlayback: (trackId: String) -> Unit,
) {
  fun navigateBack() {
    onNavigateBack()
  }

  fun navigateToTrackPlayback(trackId: String) {
    onNavigateToTrackPlayback(trackId)
  }
}
