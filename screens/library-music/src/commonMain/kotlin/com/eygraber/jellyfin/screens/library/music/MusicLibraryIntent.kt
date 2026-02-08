package com.eygraber.jellyfin.screens.library.music

sealed interface MusicLibraryIntent {
  data object LoadMore : MusicLibraryIntent
  data object Refresh : MusicLibraryIntent
  data object RetryLoad : MusicLibraryIntent
  data class SelectTab(val tab: MusicTab) : MusicLibraryIntent
  data class SelectArtist(val artistId: String) : MusicLibraryIntent
  data class SelectAlbum(val albumId: String) : MusicLibraryIntent
  data object NavigateBack : MusicLibraryIntent
}
