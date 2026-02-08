package com.eygraber.jellyfin.screens.video.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.eygraber.jellyfin.screens.video.player.model.VideoPlayerModel
import com.eygraber.jellyfin.screens.video.player.model.VideoPlayerModelError
import com.eygraber.vice.ViceCompositor
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.delay

@Inject
class VideoPlayerCompositor(
  private val key: VideoPlayerKey,
  private val navigator: VideoPlayerNavigator,
  private val playerModel: VideoPlayerModel,
) : ViceCompositor<VideoPlayerIntent, VideoPlayerViewState> {

  @Composable
  override fun composite(): VideoPlayerViewState {
    val modelState = playerModel.currentState()
    val playerState by playerModel.playerState.collectAsState()

    LaunchedEffect(Unit) {
      playerModel.loadAndPlay(
        itemId = key.itemId,
        startPositionMs = key.startPositionMs,
      )
    }

    LaunchedEffect(playerState) {
      playerModel.updateFromPlayerState(playerState)
    }

    // Report progress every 10 seconds while playing
    LaunchedEffect(playerState.isPlaying) {
      if(playerState.isPlaying) {
        while(true) {
          delay(PROGRESS_REPORT_INTERVAL_MS)
          playerModel.reportProgress()
        }
      }
    }

    // Clean up player on dispose
    DisposableEffect(Unit) {
      onDispose {
        // Note: stop() is a suspend function; the compositor lifecycle
        // handles cleanup through the screen scope cancellation
      }
    }

    return VideoPlayerViewState(
      title = key.itemName.orEmpty(),
      isPlaying = playerState.isPlaying,
      isBuffering = playerState.isBuffering,
      isLoading = modelState.isLoading,
      isControlsVisible = modelState.isControlsVisible,
      currentPositionMs = playerState.currentPositionMs,
      durationMs = playerState.durationMs,
      bufferedPositionMs = playerState.bufferedPositionMs,
      progress = playerState.progress,
      error = modelState.error?.toViewError(),
    )
  }

  override suspend fun onIntent(intent: VideoPlayerIntent) {
    when(intent) {
      VideoPlayerIntent.Play -> playerModel.play()
      VideoPlayerIntent.Pause -> playerModel.pause()
      is VideoPlayerIntent.SeekTo -> playerModel.seekTo(intent.positionMs)
      VideoPlayerIntent.ToggleControls -> playerModel.toggleControls()
      VideoPlayerIntent.NavigateBack -> {
        playerModel.stop()
        navigator.navigateBack()
      }

      VideoPlayerIntent.RetryLoad -> playerModel.loadAndPlay(
        itemId = key.itemId,
        startPositionMs = key.startPositionMs,
      )
    }
  }

  private fun VideoPlayerModelError.toViewError(): VideoPlayerError = when(this) {
    VideoPlayerModelError.SessionFailed -> VideoPlayerError.SessionFailed()
    VideoPlayerModelError.PlaybackFailed -> VideoPlayerError.PlaybackFailed()
  }

  companion object {
    private const val PROGRESS_REPORT_INTERVAL_MS = 10_000L
  }
}
