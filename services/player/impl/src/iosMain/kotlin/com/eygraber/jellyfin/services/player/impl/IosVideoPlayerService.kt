package com.eygraber.jellyfin.services.player.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.UIKitViewController
import com.eygraber.jellyfin.di.scopes.ScreenScope
import com.eygraber.jellyfin.services.player.PlaybackState
import com.eygraber.jellyfin.services.player.VideoPlayerService
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerItemDidPlayToEndTimeNotification
import platform.AVFoundation.AVPlayerItemFailedToPlayToEndTimeErrorKey
import platform.AVFoundation.AVPlayerItemFailedToPlayToEndTimeNotification
import platform.AVFoundation.AVPlayerItemStatusFailed
import platform.AVFoundation.AVPlayerStatusFailed
import platform.AVFoundation.AVPlayerTimeControlStatusPlaying
import platform.AVFoundation.AVPlayerTimeControlStatusWaitingToPlayAtSpecifiedRate
import platform.AVFoundation.addPeriodicTimeObserverForInterval
import platform.AVFoundation.currentItem
import platform.AVFoundation.currentTime
import platform.AVFoundation.duration
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.removeTimeObserver
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.AVFoundation.seekToTime
import platform.AVFoundation.timeControlStatus
import platform.AVKit.AVPlayerViewController
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.NSError
import platform.Foundation.NSNotification
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSURL
import platform.darwin.NSEC_PER_SEC

/**
 * iOS implementation of [VideoPlayerService] using AVPlayer.
 *
 * Renders video via [AVPlayerViewController] hosted in a [UIKitViewController] composable. State
 * updates are driven by a periodic time observer plus notification observers for end-of-playback
 * and item failures. Native controls are disabled because the screen layers its own controls
 * overlay.
 */
@OptIn(ExperimentalForeignApi::class)
@SingleIn(ScreenScope::class)
@ContributesBinding(ScreenScope::class)
class IosVideoPlayerService : VideoPlayerService {
  private val _playbackState = MutableStateFlow(PlaybackState.Idle)
  override val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

  private var player: AVPlayer? = null
  private var timeObserver: Any? = null
  private var endObserver: Any? = null
  private var failureObserver: Any? = null

  override fun initialize(streamUrl: String, startPositionMs: Long) {
    release()

    val url = NSURL.URLWithString(streamUrl) ?: run {
      _playbackState.value = PlaybackState(
        hasError = true,
        errorMessage = "Invalid stream URL",
      )
      return
    }

    val item = AVPlayerItem.playerItemWithURL(url)
    val newPlayer = AVPlayer.playerWithPlayerItem(item)

    if(startPositionMs > 0L) {
      newPlayer.seekToTime(secondsToCmTime(startPositionMs.toDouble() / MS_PER_SECOND))
    }

    timeObserver = newPlayer.addPeriodicTimeObserverForInterval(
      interval = secondsToCmTime(TIME_OBSERVER_INTERVAL_SECONDS),
      queue = null,
    ) { _ ->
      updateState(newPlayer)
    }

    endObserver = NSNotificationCenter.defaultCenter.addObserverForName(
      name = AVPlayerItemDidPlayToEndTimeNotification,
      `object` = item,
      queue = null,
    ) { _: NSNotification? ->
      _playbackState.value = _playbackState.value.copy(
        isPlaying = false,
        isBuffering = false,
        isEnded = true,
      )
    }

    failureObserver = NSNotificationCenter.defaultCenter.addObserverForName(
      name = AVPlayerItemFailedToPlayToEndTimeNotification,
      `object` = item,
      queue = null,
    ) { notification: NSNotification? ->
      val error = notification?.userInfo?.get(AVPlayerItemFailedToPlayToEndTimeErrorKey) as? NSError
      _playbackState.value = _playbackState.value.copy(
        hasError = true,
        errorMessage = error?.localizedDescription ?: "Playback failed",
      )
    }

    player = newPlayer
    newPlayer.play()
    updateState(newPlayer)
  }

  override fun play() {
    player?.play()
  }

  override fun pause() {
    player?.pause()
  }

  override fun seekTo(positionMs: Long) {
    player?.seekToTime(secondsToCmTime(positionMs.toDouble() / MS_PER_SECOND))
  }

  override fun release() {
    player?.let { current ->
      timeObserver?.let(current::removeTimeObserver)
      current.pause()
      current.replaceCurrentItemWithPlayerItem(null)
    }
    timeObserver = null
    endObserver?.let { NSNotificationCenter.defaultCenter.removeObserver(it) }
    failureObserver?.let { NSNotificationCenter.defaultCenter.removeObserver(it) }
    endObserver = null
    failureObserver = null
    player = null
    _playbackState.value = PlaybackState.Idle
  }

  @Composable
  override fun VideoSurface(modifier: Modifier) {
    val currentPlayer = player
    if(currentPlayer == null) {
      Box(modifier = modifier.fillMaxSize().background(Color.Black))
      return
    }

    val controller = remember(currentPlayer) {
      AVPlayerViewController().apply {
        player = currentPlayer
        showsPlaybackControls = false
      }
    }

    UIKitViewController(
      factory = { controller },
      modifier = modifier,
    )
  }

  private fun updateState(player: AVPlayer) {
    val item = player.currentItem
    val durationSeconds = item?.duration?.let { CMTimeGetSeconds(it) } ?: 0.0
    val durationMs = secondsToMs(durationSeconds)
    val currentMs = secondsToMs(CMTimeGetSeconds(player.currentTime()))

    val timeControl = player.timeControlStatus
    val isPlaying = timeControl == AVPlayerTimeControlStatusPlaying
    val isBuffering = timeControl == AVPlayerTimeControlStatusWaitingToPlayAtSpecifiedRate

    val hasError = player.status == AVPlayerStatusFailed || item?.status == AVPlayerItemStatusFailed
    val errorMessage = if(hasError) {
      player.error?.localizedDescription ?: item?.error?.localizedDescription ?: "Playback error"
    }
    else {
      null
    }

    _playbackState.value = PlaybackState(
      isPlaying = isPlaying,
      isBuffering = isBuffering,
      isEnded = _playbackState.value.isEnded,
      hasError = hasError,
      errorMessage = errorMessage,
      currentPositionMs = currentMs,
      durationMs = durationMs,
      bufferedPositionMs = currentMs,
    )
  }

  private fun secondsToMs(seconds: Double): Long =
    if(seconds.isFinite() && seconds > 0.0) {
      (seconds * MS_PER_SECOND).toLong()
    }
    else {
      0L
    }

  private fun secondsToCmTime(seconds: Double) = CMTimeMakeWithSeconds(
    seconds = seconds,
    preferredTimescale = NSEC_PER_SEC.toInt(),
  )

  companion object {
    private const val MS_PER_SECOND = 1_000.0
    private const val TIME_OBSERVER_INTERVAL_SECONDS = 0.5
  }
}
