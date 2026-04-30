package com.eygraber.jellyfin.screens.video.player

import androidx.compose.runtime.Immutable

@Immutable
data class VideoPlayerViewState(
  val title: String = "",
  val isPlaying: Boolean = false,
  val isBuffering: Boolean = false,
  val isLoading: Boolean = true,
  val isControlsVisible: Boolean = true,
  val currentPositionMs: Long = 0L,
  val durationMs: Long = 0L,
  val bufferedPositionMs: Long = 0L,
  val progress: Float = 0f,
  val error: VideoPlayerError? = null,
) {
  companion object {
    val Loading = VideoPlayerViewState(isLoading = true)
  }
}

@Immutable
sealed interface VideoPlayerError {
  val message: String

  data class SessionFailed(
    override val message: String = "Failed to load playback session",
  ) : VideoPlayerError

  data class PlaybackFailed(
    override val message: String = "Playback error occurred",
  ) : VideoPlayerError
}
