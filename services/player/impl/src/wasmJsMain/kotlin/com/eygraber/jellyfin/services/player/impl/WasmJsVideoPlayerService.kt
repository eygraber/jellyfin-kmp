package com.eygraber.jellyfin.services.player.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.eygraber.jellyfin.di.scopes.ScreenScope
import com.eygraber.jellyfin.services.player.PlaybackState
import com.eygraber.jellyfin.services.player.VideoPlayerService
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.browser.document
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.w3c.dom.HTMLVideoElement
import kotlin.js.ExperimentalWasmJsInterop

/**
 * Web (WasmJs) implementation of [VideoPlayerService].
 *
 * Creates an `<video>` element overlay positioned fullscreen above the Compose canvas. Uses the
 * browser's native HTML5 controls so the user can play/pause/seek; the Compose controls overlay
 * is hidden behind the video. To exit playback, the user navigates back via the browser or by
 * triggering Compose's NavigateBack intent (which calls [release] and removes the element).
 */
@OptIn(ExperimentalWasmJsInterop::class)
@SingleIn(ScreenScope::class)
@ContributesBinding(ScreenScope::class)
class WasmJsVideoPlayerService : VideoPlayerService {
  private val _playbackState = MutableStateFlow(PlaybackState.Idle)
  override val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

  private var videoElement: HTMLVideoElement? = null

  override fun initialize(streamUrl: String, startPositionMs: Long) {
    release()

    val video = (document.createElement("video") as HTMLVideoElement).apply {
      src = streamUrl
      controls = true
      autoplay = true
      style.cssText = OVERLAY_STYLE
      if(startPositionMs > 0L) {
        currentTime = startPositionMs.toDouble() / MS_PER_SECOND
      }
    }

    video.addEventListener("playing") { syncStateFromElement(isPlaying = true) }
    video.addEventListener("pause") { syncStateFromElement(isPlaying = false) }
    video.addEventListener("waiting") { syncStateFromElement(isBuffering = true) }
    video.addEventListener("canplay") { syncStateFromElement(isBuffering = false) }
    video.addEventListener("ended") { syncStateFromElement(isPlaying = false, isEnded = true) }
    video.addEventListener("timeupdate") { syncStateFromElement() }
    video.addEventListener("error") {
      _playbackState.value = _playbackState.value.copy(
        hasError = true,
        errorMessage = "Playback error",
      )
    }

    document.body?.appendChild(video)
    videoElement = video
  }

  override fun play() {
    videoElement?.play()
  }

  override fun pause() {
    videoElement?.pause()
  }

  override fun seekTo(positionMs: Long) {
    videoElement?.currentTime = positionMs.toDouble() / MS_PER_SECOND
  }

  override fun release() {
    val element = videoElement ?: return
    videoElement = null
    element.pause()
    element.removeAttribute("src")
    element.parentNode?.removeChild(element)
    _playbackState.value = PlaybackState.Idle
  }

  @Composable
  override fun VideoSurface(modifier: Modifier) {
    Box(modifier = modifier.fillMaxSize().background(Color.Black))
  }

  private fun syncStateFromElement(
    isPlaying: Boolean? = null,
    isBuffering: Boolean? = null,
    isEnded: Boolean = false,
  ) {
    val element = videoElement ?: return
    val current = _playbackState.value
    val durationMs = element.duration
      .takeIf { !it.isNaN() && !it.isInfinite() && it > 0.0 }
      ?.let { (it * MS_PER_SECOND).toLong() }
      ?: 0L
    val currentMs = (element.currentTime * MS_PER_SECOND).toLong().coerceAtLeast(0L)

    _playbackState.value = current.copy(
      isPlaying = isPlaying ?: !element.paused,
      isBuffering = isBuffering ?: false,
      isEnded = isEnded || current.isEnded && isPlaying != true,
      currentPositionMs = currentMs,
      durationMs = durationMs,
      bufferedPositionMs = currentMs,
    )
  }

  companion object {
    private const val MS_PER_SECOND = 1_000.0
    private const val OVERLAY_STYLE =
      "position:fixed;top:0;left:0;width:100vw;height:100vh;background:black;z-index:9999;"
  }
}
