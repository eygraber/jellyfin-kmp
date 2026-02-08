package com.eygraber.jellyfin.screens.music.artist.albums

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
import com.eygraber.jellyfin.screens.music.artist.albums.components.ArtistAlbumsGrid
import com.eygraber.jellyfin.ui.compose.PreviewJellyfinScreen
import com.eygraber.jellyfin.ui.icons.ArrowBack
import com.eygraber.jellyfin.ui.icons.JellyfinIcons
import com.eygraber.jellyfin.ui.material.theme.JellyfinPreviewTheme
import com.eygraber.jellyfin.ui.material.theme.JellyfinTheme
import com.eygraber.vice.ViceView

internal typealias ArtistAlbumsView = ViceView<ArtistAlbumsIntent, ArtistAlbumsViewState>

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ArtistAlbumsView(
  state: ArtistAlbumsViewState,
  onIntent: (ArtistAlbumsIntent) -> Unit,
) {
  JellyfinTheme {
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text(state.artistName.ifEmpty { "Albums" }) },
          navigationIcon = {
            IconButton(onClick = { onIntent(ArtistAlbumsIntent.NavigateBack) }) {
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
            onRetry = { onIntent(ArtistAlbumsIntent.RetryLoad) },
          )
          state.isEmpty -> EmptyContent()
          else -> ArtistAlbumsGrid(
            albums = state.albums,
            onAlbumClick = { albumId -> onIntent(ArtistAlbumsIntent.SelectAlbum(albumId)) },
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
  error: ArtistAlbumsError,
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
      text = "No albums found",
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@PreviewJellyfinScreen
@Composable
private fun ArtistAlbumsContentPreview() {
  JellyfinPreviewTheme {
    ArtistAlbumsView(
      state = ArtistAlbumsViewState(
        artistName = "Pink Floyd",
        isLoading = false,
        albums = listOf(
          ArtistAlbumItem(
            id = "1",
            name = "The Dark Side of the Moon",
            productionYear = 1973,
            trackCount = 10,
            imageUrl = null,
          ),
          ArtistAlbumItem(
            id = "2",
            name = "Wish You Were Here",
            productionYear = 1975,
            trackCount = 5,
            imageUrl = null,
          ),
        ),
      ),
      onIntent = {},
    )
  }
}
