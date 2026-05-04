package com.eygraber.jellyfin.screens.library.tvshows.components

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
import com.eygraber.jellyfin.screens.library.tvshows.TvShowItem
import com.eygraber.jellyfin.ui.material.image.JellyfinAsyncImage

private const val POSTER_ASPECT_RATIO = 2F / 3F
private const val LOAD_MORE_THRESHOLD = 10
private const val TITLE_LINES = 2

@Composable
internal fun TvShowPosterGrid(
  items: List<TvShowItem>,
  isLoadingMore: Boolean,
  hasMore: Boolean,
  selectedItemId: String?,
  onShowClick: (showId: String) -> Unit,
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

  LaunchedEffect(items, selectedItemId) {
    if(selectedItemId != null &&
      items.isNotEmpty() &&
      gridState.firstVisibleItemIndex == 0 &&
      gridState.firstVisibleItemScrollOffset == 0
    ) {
      val index = items.indexOfFirst { it.id == selectedItemId }
      if(index >= 0) gridState.scrollToItem(index)
    }
  }

  LazyVerticalGrid(
    columns = GridCells.Adaptive(minSize = 120.dp),
    state = gridState,
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    modifier = modifier.fillMaxSize(),
  ) {
    items(
      items = items,
      key = { it.id },
    ) { show ->
      TvShowPosterCard(
        show = show,
        onClick = { onShowClick(show.id) },
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
private fun TvShowPosterCard(
  show: TvShowItem,
  onClick: () -> Unit,
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick),
  ) {
    Column {
      JellyfinAsyncImage(
        model = show.imageUrl,
        contentDescription = show.name,
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(POSTER_ASPECT_RATIO),
        fallback = {
          Text(
            text = show.name.take(1),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        },
      )

      Column(
        modifier = Modifier.padding(8.dp),
      ) {
        Text(
          text = show.name,
          style = MaterialTheme.typography.bodySmall,
          minLines = TITLE_LINES,
          maxLines = TITLE_LINES,
          overflow = TextOverflow.Ellipsis,
        )

        // Reserve a fixed line for the year so cards stay the same height regardless of metadata.
        Text(
          text = show.productionYear?.toString() ?: " ",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
        )

        // Reserve a fixed line for the season count so cards stay the same height regardless of metadata.
        Text(
          text = show.seasonCount?.let { count -> "$count season${if(count != 1) "s" else ""}" } ?: " ",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.primary,
          maxLines = 1,
        )
      }
    }
  }
}
