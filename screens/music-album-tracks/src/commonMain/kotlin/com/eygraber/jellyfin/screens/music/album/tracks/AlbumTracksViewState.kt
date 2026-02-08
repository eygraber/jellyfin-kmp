package com.eygraber.jellyfin.screens.music.album.tracks

import androidx.compose.runtime.Immutable

@Immutable
data class AlbumTracksViewState(
  val album: AlbumDetail? = null,
  val tracks: List<TrackItem> = emptyList(),
  val isLoading: Boolean = true,
  val error: AlbumTracksError? = null,
  val isEmpty: Boolean = false,
) {
  companion object {
    val Loading = AlbumTracksViewState(isLoading = true)
  }
}

@Immutable
sealed interface AlbumTracksError {
  val message: String

  data class Network(
    override val message: String = "Unable to connect to server",
  ) : AlbumTracksError
}

@Immutable
data class AlbumDetail(
  val id: String,
  val name: String,
  val artistName: String,
  val artistId: String?,
  val productionYear: Int?,
  val genre: String?,
  val albumArtUrl: String?,
)

@Immutable
data class TrackItem(
  val id: String,
  val name: String,
  val trackNumber: Int?,
  val durationText: String?,
)
