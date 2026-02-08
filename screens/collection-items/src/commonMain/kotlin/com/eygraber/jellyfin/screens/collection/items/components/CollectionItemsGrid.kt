package com.eygraber.jellyfin.screens.collection.items.components

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
import com.eygraber.jellyfin.screens.collection.items.CollectionContentItem

@Composable
internal fun CollectionItemsGrid(
  items: List<CollectionContentItem>,
  isLoadingMore: Boolean,
  onItemClick: (itemId: String) -> Unit,
  onLoadMore: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val gridState = rememberLazyGridState()
  val currentOnLoadMore by rememberUpdatedState(onLoadMore)

  val shouldLoadMore by remember {
    derivedStateOf {
      val lastVisibleItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
      lastVisibleItem >= items.size - LOAD_MORE_THRESHOLD
    }
  }

  LaunchedEffect(shouldLoadMore) {
    if(shouldLoadMore) {
      currentOnLoadMore()
    }
  }

  LazyVerticalGrid(
    state = gridState,
    columns = GridCells.Adaptive(minSize = 140.dp),
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    modifier = modifier.fillMaxSize(),
  ) {
    items(
      items = items,
      key = { it.id },
    ) { item ->
      ContentItemCard(
        item = item,
        onClick = { onItemClick(item.id) },
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
private fun ContentItemCard(
  item: CollectionContentItem,
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
          .aspectRatio(POSTER_ASPECT_RATIO),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = item.name.take(1),
          style = MaterialTheme.typography.headlineMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      Column(
        modifier = Modifier.padding(8.dp),
      ) {
        Text(
          text = item.name,
          style = MaterialTheme.typography.bodySmall,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )

        item.productionYear?.let { year ->
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

private const val LOAD_MORE_THRESHOLD = 6
private const val POSTER_ASPECT_RATIO = 2F / 3F
