package com.eygraber.jellyfin.screens.video.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.eygraber.jellyfin.ui.icons.ArrowBack
import com.eygraber.jellyfin.ui.icons.JellyfinIcons
import com.eygraber.jellyfin.ui.icons.Pause
import com.eygraber.jellyfin.ui.icons.PlayArrow
import com.eygraber.vice.ViceView

internal typealias VideoPlayerView = ViceView<VideoPlayerIntent, VideoPlayerViewState>

@Composable
internal fun VideoPlayerView(
  state: VideoPlayerViewState,
  onIntent: (VideoPlayerIntent) -> Unit,
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Black)
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = { onIntent(VideoPlayerIntent.ToggleControls) },
      ),
    contentAlignment = Alignment.Center,
  ) {
    when {
      state.isLoading -> LoadingContent()

      state.error != null -> ErrorContent(
        error = state.error,
        onRetry = { onIntent(VideoPlayerIntent.RetryLoad) },
        onBack = { onIntent(VideoPlayerIntent.NavigateBack) },
      )

      else -> PlayerContent(
        state = state,
        onIntent = onIntent,
      )
    }
  }
}

@Composable
private fun LoadingContent() {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    CircularProgressIndicator(
      color = Color.White,
    )
    Spacer(modifier = Modifier.size(16.dp))
    Text(
      text = "Loading...",
      color = Color.White,
      style = MaterialTheme.typography.bodyLarge,
    )
  }
}

@Composable
private fun ErrorContent(
  error: VideoPlayerError,
  onRetry: () -> Unit,
  onBack: () -> Unit,
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
    modifier = Modifier.padding(32.dp),
  ) {
    Text(
      text = error.message,
      color = Color.White,
      style = MaterialTheme.typography.bodyLarge,
    )
    Spacer(modifier = Modifier.size(16.dp))
    Row(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      TextButton(onClick = onBack) {
        Text("Go Back", color = Color.White)
      }
      TextButton(onClick = onRetry) {
        Text("Retry", color = Color.White)
      }
    }
  }
}

@Composable
private fun PlayerContent(
  state: VideoPlayerViewState,
  onIntent: (VideoPlayerIntent) -> Unit,
) {
  Box(modifier = Modifier.fillMaxSize()) {
    // Buffering indicator
    if(state.isBuffering) {
      CircularProgressIndicator(
        color = Color.White,
        modifier = Modifier.align(Alignment.Center),
      )
    }

    // Controls overlay
    AnimatedVisibility(
      visible = state.isControlsVisible,
      enter = fadeIn(),
      exit = fadeOut(),
    ) {
      PlayerControlsOverlay(
        state = state,
        onIntent = onIntent,
      )
    }
  }
}

@Suppress("LongMethod")
@Composable
private fun PlayerControlsOverlay(
  state: VideoPlayerViewState,
  onIntent: (VideoPlayerIntent) -> Unit,
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Black.copy(alpha = CONTROLS_OVERLAY_ALPHA)),
  ) {
    // Top bar with back button and title
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .align(Alignment.TopStart),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      IconButton(onClick = { onIntent(VideoPlayerIntent.NavigateBack) }) {
        Icon(
          imageVector = JellyfinIcons.ArrowBack,
          contentDescription = "Back",
          tint = Color.White,
        )
      }
      if(state.title.isNotEmpty()) {
        Text(
          text = state.title,
          color = Color.White,
          style = MaterialTheme.typography.titleMedium,
          modifier = Modifier.padding(start = 8.dp),
        )
      }
    }

    // Center play/pause button
    IconButton(
      onClick = {
        if(state.isPlaying) {
          onIntent(VideoPlayerIntent.Pause)
        }
        else {
          onIntent(VideoPlayerIntent.Play)
        }
      },
      modifier = Modifier
        .align(Alignment.Center)
        .size(CENTER_BUTTON_SIZE.dp),
    ) {
      Icon(
        imageVector = if(state.isPlaying) JellyfinIcons.Pause else JellyfinIcons.PlayArrow,
        contentDescription = if(state.isPlaying) "Pause" else "Play",
        tint = Color.White,
        modifier = Modifier.size(CENTER_ICON_SIZE.dp),
      )
    }

    // Bottom progress bar and time
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .align(Alignment.BottomStart),
    ) {
      // Seek bar
      Slider(
        value = state.progress,
        onValueChange = { fraction ->
          val positionMs = (fraction * state.durationMs).toLong()
          onIntent(VideoPlayerIntent.SeekTo(positionMs))
        },
        colors = SliderDefaults.colors(
          thumbColor = MaterialTheme.colorScheme.primary,
          activeTrackColor = MaterialTheme.colorScheme.primary,
          inactiveTrackColor = Color.White.copy(alpha = INACTIVE_TRACK_ALPHA),
        ),
        modifier = Modifier.fillMaxWidth(),
      )

      // Time display
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text(
          text = formatDuration(state.currentPositionMs),
          color = Color.White,
          style = MaterialTheme.typography.bodySmall,
        )
        Text(
          text = formatDuration(state.durationMs),
          color = Color.White,
          style = MaterialTheme.typography.bodySmall,
        )
      }

      // Buffer indicator
      if(state.bufferedPositionMs > 0 && state.durationMs > 0) {
        LinearProgressIndicator(
          progress = {
            (state.bufferedPositionMs.toFloat() / state.durationMs.toFloat())
              .coerceIn(minimumValue = 0f, maximumValue = 1f)
          },
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp),
          color = Color.White.copy(alpha = BUFFER_INDICATOR_ALPHA),
          trackColor = Color.Transparent,
        )
      }
    }
  }
}

@Suppress("MagicNumber")
private fun formatDuration(durationMs: Long): String {
  val totalSeconds = durationMs / 1_000L
  val hours = totalSeconds / 3_600L
  val minutes = totalSeconds % 3_600L / 60L
  val seconds = totalSeconds % 60L

  return buildString {
    if(hours > 0) {
      append(hours)
      append(':')
      append(minutes.toString().padStart(length = 2, padChar = '0'))
      append(':')
    }
    else {
      append(minutes)
      append(':')
    }
    append(seconds.toString().padStart(length = 2, padChar = '0'))
  }
}

private const val CONTROLS_OVERLAY_ALPHA = 0.5f
private const val INACTIVE_TRACK_ALPHA = 0.3f
private const val BUFFER_INDICATOR_ALPHA = 0.4f
private const val CENTER_BUTTON_SIZE = 72
private const val CENTER_ICON_SIZE = 48
