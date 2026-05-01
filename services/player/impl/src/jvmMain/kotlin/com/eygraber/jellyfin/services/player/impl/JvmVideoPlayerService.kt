package com.eygraber.jellyfin.services.player.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import com.eygraber.jellyfin.di.scopes.ScreenScope
import com.eygraber.jellyfin.services.player.PlaybackState
import com.eygraber.jellyfin.services.player.VideoPlayerService
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import javax.swing.SwingUtilities
import javax.swing.Timer

/**
 * Desktop (JVM) implementation of [VideoPlayerService] using vlcj.
 *
 * Uses [CallbackMediaPlayerComponent] (a lightweight `JPanel` that paints frames into a
 * `BufferedImage`) rather than the heavyweight `EmbeddedMediaPlayerComponent`, so the Compose
 * controls overlay rendered on top of [SwingPanel] is actually visible — heavyweight AWT canvases
 * always render above Compose's Skia surface and would hide the controls.
 *
 * Requires a libvlc installation reachable via [NativeDiscovery]; if libvlc cannot be found we
 * fall back to an error state.
 *
 * Component lifecycle is owned by this service: [initialize] creates a fresh player on the EDT,
 * [release] tears it down. UI rendering simply binds the existing component to a [SwingPanel].
 */
@SingleIn(ScreenScope::class)
@ContributesBinding(ScreenScope::class)
class JvmVideoPlayerService : VideoPlayerService {
  private val _playbackState = MutableStateFlow(PlaybackState.Idle)
  override val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

  private var component: CallbackMediaPlayerComponent? = null

  override fun initialize(streamUrl: String, startPositionMs: Long) {
    release()

    val newComponent = createComponentOnEdt() ?: run {
      _playbackState.value = PlaybackState(
        hasError = true,
        errorMessage = "VLC libraries not found. Install VLC to enable Desktop playback.",
      )
      return
    }

    component = newComponent

    val player = newComponent.mediaPlayer()
    player.events().addMediaPlayerEventListener(VlcEventListener())
    // vlcj requires the embedded Swing component to be attached to a displayable AWT hierarchy
    // before media().play() can attach a video surface. SwingPanel mounts the component on the
    // EDT after composition, so wait until isDisplayable() before kicking off playback.
    startPlaybackWhenDisplayable(
      component = newComponent,
      streamUrl = streamUrl,
      startPositionMs = startPositionMs,
    )
  }

  override fun play() {
    component?.mediaPlayer()?.controls()?.play()
  }

  override fun pause() {
    component?.mediaPlayer()?.controls()?.pause()
  }

  override fun seekTo(positionMs: Long) {
    component?.mediaPlayer()?.controls()?.setTime(positionMs)
  }

  override fun release() {
    val current = component ?: return
    component = null
    SwingUtilities.invokeLater { current.release() }
    _playbackState.value = PlaybackState.Idle
  }

  @Composable
  override fun VideoSurface(modifier: Modifier) {
    val current = component
    if(current == null) {
      Box(
        modifier = modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = "Player not initialized",
          color = Color.White,
          style = MaterialTheme.typography.bodyMedium,
        )
      }
      return
    }

