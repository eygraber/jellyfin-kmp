package com.eygraber.jellyfin.screens.library.collections.components

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
import com.eygraber.jellyfin.screens.library.collections.CollectionItem

@Composable
internal fun CollectionPosterGrid(
  collections: List<CollectionItem>,
  isLoadingMore: Boolean,
  onCollectionClick: (collectionId: String) -> Unit,
  onLoadMore: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val gridState = rememberLazyGridState()
  val currentOnLoadMore by rememberUpdatedState(onLoadMore)

  val shouldLoadMore by remember {
    derivedStateOf {
      val lastVisibleItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
      lastVisibleItem >= collections.size - LOAD_MORE_THRESHOLD
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
      items = collections,
      key = { it.id },
    ) { collection ->
      CollectionCard(
        collection = collection,
        onClick = { onCollectionClick(collection.id) },
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
private fun CollectionCard(
  collection: CollectionItem,
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
          text = collection.name.take(1),
          style = MaterialTheme.typography.headlineMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      Column(
        modifier = Modifier.padding(8.dp),
      ) {
        Text(
          text = collection.name,
          style = MaterialTheme.typography.bodySmall,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )

        collection.itemCount?.let { count ->
          Text(
            text = "$count items",
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
