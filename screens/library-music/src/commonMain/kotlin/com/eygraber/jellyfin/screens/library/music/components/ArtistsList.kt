package com.eygraber.jellyfin.screens.library.music.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.eygraber.jellyfin.screens.library.music.ArtistItem

private const val LOAD_MORE_THRESHOLD = 10

@Composable
internal fun ArtistsList(
  artists: List<ArtistItem>,
  isLoadingMore: Boolean,
  hasMore: Boolean,
  onArtistClick: (artistId: String) -> Unit,
  onLoadMore: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val listState = rememberLazyListState()
  val currentOnLoadMore by rememberUpdatedState(onLoadMore)

  val shouldLoadMore by remember {
    derivedStateOf {
      val layoutInfo = listState.layoutInfo
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

  LazyColumn(
    state = listState,
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    modifier = modifier.fillMaxSize(),
  ) {
    items(
      items = artists,
      key = { it.id },
    ) { artist ->
      ArtistCard(
        artist = artist,
        onClick = { onArtistClick(artist.id) },
      )
    }

    if(isLoadingMore) {
      item {
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
private fun ArtistCard(
  artist: ArtistItem,
  onClick: () -> Unit,
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Box(
        modifier = Modifier
          .size(artistImageSize)
          .clip(CircleShape),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = artist.name.take(1).uppercase(),
          style = MaterialTheme.typography.titleLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      Column(
        modifier = Modifier.weight(1F),
      ) {
        Text(
          text = artist.name,
          style = MaterialTheme.typography.titleMedium,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )

        artist.albumCount?.let { count ->
          Text(
            text = "$count album${if(count != 1) "s" else ""}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
    }
  }
}

private val artistImageSize = 56.dp
