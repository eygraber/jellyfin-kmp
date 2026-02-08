package com.eygraber.jellyfin.screens.music.album.tracks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.eygraber.jellyfin.screens.music.album.tracks.model.AlbumTracksModel
import com.eygraber.jellyfin.screens.music.album.tracks.model.AlbumTracksModelError
import com.eygraber.vice.ViceCompositor
import dev.zacsweers.metro.Inject

@Inject
class AlbumTracksCompositor(
  private val key: AlbumTracksKey,
  private val navigator: AlbumTracksNavigator,
  private val tracksModel: AlbumTracksModel,
) : ViceCompositor<AlbumTracksIntent, AlbumTracksViewState> {

  @Composable
  override fun composite(): AlbumTracksViewState {
    val modelState = tracksModel.currentState()

    LaunchedEffect(Unit) {
      tracksModel.loadTracks(key.albumId)
    }

    return AlbumTracksViewState(
      album = modelState.album,
      tracks = modelState.tracks,
      isLoading = modelState.isLoading,
      error = modelState.error?.toViewError(),
      isEmpty = !modelState.isLoading && modelState.error == null && modelState.tracks.isEmpty(),
    )
  }

  override suspend fun onIntent(intent: AlbumTracksIntent) {
    when(intent) {
      AlbumTracksIntent.RetryLoad -> tracksModel.loadTracks(key.albumId)
      is AlbumTracksIntent.SelectTrack -> navigator.navigateToTrackPlayback(intent.trackId)
      AlbumTracksIntent.PlayAll -> Unit // Playback not yet implemented
      AlbumTracksIntent.ShufflePlay -> Unit // Playback not yet implemented
      AlbumTracksIntent.ToggleFavorite -> Unit // Favorites not yet implemented
      AlbumTracksIntent.NavigateToArtist -> navigateToArtist()
      AlbumTracksIntent.NavigateBack -> navigator.navigateBack()
    }
  }

  private fun navigateToArtist() {
    tracksModel.currentAlbumArtistId()?.let { artistId ->
      navigator.navigateToArtist(artistId)
    }
  }

  private fun AlbumTracksModelError.toViewError(): AlbumTracksError = when(this) {
    AlbumTracksModelError.LoadFailed -> AlbumTracksError.Network()
  }
}
