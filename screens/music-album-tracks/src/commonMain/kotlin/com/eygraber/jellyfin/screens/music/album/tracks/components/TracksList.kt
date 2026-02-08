package com.eygraber.jellyfin.screens.music.album.tracks.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.eygraber.jellyfin.screens.music.album.tracks.AlbumDetail
import com.eygraber.jellyfin.screens.music.album.tracks.AlbumHeader
import com.eygraber.jellyfin.screens.music.album.tracks.TrackItem
import com.eygraber.jellyfin.ui.icons.JellyfinIcons
import com.eygraber.jellyfin.ui.icons.PlayArrow

private const val PLAY_ICON_SIZE = 20

@Composable
internal fun TracksList(
  album: AlbumDetail?,
  tracks: List<TrackItem>,
  onTrackClick: (trackId: String) -> Unit,
  onPlayAll: () -> Unit,
  onShuffle: () -> Unit,
  onArtistClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  LazyColumn(
    contentPadding = PaddingValues(vertical = 8.dp),
    modifier = modifier.fillMaxSize(),
  ) {
    item(key = "album-header") {
      AlbumHeader(
        album = album,
        onPlayAll = onPlayAll,
        onShuffle = onShuffle,
        onArtistClick = onArtistClick,
      )
    }

    items(
      items = tracks,
      key = { it.id },
    ) { track ->
      TrackRow(
        track = track,
        onClick = { onTrackClick(track.id) },
      )

      HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5F),
      )
    }
  }
}

@Composable
private fun TrackRow(
  track: TrackItem,
  onClick: () -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    track.trackNumber?.let { number ->
      Text(
        text = number.toString(),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.width(28.dp),
      )
    }

    Column(
      modifier = Modifier.weight(1F),
    ) {
      Text(
        text = track.name,
        style = MaterialTheme.typography.bodyLarge,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }

    track.durationText?.let { duration ->
      Text(
        text = duration,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }

    IconButton(
      onClick = onClick,
      modifier = Modifier.size(32.dp),
    ) {
      Icon(
        imageVector = JellyfinIcons.PlayArrow,
        contentDescription = "Play track",
        modifier = Modifier.size(PLAY_ICON_SIZE.dp),
        tint = MaterialTheme.colorScheme.primary,
      )
    }
  }
}
