package com.eygraber.jellyfin.screens.music.artist.albums

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.eygraber.jellyfin.screens.music.artist.albums.model.ArtistAlbumsModel
import com.eygraber.jellyfin.screens.music.artist.albums.model.ArtistAlbumsModelError
import com.eygraber.vice.ViceCompositor
import dev.zacsweers.metro.Inject

@Inject
class ArtistAlbumsCompositor(
  private val key: ArtistAlbumsKey,
  private val navigator: ArtistAlbumsNavigator,
  private val albumsModel: ArtistAlbumsModel,
) : ViceCompositor<ArtistAlbumsIntent, ArtistAlbumsViewState> {

  @Composable
  override fun composite(): ArtistAlbumsViewState {
    val modelState = albumsModel.currentState()

    LaunchedEffect(Unit) {
      albumsModel.loadAlbums(key.artistId)
    }

    return ArtistAlbumsViewState(
      artist = modelState.artist,
      albums = modelState.albums,
      isLoading = modelState.isLoading,
      error = modelState.error?.toViewError(),
      isEmpty = !modelState.isLoading && modelState.error == null && modelState.albums.isEmpty(),
    )
  }

  override suspend fun onIntent(intent: ArtistAlbumsIntent) {
    when(intent) {
      ArtistAlbumsIntent.RetryLoad -> albumsModel.loadAlbums(key.artistId)
      is ArtistAlbumsIntent.SelectAlbum -> navigator.navigateToAlbumTracks(intent.albumId)
      ArtistAlbumsIntent.ToggleFavorite -> Unit // Favorites not yet implemented
      ArtistAlbumsIntent.NavigateBack -> navigator.navigateBack()
    }
  }

  private fun ArtistAlbumsModelError.toViewError(): ArtistAlbumsError = when(this) {
    ArtistAlbumsModelError.LoadFailed -> ArtistAlbumsError.Network()
  }
}
