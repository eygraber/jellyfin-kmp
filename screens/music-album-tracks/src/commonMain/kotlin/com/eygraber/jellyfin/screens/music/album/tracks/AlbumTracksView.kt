package com.eygraber.jellyfin.screens.music.album.tracks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.eygraber.jellyfin.screens.music.album.tracks.components.TracksList
import com.eygraber.jellyfin.ui.compose.PreviewJellyfinScreen
import com.eygraber.jellyfin.ui.icons.ArrowBack
import com.eygraber.jellyfin.ui.icons.FavoriteBorder
import com.eygraber.jellyfin.ui.icons.JellyfinIcons
import com.eygraber.jellyfin.ui.icons.MusicNote
import com.eygraber.jellyfin.ui.icons.PlayArrow
import com.eygraber.jellyfin.ui.icons.Shuffle
import com.eygraber.jellyfin.ui.material.theme.JellyfinPreviewTheme
import com.eygraber.jellyfin.ui.material.theme.JellyfinTheme
import com.eygraber.vice.ViceView

internal typealias AlbumTracksView = ViceView<AlbumTracksIntent, AlbumTracksViewState>

private const val ALBUM_ART_ASPECT_RATIO = 1F
private const val BUTTON_ICON_SIZE = 18

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
          title = {},
          navigationIcon = {
            IconButton(onClick = { onIntent(AlbumTracksIntent.NavigateBack) }) {
              Icon(
                imageVector = JellyfinIcons.ArrowBack,
                contentDescription = "Navigate back",
              )
            }
          },
          actions = {
            IconButton(onClick = { onIntent(AlbumTracksIntent.ToggleFavorite) }) {
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
            onRetry = { onIntent(AlbumTracksIntent.RetryLoad) },
          )
          state.isEmpty -> EmptyContent()
          else -> AlbumContent(
            state = state,
            onIntent = onIntent,
          )
        }
      }
    }
  }
}

@Composable
private fun AlbumContent(
  state: AlbumTracksViewState,
  onIntent: (AlbumTracksIntent) -> Unit,
) {
  TracksList(
    album = state.album,
    tracks = state.tracks,
    onTrackClick = { trackId -> onIntent(AlbumTracksIntent.SelectTrack(trackId)) },
    onPlayAll = { onIntent(AlbumTracksIntent.PlayAll) },
    onShuffle = { onIntent(AlbumTracksIntent.ShufflePlay) },
    onArtistClick = { onIntent(AlbumTracksIntent.NavigateToArtist) },
  )
}

@Composable
internal fun AlbumHeader(
  album: AlbumDetail?,
  onPlayAll: () -> Unit,
  onShuffle: () -> Unit,
  onArtistClick: () -> Unit,
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
        .fillMaxWidth(fraction = 0.6f)
        .aspectRatio(ALBUM_ART_ASPECT_RATIO)
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

    album?.let { albumDetail ->
      Text(
        text = albumDetail.name,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
      )

      if(albumDetail.artistName.isNotEmpty()) {
        Text(
          text = albumDetail.artistName,
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.primary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.clickable(
            enabled = albumDetail.artistId != null,
            onClick = onArtistClick,
          ),
        )
      }

      val details = listOfNotNull(
        albumDetail.productionYear?.toString(),
        albumDetail.genre,
      ).joinToString(" \u00B7 ")

      if(details.isNotEmpty()) {
        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = details,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Row(
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Button(
        onClick = onPlayAll,
        modifier = Modifier.weight(1F),
      ) {
        Icon(
          imageVector = JellyfinIcons.PlayArrow,
          contentDescription = null,
          modifier = Modifier.size(BUTTON_ICON_SIZE.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Play All")
      }

      OutlinedButton(
        onClick = onShuffle,
        modifier = Modifier.weight(1F),
      ) {
        Icon(
          imageVector = JellyfinIcons.Shuffle,
          contentDescription = null,
          modifier = Modifier.size(BUTTON_ICON_SIZE.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Shuffle")
      }
    }

    Spacer(modifier = Modifier.height(8.dp))
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

@Suppress("MagicNumber")
@PreviewJellyfinScreen
@Composable
private fun AlbumTracksContentPreview() {
  JellyfinPreviewTheme {
    AlbumTracksView(
      state = AlbumTracksViewState(
        album = AlbumDetail(
          id = "album-1",
          name = "The Dark Side of the Moon",
          artistName = "Pink Floyd",
          artistId = "artist-1",
          productionYear = 1973,
          genre = "Progressive Rock",
          albumArtUrl = null,
        ),
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
