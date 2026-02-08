package com.eygraber.jellyfin.screens.music.artist.albums

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.eygraber.jellyfin.screens.music.artist.albums.components.ArtistAlbumsGrid
import com.eygraber.jellyfin.ui.compose.PreviewJellyfinScreen
import com.eygraber.jellyfin.ui.icons.ArrowBack
import com.eygraber.jellyfin.ui.icons.FavoriteBorder
import com.eygraber.jellyfin.ui.icons.JellyfinIcons
import com.eygraber.jellyfin.ui.icons.MusicNote
import com.eygraber.jellyfin.ui.material.theme.JellyfinPreviewTheme
import com.eygraber.jellyfin.ui.material.theme.JellyfinTheme
import com.eygraber.vice.ViceView

internal typealias ArtistAlbumsView = ViceView<ArtistAlbumsIntent, ArtistAlbumsViewState>

private const val ARTIST_IMAGE_SIZE = 120
private const val MAX_OVERVIEW_LINES = 4

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
          title = {},
          navigationIcon = {
            IconButton(onClick = { onIntent(ArtistAlbumsIntent.NavigateBack) }) {
              Icon(
                imageVector = JellyfinIcons.ArrowBack,
                contentDescription = "Navigate back",
              )
            }
          },
          actions = {
            IconButton(onClick = { onIntent(ArtistAlbumsIntent.ToggleFavorite) }) {
              Icon(
                imageVector = JellyfinIcons.FavoriteBorder,
                contentDescription = "Add to favorites",
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
            onRetry = { onIntent(ArtistAlbumsIntent.RetryLoad) },
          )
          state.isEmpty -> EmptyContent()
          else -> ArtistContent(
            state = state,
            onAlbumClick = { albumId -> onIntent(ArtistAlbumsIntent.SelectAlbum(albumId)) },
          )
        }
      }
    }
  }
}

@Composable
internal fun ArtistHeader(
  artist: ArtistDetail?,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Box(
      modifier = Modifier
        .size(ARTIST_IMAGE_SIZE.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.surfaceVariant),
      contentAlignment = Alignment.Center,
    ) {
      Icon(
        imageVector = JellyfinIcons.MusicNote,
        contentDescription = null,
        modifier = Modifier.size(48.dp),
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    artist?.let { artistDetail ->
      Text(
        text = artistDetail.name,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
      )

      artistDetail.genre?.let { genre ->
        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = genre,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      artistDetail.overview?.let { overview ->
        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = overview,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = MAX_OVERVIEW_LINES,
          overflow = TextOverflow.Ellipsis,
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = "Albums",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.SemiBold,
      modifier = Modifier.fillMaxWidth(),
    )

    Spacer(modifier = Modifier.height(8.dp))
  }
}

@Composable
private fun ArtistContent(
  state: ArtistAlbumsViewState,
  onAlbumClick: (albumId: String) -> Unit,
) {
  ArtistAlbumsGrid(
    artist = state.artist,
    albums = state.albums,
    onAlbumClick = onAlbumClick,
  )
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

@Suppress("MagicNumber")
@PreviewJellyfinScreen
@Composable
private fun ArtistAlbumsContentPreview() {
  JellyfinPreviewTheme {
    ArtistAlbumsView(
      state = ArtistAlbumsViewState(
        artist = ArtistDetail(
          id = "artist-1",
          name = "Pink Floyd",
          overview = "Pink Floyd were an English rock band formed in London in 1965.",
          genre = "Progressive Rock",
          imageUrl = null,
        ),
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
