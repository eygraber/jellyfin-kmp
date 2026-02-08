package com.eygraber.jellyfin.screens.music.album.tracks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eygraber.jellyfin.screens.music.album.tracks.components.TracksList
import com.eygraber.jellyfin.ui.compose.PreviewJellyfinScreen
import com.eygraber.jellyfin.ui.icons.ArrowBack
import com.eygraber.jellyfin.ui.icons.JellyfinIcons
import com.eygraber.jellyfin.ui.material.theme.JellyfinPreviewTheme
import com.eygraber.jellyfin.ui.material.theme.JellyfinTheme
import com.eygraber.vice.ViceView

internal typealias AlbumTracksView = ViceView<AlbumTracksIntent, AlbumTracksViewState>

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AlbumTracksView(
  state: AlbumTracksViewState,
  onIntent: (AlbumTracksIntent) -> Unit,
) {
  JellyfinTheme {
    Scaffold(
      topBar = {
        TopAppBar(
          title = {
            Column {
              Text(state.albumName.ifEmpty { "Tracks" })
              if(state.artistName.isNotEmpty()) {
                Text(
                  text = state.artistName,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
            }
          },
          navigationIcon = {
            IconButton(onClick = { onIntent(AlbumTracksIntent.NavigateBack) }) {
              Icon(
                imageVector = JellyfinIcons.ArrowBack,
                contentDescription = "Navigate back",
              )
            }
          },
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
            onRetry = { onIntent(AlbumTracksIntent.RetryLoad) },
          )
          state.isEmpty -> EmptyContent()
          else -> TracksList(
            tracks = state.tracks,
            onTrackClick = { trackId -> onIntent(AlbumTracksIntent.SelectTrack(trackId)) },
          )
        }
      }
    }
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
  error: AlbumTracksError,
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

@Composable
private fun EmptyContent() {
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Text(
      text = "No tracks found",
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@PreviewJellyfinScreen
@Composable
private fun AlbumTracksContentPreview() {
  JellyfinPreviewTheme {
    AlbumTracksView(
      state = AlbumTracksViewState(
        albumName = "The Dark Side of the Moon",
        artistName = "Pink Floyd",
        isLoading = false,
        tracks = listOf(
          TrackItem(
            id = "1",
            name = "Speak to Me",
            trackNumber = 1,
            durationText = "1:30",
          ),
          TrackItem(
            id = "2",
            name = "Breathe (In the Air)",
            trackNumber = 2,
            durationText = "2:43",
          ),
          TrackItem(
            id = "3",
            name = "On the Run",
            trackNumber = 3,
            durationText = "3:36",
          ),
        ),
      ),
      onIntent = {},
    )
  }
}
