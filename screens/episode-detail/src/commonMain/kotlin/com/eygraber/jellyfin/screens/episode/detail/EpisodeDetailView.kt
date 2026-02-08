package com.eygraber.jellyfin.screens.episode.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eygraber.jellyfin.ui.compose.PreviewJellyfinScreen
import com.eygraber.jellyfin.ui.icons.ArrowBack
import com.eygraber.jellyfin.ui.icons.JellyfinIcons
import com.eygraber.jellyfin.ui.icons.PlayArrow
import com.eygraber.jellyfin.ui.material.theme.JellyfinPreviewTheme
import com.eygraber.jellyfin.ui.material.theme.JellyfinTheme
import com.eygraber.vice.ViceView

internal typealias EpisodeDetailView = ViceView<EpisodeDetailIntent, EpisodeDetailViewState>

private const val THUMBNAIL_ASPECT_RATIO = 16F / 9F
private const val PLAY_ICON_SIZE = 18

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EpisodeDetailView(
  state: EpisodeDetailViewState,
  onIntent: (EpisodeDetailIntent) -> Unit,
) {
  JellyfinTheme {
    Scaffold(
      topBar = {
        TopAppBar(
          title = {},
          navigationIcon = {
            IconButton(onClick = { onIntent(EpisodeDetailIntent.NavigateBack) }) {
              Icon(
                imageVector = JellyfinIcons.ArrowBack,
                contentDescription = "Navigate back",
              )
            }
          },
          colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
          ),
        )
      },
    ) { contentPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(contentPadding),
      ) {
        when {
          state.isLoading -> LoadingContent()
          state.error != null -> ErrorContent(
            error = state.error,
            onRetry = { onIntent(EpisodeDetailIntent.RetryLoad) },
          )
          state.episode != null -> EpisodeContent(episode = state.episode)
        }
      }
    }
  }
}

@Composable
private fun EpisodeContent(
  episode: EpisodeDetail,
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState()),
  ) {
    ThumbnailSection(episode = episode)

    Column(
      modifier = Modifier.padding(horizontal = 16.dp),
    ) {
      Spacer(modifier = Modifier.height(16.dp))

      episode.seriesName?.let { seriesName ->
        Text(
          text = seriesName,
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(4.dp))
      }

      Text(
        text = episode.name,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
      )

      Spacer(modifier = Modifier.height(8.dp))

      MetadataRow(episode = episode)

      Spacer(modifier = Modifier.height(16.dp))

      Button(
        onClick = { },
      ) {
        Icon(
          imageVector = JellyfinIcons.PlayArrow,
          contentDescription = null,
          modifier = Modifier.size(PLAY_ICON_SIZE.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Play")
      }

      episode.overview?.let { overview ->
        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "Overview",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = overview,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun ThumbnailSection(
  episode: EpisodeDetail,
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .aspectRatio(THUMBNAIL_ASPECT_RATIO),
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.surfaceVariant),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = episode.name.take(1),
        style = MaterialTheme.typography.displayLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

@Composable
private fun MetadataRow(
  episode: EpisodeDetail,
) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    episode.seasonEpisodeLabel?.let { label ->
      Text(
        text = label,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }

    episode.runtimeMinutes?.let { minutes ->
      Text(
        text = formatRuntime(minutes),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

private fun formatRuntime(minutes: Int): String {
  val hours = minutes / 60
  val remainingMinutes = minutes % 60
  return if(hours > 0) {
    "${hours}h ${remainingMinutes}m"
  }
  else {
    "${remainingMinutes}m"
  }
}

@Composable
private fun LoadingContent() {
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center,
  ) {
    CircularProgressIndicator()
  }
}

@Composable
private fun ErrorContent(
  error: EpisodeDetailError,
  onRetry: () -> Unit,
) {
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Text(
      text = error.message,
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.error,
    )

    Spacer(modifier = Modifier.height(16.dp))

    Button(onClick = onRetry) {
      Text("Retry")
    }
  }
}

@PreviewJellyfinScreen
@Composable
private fun EpisodeDetailLoadingPreview() {
  JellyfinPreviewTheme {
    EpisodeDetailView(
      state = EpisodeDetailViewState.Loading,
      onIntent = {},
    )
  }
}

@PreviewJellyfinScreen
@Composable
private fun EpisodeDetailErrorPreview() {
  JellyfinPreviewTheme {
    EpisodeDetailView(
      state = EpisodeDetailViewState(
        isLoading = false,
        error = EpisodeDetailError.Network(),
      ),
      onIntent = {},
    )
  }
}

@Suppress("MagicNumber")
@PreviewJellyfinScreen
@Composable
private fun EpisodeDetailContentPreview() {
  JellyfinPreviewTheme {
    EpisodeDetailView(
      state = EpisodeDetailViewState(
        isLoading = false,
        episode = EpisodeDetail(
          id = "1",
          name = "Pilot",
          seriesName = "Breaking Bad",
          seasonEpisodeLabel = "Episode 1",
          overview = "Diagnosed with terminal lung cancer, chemistry teacher Walter White " +
            "teams up with former student Jesse Pinkman to cook and sell crystal meth.",
          runtimeMinutes = 58,
          thumbnailImageUrl = null,
        ),
      ),
      onIntent = {},
    )
  }
}
