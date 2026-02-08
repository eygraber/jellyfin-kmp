package com.eygraber.jellyfin.services.player.impl

import com.eygraber.jellyfin.services.player.PlaybackState
import com.eygraber.jellyfin.services.player.VideoPlayerService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * No-op [VideoPlayerService] used as a fallback on platforms
 * where a native player implementation is not yet available.
 *
 * Platform-specific implementations (e.g. ExoPlayer on Android)
 * override this binding via [ContributesBinding].
 */
open class NoOpVideoPlayerService : VideoPlayerService {
  private val _playbackState = MutableStateFlow(
    PlaybackState(
      hasError = true,
      errorMessage = "Video playback is not supported on this platform yet",
    ),
  )
  override val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

  override fun initialize(streamUrl: String, startPositionMs: Long) {
    // No-op
  }

  override fun play() {
    // No-op
  }

  override fun pause() {
    // No-op
  }

  override fun seekTo(positionMs: Long) {
    // No-op
  }

  override fun release() {
    // No-op
  }
}
