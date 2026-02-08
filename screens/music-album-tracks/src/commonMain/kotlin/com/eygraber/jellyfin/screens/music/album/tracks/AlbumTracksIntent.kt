package com.eygraber.jellyfin.screens.music.album.tracks

sealed interface AlbumTracksIntent {
  data object RetryLoad : AlbumTracksIntent
  data class SelectTrack(val trackId: String) : AlbumTracksIntent
  data object PlayAll : AlbumTracksIntent
  data object ShufflePlay : AlbumTracksIntent
  data object ToggleFavorite : AlbumTracksIntent
  data object NavigateToArtist : AlbumTracksIntent
  data object NavigateBack : AlbumTracksIntent
}
