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
 * Placeholder Desktop (JVM) implementation. Replaced with a real player in #59.
 */
@ContributesBinding(ScreenScope::class)
class JvmVideoPlayerService : VideoPlayerService {
  private val _playbackState = MutableStateFlow(
    PlaybackState(
      hasError = true,
      errorMessage = "Video playback is not supported on Desktop yet",
    ),
  )
  override val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

  override fun initialize(streamUrl: String, startPositionMs: Long) = Unit
  override fun play() = Unit
  override fun pause() = Unit
  override fun seekTo(positionMs: Long) = Unit
  override fun release() = Unit

  @Composable
  override fun VideoSurface(modifier: Modifier) {
    Box(
      modifier = modifier.fillMaxSize().background(Color.Black),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = "Video playback not yet supported on Desktop",
        color = Color.White,
        style = MaterialTheme.typography.bodyMedium,
      )
    }
  }
}
