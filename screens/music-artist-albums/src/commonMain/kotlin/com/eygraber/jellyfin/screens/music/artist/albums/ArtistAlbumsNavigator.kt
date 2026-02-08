package com.eygraber.jellyfin.screens.music.artist.albums

class ArtistAlbumsNavigator(
  private val onNavigateBack: () -> Unit,
  private val onNavigateToAlbumTracks: (albumId: String) -> Unit,
) {
  fun navigateBack() {
    onNavigateBack()
  }

  fun navigateToAlbumTracks(albumId: String) {
    onNavigateToAlbumTracks(albumId)
  }
}
