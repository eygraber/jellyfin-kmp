package com.eygraber.jellyfin.services.player.impl

import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.PlayerSurface
import com.eygraber.jellyfin.di.qualifiers.AppContext
import com.eygraber.jellyfin.di.scopes.ScreenScope
import com.eygraber.jellyfin.services.player.PlaybackState
import com.eygraber.jellyfin.services.player.VideoPlayerService
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Android implementation of [VideoPlayerService] using ExoPlayer (Media3).
 *
 * Creates and manages an ExoPlayer instance for video playback. The player is scoped to the
 * screen so it is released along with the [VideoPlayerCompositor] that owns it.
 */
@SingleIn(ScreenScope::class)
@ContributesBinding(ScreenScope::class)
class AndroidVideoPlayerService(
  @param:AppContext private val context: Context,
) : VideoPlayerService {
  private val _playbackState = MutableStateFlow(PlaybackState.Idle)
  override val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

  private var player: ExoPlayer? = null

  private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
  private var positionPollJob: Job? = null

  @OptIn(UnstableApi::class)
  override fun initialize(
    streamUrl: String,
    startPositionMs: Long,
  ) {
    release()

    val exoPlayer = ExoPlayer.Builder(context)
      .build()
      .apply {
        addListener(PlayerListener())
        setMediaItem(MediaItem.fromUri(streamUrl))
        if(startPositionMs > 0L) {
          seekTo(startPositionMs)
        }
        prepare()
        playWhenReady = true
      }

    player = exoPlayer
  }

  override fun play() {
    player?.play()
  }

  override fun pause() {
    player?.pause()
  }

  override fun seekTo(positionMs: Long) {
    player?.seekTo(positionMs)
  }

  override fun release() {
    positionPollJob?.cancel()
    positionPollJob = null
    player?.release()
    player = null
    _playbackState.value = PlaybackState.Idle
  }

  private fun startPositionPolling() {
    positionPollJob?.cancel()
    positionPollJob = scope.launch {
      while(isActive) {
        delay(POSITION_POLL_INTERVAL_MS)
        val currentPlayer = player ?: break
        _playbackState.value = _playbackState.value.copy(
          currentPositionMs = currentPlayer.currentPosition,
          bufferedPositionMs = currentPlayer.bufferedPosition,
          durationMs = currentPlayer.duration.coerceAtLeast(0L),
        )
      }
    }
  }

  private fun stopPositionPolling() {
    positionPollJob?.cancel()
    positionPollJob = null
  }

  @OptIn(UnstableApi::class)
  @Composable
  override fun VideoSurface(modifier: Modifier) {
    val currentPlayer = player
    if(currentPlayer != null) {
      PlayerSurface(
        player = currentPlayer,
        modifier = modifier,
      )
    }
    else {
      Box(modifier = modifier.fillMaxSize().background(Color.Black))
    }
  }

  companion object {
    private const val POSITION_POLL_INTERVAL_MS = 250L
  }

  private inner class PlayerListener : Player.Listener {
    override fun onPlaybackStateChanged(playbackState: Int) {
      updateState()
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
      updateState()
      if(isPlaying) {
        startPositionPolling()
      }
      else {
        stopPositionPolling()
      }
    }

    override fun onPlayerError(error: PlaybackException) {
      _playbackState.value = _playbackState.value.copy(
        hasError = true,
        errorMessage = error.message ?: "Playback error",
      )
    }

    private fun updateState() {
      val currentPlayer = player ?: return
      _playbackState.value = PlaybackState(
        isPlaying = currentPlayer.isPlaying,
        isBuffering = currentPlayer.playbackState == Player.STATE_BUFFERING,
        isEnded = currentPlayer.playbackState == Player.STATE_ENDED,
        hasError = false,
        currentPositionMs = currentPlayer.currentPosition,
        durationMs = currentPlayer.duration.coerceAtLeast(0L),
        bufferedPositionMs = currentPlayer.bufferedPosition,
      )
    }
  }
}
