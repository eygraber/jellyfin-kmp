package com.eygraber.jellyfin.screens.video.player

sealed interface VideoPlayerIntent {
  data object Play : VideoPlayerIntent
  data object Pause : VideoPlayerIntent
  data class SeekTo(val positionMs: Long) : VideoPlayerIntent
  data object ToggleControls : VideoPlayerIntent
  data object NavigateBack : VideoPlayerIntent
  data object RetryLoad : VideoPlayerIntent
}
