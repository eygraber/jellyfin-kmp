package com.eygraber.jellyfin.screens.library.music.components

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
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.eygraber.jellyfin.screens.library.music.AlbumItem

private const val LOAD_MORE_THRESHOLD = 10

@Composable
internal fun AlbumsGrid(
  albums: List<AlbumItem>,
  isLoadingMore: Boolean,
  hasMore: Boolean,
  onAlbumClick: (albumId: String) -> Unit,
  onLoadMore: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val gridState = rememberLazyGridState()
  val currentOnLoadMore by rememberUpdatedState(onLoadMore)

  val shouldLoadMore by remember {
    derivedStateOf {
      val layoutInfo = gridState.layoutInfo
      val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
      val totalItems = layoutInfo.totalItemsCount
      hasMore && !isLoadingMore && lastVisibleIndex >= totalItems - LOAD_MORE_THRESHOLD
    }
  }

  LaunchedEffect(shouldLoadMore) {
    if(shouldLoadMore) {
      currentOnLoadMore()
    }
  }

  LazyVerticalGrid(
    columns = GridCells.Adaptive(minSize = 140.dp),
    state = gridState,
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    modifier = modifier.fillMaxSize(),
  ) {
    items(
      items = albums,
      key = { it.id },
    ) { album ->
      AlbumCard(
        album = album,
        onClick = { onAlbumClick(album.id) },
      )
    }

    if(isLoadingMore) {
      item(
        span = { GridItemSpan(maxLineSpan) },
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          contentAlignment = Alignment.Center,
        ) {
          CircularProgressIndicator()
        }
      }
    }
  }
}

@Composable
private fun AlbumCard(
  album: AlbumItem,
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

        album.artistName?.let { artist ->
          Text(
            text = artist,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        }

        album.productionYear?.let { year ->
          Text(
            text = year.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
    }
  }
}
