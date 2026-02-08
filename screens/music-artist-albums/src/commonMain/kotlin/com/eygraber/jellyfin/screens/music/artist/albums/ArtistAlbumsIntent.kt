package com.eygraber.jellyfin.screens.music.artist.albums

sealed interface ArtistAlbumsIntent {
  data object RetryLoad : ArtistAlbumsIntent
  data class SelectAlbum(val albumId: String) : ArtistAlbumsIntent
  data object NavigateBack : ArtistAlbumsIntent
}
