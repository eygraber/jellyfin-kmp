package com.eygraber.jellyfin.screens.collection.items

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
import com.eygraber.jellyfin.screens.collection.items.components.CollectionItemsGrid
import com.eygraber.jellyfin.ui.compose.PreviewJellyfinScreen
import com.eygraber.jellyfin.ui.icons.ArrowBack
import com.eygraber.jellyfin.ui.icons.JellyfinIcons
import com.eygraber.jellyfin.ui.material.theme.JellyfinPreviewTheme
import com.eygraber.jellyfin.ui.material.theme.JellyfinTheme
import com.eygraber.vice.ViceView

internal typealias CollectionItemsView = ViceView<CollectionItemsIntent, CollectionItemsViewState>

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CollectionItemsView(
  state: CollectionItemsViewState,
  onIntent: (CollectionItemsIntent) -> Unit,
) {
  JellyfinTheme {
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text(state.collectionName.ifEmpty { "Collection" }) },
          navigationIcon = {
            IconButton(onClick = { onIntent(CollectionItemsIntent.NavigateBack) }) {
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
            onRetry = { onIntent(CollectionItemsIntent.RetryLoad) },
          )
          state.isEmpty -> EmptyContent()
          else -> CollectionItemsGrid(
            items = state.items,
            isLoadingMore = state.isLoadingMore,
            onItemClick = { itemId -> onIntent(CollectionItemsIntent.SelectItem(itemId)) },
            onLoadMore = { onIntent(CollectionItemsIntent.LoadMore) },
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
  error: CollectionItemsError,
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
      text = "No items in this collection",
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@PreviewJellyfinScreen
@Composable
private fun CollectionItemsContentPreview() {
  JellyfinPreviewTheme {
    CollectionItemsView(
      state = CollectionItemsViewState(
        collectionName = "Marvel Cinematic Universe",
        isLoading = false,
        items = listOf(
          CollectionContentItem(
            id = "1",
            name = "Iron Man",
            type = "Movie",
            productionYear = 2008,
            imageUrl = null,
          ),
          CollectionContentItem(
            id = "2",
            name = "The Avengers",
            type = "Movie",
            productionYear = 2012,
            imageUrl = null,
          ),
        ),
      ),
      onIntent = {},
    )
  }
}
