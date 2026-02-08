package com.eygraber.jellyfin.services.player.impl

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.eygraber.jellyfin.di.qualifiers.AppContext
import com.eygraber.jellyfin.di.scopes.ScreenScope
import com.eygraber.jellyfin.services.player.PlaybackState
import com.eygraber.jellyfin.services.player.VideoPlayerService
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Android implementation of [VideoPlayerService] using ExoPlayer (Media3).
 *
 * Creates and manages an ExoPlayer instance for video playback.
 * The player is scoped to the screen to ensure proper lifecycle management.
 */
@ContributesBinding(ScreenScope::class)
class AndroidVideoPlayerService(
  @param:AppContext private val context: Context,
) : VideoPlayerService {
  private val _playbackState = MutableStateFlow(PlaybackState.Idle)
  override val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

  private var player: ExoPlayer? = null

  /**
   * The underlying ExoPlayer instance for use in AndroidView compositions.
   * Only available after [initialize] is called and before [release].
   */
  val exoPlayer: ExoPlayer? get() = player

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
    player?.release()
    player = null
    _playbackState.value = PlaybackState.Idle
  }

  private inner class PlayerListener : Player.Listener {
    override fun onPlaybackStateChanged(playbackState: Int) {
      updateState()
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
      updateState()
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
