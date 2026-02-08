package com.eygraber.jellyfin.screens.library.music

class MusicLibraryNavigator(
  private val onNavigateBack: () -> Unit,
  private val onNavigateToArtistAlbums: (artistId: String) -> Unit,
  private val onNavigateToAlbumTracks: (albumId: String) -> Unit,
) {
  fun navigateBack() {
    onNavigateBack()
  }

  fun navigateToArtistAlbums(artistId: String) {
    onNavigateToArtistAlbums(artistId)
  }

  fun navigateToAlbumTracks(albumId: String) {
    onNavigateToAlbumTracks(albumId)
  }
}
