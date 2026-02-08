package com.eygraber.jellyfin.screens.music.artist.albums

import androidx.compose.runtime.Immutable

@Immutable
data class ArtistAlbumsViewState(
  val artistName: String = "",
  val albums: List<ArtistAlbumItem> = emptyList(),
  val isLoading: Boolean = true,
  val error: ArtistAlbumsError? = null,
  val isEmpty: Boolean = false,
) {
  companion object {
    val Loading = ArtistAlbumsViewState(isLoading = true)
  }
}

@Immutable
sealed interface ArtistAlbumsError {
  val message: String

  data class Network(
    override val message: String = "Unable to connect to server",
  ) : ArtistAlbumsError
}

@Immutable
data class ArtistAlbumItem(
  val id: String,
  val name: String,
  val productionYear: Int?,
  val trackCount: Int?,
  val imageUrl: String?,
)
