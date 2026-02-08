package com.eygraber.jellyfin.screens.music.artist.albums.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.eygraber.jellyfin.screens.music.artist.albums.ArtistAlbumItem
import com.eygraber.jellyfin.screens.music.artist.albums.ArtistDetail
import com.eygraber.jellyfin.screens.music.artist.albums.ArtistHeader

private const val GRID_COLUMNS = 2

@Composable
internal fun ArtistAlbumsGrid(
  artist: ArtistDetail?,
  albums: List<ArtistAlbumItem>,
  onAlbumClick: (albumId: String) -> Unit,
  modifier: Modifier = Modifier,
) {
  LazyVerticalGrid(
    columns = GridCells.Fixed(GRID_COLUMNS),
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    modifier = modifier.fillMaxSize(),
  ) {
    item(
      key = "artist-header",
      span = { GridItemSpan(maxLineSpan) },
    ) {
      ArtistHeader(artist = artist)
    }

    items(
      items = albums,
      key = { it.id },
    ) { album ->
      ArtistAlbumCard(
        album = album,
        onClick = { onAlbumClick(album.id) },
      )
    }
  }
}

@Composable
private fun ArtistAlbumCard(
  album: ArtistAlbumItem,
  onClick: () -> Unit,
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick),
  ) {
    Column {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(1F),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = album.name.take(1),
          style = MaterialTheme.typography.headlineMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      Column(
        modifier = Modifier.padding(8.dp),
      ) {
        Text(
          text = album.name,
          style = MaterialTheme.typography.bodySmall,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )

        val details = listOfNotNull(
          album.productionYear?.toString(),
          album.trackCount?.let { "$it tracks" },
        ).joinToString(" \u00B7 ")

        if(details.isNotEmpty()) {
          Text(
            text = details,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
    }
  }
}
