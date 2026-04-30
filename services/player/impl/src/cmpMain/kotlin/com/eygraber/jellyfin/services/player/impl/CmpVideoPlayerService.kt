package com.eygraber.jellyfin.services.player.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.eygraber.jellyfin.di.scopes.ScreenScope
import com.eygraber.jellyfin.services.player.PlaybackState
import com.eygraber.jellyfin.services.player.VideoPlayerService
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Stub [VideoPlayerService] used on iOS, Desktop (JVM), and Web (WasmJs).
 *
 * Each platform will get its own native implementation in a follow-up issue
 * (AVPlayer for iOS, VLC/JavaFX for Desktop, HTML5 Video for Web). Until then,
 * this stub reports an error state so the UI can surface "not supported" messaging.
 */
@ContributesBinding(ScreenScope::class)
class CmpVideoPlayerService : VideoPlayerService {
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

  @Composable
  override fun VideoSurface(modifier: Modifier) {
    Box(
      modifier = modifier.fillMaxSize().background(Color.Black),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = "Video playback not yet supported on this platform",
        color = Color.White,
        style = MaterialTheme.typography.bodyMedium,
      )
    }
  }
}
