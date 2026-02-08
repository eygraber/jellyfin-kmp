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
      albumName = modelState.albumName,
      artistName = modelState.artistName,
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
      AlbumTracksIntent.NavigateBack -> navigator.navigateBack()
    }
  }

  private fun AlbumTracksModelError.toViewError(): AlbumTracksError = when(this) {
    AlbumTracksModelError.LoadFailed -> AlbumTracksError.Network()
  }
}
