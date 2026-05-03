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
import sun.misc.Unsafe
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormatCallback
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.RenderCallback
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.format.RV32BufferFormat
import java.nio.Buffer
import java.nio.ByteBuffer

/**
 * Desktop (JVM) implementation of [VideoPlayerService] using vlcj.
 *
 * Renders video by hooking libvlc's callback video surface. Allocates one Skia [Bitmap] per stream
 * session (Skia owns the native pixel memory via [Bitmap.allocPixels]) and grabs its pixel-storage
 * address through [Bitmap.peekPixels]. Per frame, [Unsafe.copyMemory] memcpys directly from
 * vlcj's direct [ByteBuffer] (whose native address we read from [Buffer.address] via the field's
 * Unsafe offset) into the bitmap's pixel storage, then [Bitmap.notifyPixelsChanged] invalidates
 * Skia's caches and [Bitmap.asComposeImageBitmap] exposes it to Compose. Steady-state playback
 * allocates only the cheap [ImageBitmap] wrapper plus does one native-to-native memcpy — no JVM
 * byte-array marshalling and no per-frame Skia bitmap allocation.
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
// sun.misc.Unsafe pixel-copy methods (copyMemory/objectFieldOffset/getLong) are deprecated for
// removal in newer JDKs, but on JDK 17 they remain the only way to memcpy native→native and read
// a DirectByteBuffer's address without requiring --add-opens JVM flags.
@Suppress("DEPRECATION")
@SingleIn(ScreenScope::class)
@ContributesBinding(ScreenScope::class)
class JvmVideoPlayerService : VideoPlayerService {
  private val _playbackState = MutableStateFlow(PlaybackState.Idle)
  override val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

  private var factory: MediaPlayerFactory? = null
  private var player: EmbeddedMediaPlayer? = null
  private var frameBitmap: Bitmap? = null
  private var bitmapPixelsAddr: Long = 0L
  private var sourceAddr: Long = 0L
  private var pixelByteCount: Int = 0
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
    bitmapPixelsAddr = 0L
    sourceAddr = 0L
    pixelByteCount = 0
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
      // RV32 is 32-bit native-endian BGRA (the only platforms vlcj runs on for Desktop are
      // little-endian), which matches Skia's BGRA_8888. allocPixels lets Skia allocate the native
      // pixel storage so we can write to it directly via Unsafe.copyMemory and avoid the per-frame
      // JNI byte-array marshalling that installPixels(info, ByteArray, rowBytes) incurs.
      val info = ImageInfo(
        width = sourceWidth,
        height = sourceHeight,
        colorType = ColorType.BGRA_8888,
        alphaType = ColorAlphaType.PREMUL,
      )
      val rowBytes = sourceWidth * BYTES_PER_PIXEL
      val byteCount = rowBytes * sourceHeight
      val bitmap = Bitmap().apply {
        check(allocPixels(info)) { "Failed to allocate Skia bitmap pixels for ${sourceWidth}x$sourceHeight" }
      }
      // peekPixels returns a non-owning Pixmap pointing at the bitmap's pixel storage; the addr
      // stays valid as long as the bitmap doesn't reallocate.
      val pixmap = bitmap.peekPixels() ?: error("Bitmap.peekPixels returned null after allocPixels")
      val addr = pixmap.addr
      pixmap.close()

      frameBitmap?.close()
      frameBitmap = bitmap
      bitmapPixelsAddr = addr
      pixelByteCount = byteCount
      // sourceAddr will be cached when allocatedBuffers fires (or lazily on the first display call).
      sourceAddr = 0L

      return RV32BufferFormat(sourceWidth, sourceHeight)
    }

    override fun newFormatSize(bufferWidth: Int, bufferHeight: Int, displayWidth: Int, displayHeight: Int) = Unit

    override fun allocatedBuffers(buffers: Array<ByteBuffer>) {
      sourceAddr = directBufferAddress(buffers[0])
    }
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
      val bitmap = frameBitmap ?: return
      val dst = bitmapPixelsAddr
      val len = pixelByteCount
      if(dst == 0L || len == 0) return
      val src = sourceAddr.takeIf { it != 0L } ?: directBufferAddress(nativeBuffers[0]).also { sourceAddr = it }
      // Native-to-native memcpy straight from libvlc's render buffer into the Skia bitmap's pixel
      // storage. notifyPixelsChanged invalidates Skia's GPU/raster caches so the next draw picks
      // up the new bytes; asComposeImageBitmap wraps the same Bitmap in a fresh thin ImageBitmap
      // so Compose sees a state change and recomposes.
      UNSAFE.copyMemory(src, dst, len.toLong())
      bitmap.notifyPixelsChanged()
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

    // sun.misc.Unsafe lives in the jdk.unsupported module which `opens sun.misc` to all unnamed
    // modules by default in JDK 17+, so reflective access to `theUnsafe` works without any
    // --add-opens flag.
    private val UNSAFE: Unsafe = run {
      val field = Unsafe::class.java.getDeclaredField("theUnsafe")
      field.isAccessible = true
      field.get(null) as Unsafe
    }

    // Buffer.address is package-private and lives in java.base/java.nio. getDeclaredField is
    // metadata-only (no module check), and Unsafe.objectFieldOffset doesn't go through
    // setAccessible, so reading the field this way needs no --add-opens either.
    private val bufferAddressOffset: Long =
      UNSAFE.objectFieldOffset(Buffer::class.java.getDeclaredField("address"))

    private fun directBufferAddress(buffer: ByteBuffer): Long {
      check(buffer.isDirect) { "vlcj video frame buffer is unexpectedly non-direct" }
      return UNSAFE.getLong(buffer, bufferAddressOffset)
    }
  }
}
