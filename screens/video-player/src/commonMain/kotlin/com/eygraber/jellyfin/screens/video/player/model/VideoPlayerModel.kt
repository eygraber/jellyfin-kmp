package com.eygraber.jellyfin.screens.video.player.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.data.playback.PlaybackRepository
import com.eygraber.jellyfin.data.playback.PlaybackSession
import com.eygraber.jellyfin.services.player.PlaybackState
import com.eygraber.jellyfin.services.player.VideoPlayerService
import com.eygraber.vice.ViceSource
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.StateFlow

data class VideoPlayerModelState(
  val session: PlaybackSession? = null,
  val playbackState: PlaybackState = PlaybackState.Idle,
  val isLoading: Boolean = true,
  val isControlsVisible: Boolean = true,
  val error: VideoPlayerModelError? = null,
)

enum class VideoPlayerModelError {
  SessionFailed,
  PlaybackFailed,
}

@Inject
class VideoPlayerModel(
  private val playbackRepository: PlaybackRepository,
  private val playerService: VideoPlayerService,
) : ViceSource<VideoPlayerModelState> {
  private var state by mutableStateOf(VideoPlayerModelState())

  internal val stateForTest: VideoPlayerModelState get() = state

  /**
   * Reactive playback state from the player service.
   */
  val playerState: StateFlow<PlaybackState> get() = playerService.playbackState

  @Composable
  override fun currentState(): VideoPlayerModelState = state

  /**
   * Loads the playback session and initializes the player.
   */
  suspend fun loadAndPlay(itemId: String, startPositionMs: Long) {
    state = state.copy(isLoading = true, error = null)

    val result = playbackRepository.getPlaybackSession(itemId = itemId)

    if(result.isSuccess()) {
      val session = result.value
      state = state.copy(
        session = session,
        isLoading = false,
      )

      playerService.initialize(
        streamUrl = session.streamUrl,
        startPositionMs = startPositionMs,
      )

      playbackRepository.reportPlaybackStart(session = session)
    }
    else {
      state = state.copy(
        isLoading = false,
        error = VideoPlayerModelError.SessionFailed,
      )
    }
  }

  /**
   * Updates the model state from the player service state.
   */
  fun updateFromPlayerState(playbackState: PlaybackState) {
    state = state.copy(
      playbackState = playbackState,
      error = if(playbackState.hasError) VideoPlayerModelError.PlaybackFailed else state.error,
    )
  }

  fun play() {
    playerService.play()
  }

  fun pause() {
    playerService.pause()
  }

  fun seekTo(positionMs: Long) {
    playerService.seekTo(positionMs)
  }

  fun toggleControls() {
    state = state.copy(isControlsVisible = !state.isControlsVisible)
  }

  /**
   * Reports progress and releases player resources.
   */
  suspend fun stop() {
    val session = state.session ?: return
    val positionTicks = state.playbackState.currentPositionMs * TICKS_PER_MILLISECOND

    playbackRepository.reportPlaybackStopped(
      session = session,
      positionTicks = positionTicks,
    )

    playerService.release()
  }

  /**
   * Reports current progress to the server.
   */
  suspend fun reportProgress() {
    val session = state.session ?: return
    val positionTicks = state.playbackState.currentPositionMs * TICKS_PER_MILLISECOND

    playbackRepository.reportPlaybackProgress(
      session = session,
      positionTicks = positionTicks,
      isPaused = !state.playbackState.isPlaying,
    )
  }

  companion object {
    private const val TICKS_PER_MILLISECOND = 10_000L
  }
}
