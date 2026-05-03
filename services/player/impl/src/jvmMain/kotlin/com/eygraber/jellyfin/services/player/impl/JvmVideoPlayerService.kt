package com.eygraber.jellyfin.services.player.impl

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.eygraber.jellyfin.di.scopes.ScreenScope
import com.eygraber.jellyfin.services.player.PlaybackState
import com.eygraber.jellyfin.services.player.VideoPlayerService
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.ImageInfo
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormatCallback
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.RenderCallback
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.format.RV32BufferFormat
import java.nio.ByteBuffer

/**
 * Desktop (JVM) implementation of [VideoPlayerService] using vlcj.
 *
 * Renders video by hooking libvlc's callback video surface: each decoded frame is copied into a
 * single Skia [Bitmap] whose pixel storage is reused across frames via [Bitmap.installPixels],
 * then exposed to Compose through [Bitmap.asComposeImageBitmap]. Both the pixel byte array and
 * the [Bitmap] live for the duration of a stream session, so steady-state playback only allocates
 * the cheap [ImageBitmap] wrapper per frame instead of a fresh ~8 MB Skia bitmap.
 *
 * Sidesteps `SwingPanel` entirely, which was hiding the controls overlay on Desktop — interop
 * content from `SwingPanel` paints above Compose's Skia surface regardless of z-order, even with
 * the lightweight `CallbackMediaPlayerComponent` and the `compose.interop.blending` system
 * property.
 *
 * Audio uses libvlc's standard output. `--stereo-mode=1` forces a stereo downmix so multichannel
 * sources (e.g. AAC 5.1) play correctly on stereo systems; per-output capability detection is
 * tracked in #238.
 *
 * Requires a libvlc installation reachable via [NativeDiscovery]; if libvlc cannot be found we
 * fall back to an error state.
 */
@SingleIn(ScreenScope::class)
@ContributesBinding(ScreenScope::class)
class JvmVideoPlayerService : VideoPlayerService {
  private val _playbackState = MutableStateFlow(PlaybackState.Idle)
  override val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

  private var factory: MediaPlayerFactory? = null
  private var player: EmbeddedMediaPlayer? = null
  private var frameBitmap: Bitmap? = null
  private var framePixels: ByteArray? = null
  private var currentFrame by mutableStateOf<ImageBitmap?>(null)

  override fun initialize(streamUrl: String, startPositionMs: Long) {
    release()

    if(!isNativeDiscoverySuccessful) {
      _playbackState.value = PlaybackState(
        hasError = true,
        errorMessage = "VLC libraries not found. Install VLC to enable Desktop playback.",
      )
      return
    }

    val newFactory = MediaPlayerFactory("--stereo-mode=1")
    val newPlayer = newFactory.mediaPlayers().newEmbeddedMediaPlayer()

    val videoSurface = newFactory.videoSurfaces().newVideoSurface(
      ImageBufferFormatCallback(),
      ImageRenderCallback(),
      true,
    )
    newPlayer.videoSurface().set(videoSurface)
    newPlayer.events().addMediaPlayerEventListener(VlcEventListener())

    factory = newFactory
    player = newPlayer

    newPlayer.media().play(streamUrl)
    if(startPositionMs > 0L) {
      newPlayer.controls().setTime(startPositionMs)
    }
  }

  override fun play() {
    player?.controls()?.play()
  }

  override fun pause() {
    player?.controls()?.pause()
  }

  override fun seekTo(positionMs: Long) {
    player?.controls()?.setTime(positionMs)
  }

  override fun release() {
    player?.release()
    factory?.release()
    player = null
    factory = null
    frameBitmap?.close()
    frameBitmap = null
    framePixels = null
    currentFrame = null
    _playbackState.value = PlaybackState.Idle
  }

  @Composable
  override fun VideoSurface(modifier: Modifier) {
    val frame = currentFrame
    if(frame == null) {
      Box(modifier = modifier.fillMaxSize().background(Color.Black))
      return
    }
    Image(
      bitmap = frame,
      contentDescription = null,
      contentScale = ContentScale.Fit,
      modifier = modifier.fillMaxSize().background(Color.Black),
    )
  }

  private inner class ImageBufferFormatCallback : BufferFormatCallback {
    override fun getBufferFormat(sourceWidth: Int, sourceHeight: Int): BufferFormat {
      // Allocate a Skia bitmap whose pixel storage is reused across every decoded frame. RV32 is
      // 32-bit native-endian BGRA (the only platforms vlcj runs on for Desktop are little-endian),
      // which matches Skia's BGRA_8888.
      val info = ImageInfo(
        width = sourceWidth,
        height = sourceHeight,
        colorType = ColorType.BGRA_8888,
        alphaType = ColorAlphaType.PREMUL,
      )
      val rowBytes = sourceWidth * BYTES_PER_PIXEL
      val pixels = ByteArray(rowBytes * sourceHeight)
      val bitmap = Bitmap().apply {
        // installPixels is the no-copy variant: the bitmap reads directly from `pixels` on every
        // draw, so refreshing the byte array in `display` reflects in the next composition.
        installPixels(info, pixels, rowBytes)
      }
      frameBitmap?.close()
      frameBitmap = bitmap
      framePixels = pixels
      return RV32BufferFormat(sourceWidth, sourceHeight)
    }

    override fun newFormatSize(bufferWidth: Int, bufferHeight: Int, displayWidth: Int, displayHeight: Int) = Unit

    override fun allocatedBuffers(buffers: Array<ByteBuffer>) = Unit
  }

  private inner class ImageRenderCallback : RenderCallback {
    override fun lock(mediaPlayer: MediaPlayer) = Unit

    override fun display(
      mediaPlayer: MediaPlayer,
      nativeBuffers: Array<ByteBuffer>,
      bufferFormat: BufferFormat,
      displayWidth: Int,
      displayHeight: Int,
    ) {
      val pixels = framePixels ?: return
      val bitmap = frameBitmap ?: return
      val src = nativeBuffers[0]
      src.position(0)
      src.get(pixels)
      // The bitmap's pixel storage is `pixels`, so it now reflects the new frame. Wrap it in a
      // fresh ImageBitmap each frame so Compose sees a state change and recomposes; the wrapper
      // is a thin object — no pixel copy.
      currentFrame = bitmap.asComposeImageBitmap()
    }

    override fun unlock(mediaPlayer: MediaPlayer) = Unit
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
    private const val BYTES_PER_PIXEL = 4
  }
}
