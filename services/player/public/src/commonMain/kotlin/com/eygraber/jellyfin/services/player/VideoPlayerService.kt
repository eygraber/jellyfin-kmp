package com.eygraber.jellyfin.services.player

import kotlinx.coroutines.flow.StateFlow

/**
 * Platform-agnostic video player service.
 *
 * Implementations use platform-specific media players
 * (ExoPlayer on Android, AVPlayer on iOS, etc.)
 */
interface VideoPlayerService {
  /**
   * Current playback state as a reactive flow.
   */
  val playbackState: StateFlow<PlaybackState>

  /**
   * Initializes the player with the given stream URL.
   *
   * @param streamUrl The URL to play.
   * @param startPositionMs The position in milliseconds to start playback from.
   */
  fun initialize(
    streamUrl: String,
    startPositionMs: Long = 0L,
  )

  /**
   * Starts or resumes playback.
   */
  fun play()

  /**
   * Pauses playback.
   */
  fun pause()

  /**
   * Seeks to the given position.
   *
   * @param positionMs Position in milliseconds.
   */
  fun seekTo(positionMs: Long)

  /**
   * Releases all player resources.
   */
  fun release()
}

/**
 * Represents the current state of the video player.
 */
data class PlaybackState(
  val isPlaying: Boolean = false,
  val isBuffering: Boolean = false,
  val isEnded: Boolean = false,
  val hasError: Boolean = false,
  val errorMessage: String? = null,
  val currentPositionMs: Long = 0L,
  val durationMs: Long = 0L,
  val bufferedPositionMs: Long = 0L,
) {
  /**
   * Progress as a fraction between 0.0 and 1.0.
   */
  val progress: Float
    get() = if(durationMs > 0L) {
      (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(minimumValue = 0f, maximumValue = 1f)
    }
    else {
      0f
    }

  companion object {
    val Idle = PlaybackState()
  }
}