    SwingPanel(
      factory = { current },
      modifier = modifier,
      background = Color.Black,
    )
  }

  private fun createComponentOnEdt(): CallbackMediaPlayerComponent? {
    if(!isNativeDiscoverySuccessful) return null

    var component: CallbackMediaPlayerComponent? = null
    val task = Runnable {
      component = runCatching {
        // libvlc's default stereo-mode (Unset / "Original" in the standalone player) passes the
        // source channel layout straight through, which on multichannel content like AAC 5.1
        // routes only the surround channels to a stereo output — the user hears effects/ambient
        // but no dialogue. Force stereo-mode=1 (the "Stereo" option in standalone VLC's GUI)
        // so libvlc downmixes multichannel sources to stereo using the standard ITU matrix.
        // This is the same setting standalone VLC ships with once a user picks "Stereo".
        // 5.1 passthrough on a 5.1-capable output device is tracked separately — fixing it
        // requires detecting the system's audio output capability.
        val factory = MediaPlayerFactory("--stereo-mode=1")
        CallbackMediaPlayerComponent(
          /* mediaPlayerFactory = */ factory,
          /* fullScreenStrategy = */ null,
          /* inputEvents = */ null,
          /* lockBuffers = */ true,
          /* imagePainter = */ null,
          /* renderCallback = */ null,
          /* bufferFormatCallback = */ null,
          /* videoSurfaceComponent = */ null,
        )
      }.getOrNull()
    }
    if(SwingUtilities.isEventDispatchThread()) {
      task.run()
    }
    else {
      SwingUtilities.invokeAndWait(task)
    }
    return component
  }

  private fun startPlaybackWhenDisplayable(
    component: CallbackMediaPlayerComponent,
    streamUrl: String,
    startPositionMs: Long,
  ) {
    fun attempt() {
      if(this.component !== component) return
      if(component.isDisplayable) {
        val player = component.mediaPlayer()
        player.media().play(streamUrl)
        if(startPositionMs > 0L) {
          player.controls().setTime(startPositionMs)
        }
      }
      else {
        Timer(DISPLAYABLE_POLL_INTERVAL_MS) { event ->
          (event.source as? Timer)?.stop()
          attempt()
        }.apply { isRepeats = false }.start()
      }
    }
    SwingUtilities.invokeLater(::attempt)
  }

  private inner class VlcEventListener : MediaPlayerEventAdapter() {
    override fun playing(mediaPlayer: MediaPlayer) {
      pushState(mediaPlayer, isPlaying = true, isBuffering = false)
    }

    override fun paused(mediaPlayer: MediaPlayer) {
      pushState(mediaPlayer, isPlaying = false, isBuffering = false)
    }

    override fun stopped(mediaPlayer: MediaPlayer) {
      pushState(mediaPlayer, isPlaying = false, isBuffering = false)
    }

    override fun finished(mediaPlayer: MediaPlayer) {
      pushState(
        mediaPlayer = mediaPlayer,
        isPlaying = false,
        isBuffering = false,
        isEnded = true,
      )
    }

    override fun buffering(mediaPlayer: MediaPlayer, newCache: Float) {
      pushState(
        mediaPlayer = mediaPlayer,
        isPlaying = mediaPlayer.status().isPlaying,
        isBuffering = newCache < FULL_BUFFER_PERCENT,
      )
    }

    override fun timeChanged(mediaPlayer: MediaPlayer, newTime: Long) {
      pushState(
        mediaPlayer = mediaPlayer,
        isPlaying = mediaPlayer.status().isPlaying,
        isBuffering = false,
        currentMs = newTime,
      )
    }

    override fun error(mediaPlayer: MediaPlayer) {
      _playbackState.value = _playbackState.value.copy(
        hasError = true,
        errorMessage = "Playback error",
      )
    }

    private fun pushState(
      mediaPlayer: MediaPlayer,
      isPlaying: Boolean,
      isBuffering: Boolean,
      isEnded: Boolean = _playbackState.value.isEnded,
      currentMs: Long = mediaPlayer.status().time(),
    ) {
      _playbackState.value = PlaybackState(
        isPlaying = isPlaying,
        isBuffering = isBuffering,
        isEnded = isEnded,
        currentPositionMs = currentMs.coerceAtLeast(0L),
        durationMs = mediaPlayer.status().length().coerceAtLeast(0L),
        bufferedPositionMs = currentMs.coerceAtLeast(0L),
      )
    }
  }

  companion object {
    private val isNativeDiscoverySuccessful: Boolean = NativeDiscovery().discover()
    private const val FULL_BUFFER_PERCENT = 100f
    private const val DISPLAYABLE_POLL_INTERVAL_MS = 50
  }
}
