package com.eygraber.jellyfin.screens.genre.items

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
import com.eygraber.jellyfin.screens.genre.items.components.GenreItemsGrid
import com.eygraber.jellyfin.ui.compose.PreviewJellyfinScreen
import com.eygraber.jellyfin.ui.icons.ArrowBack
import com.eygraber.jellyfin.ui.icons.JellyfinIcons
import com.eygraber.jellyfin.ui.material.theme.JellyfinPreviewTheme
import com.eygraber.jellyfin.ui.material.theme.JellyfinTheme
import com.eygraber.vice.ViceView

internal typealias GenreItemsView = ViceView<GenreItemsIntent, GenreItemsViewState>

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GenreItemsView(
  state: GenreItemsViewState,
  onIntent: (GenreItemsIntent) -> Unit,
) {
  JellyfinTheme {
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text(state.genreName.ifEmpty { "Genre" }) },
          navigationIcon = {
            IconButton(onClick = { onIntent(GenreItemsIntent.NavigateBack) }) {
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
            onRetry = { onIntent(GenreItemsIntent.RetryLoad) },
          )
          state.isEmpty -> EmptyContent()
          else -> GenreItemsGrid(
            items = state.items,
            isLoadingMore = state.isLoadingMore,
            onItemClick = { itemId -> onIntent(GenreItemsIntent.SelectItem(itemId)) },
            onLoadMore = { onIntent(GenreItemsIntent.LoadMore) },
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
  error: GenreItemsError,
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
      text = "No items found",
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@PreviewJellyfinScreen
@Composable
private fun GenreItemsContentPreview() {
  JellyfinPreviewTheme {
    GenreItemsView(
      state = GenreItemsViewState(
        genreName = "Science Fiction",
        isLoading = false,
        items = listOf(
          GenreContentItem(
            id = "1",
            name = "Blade Runner 2049",
            productionYear = 2017,
            imageUrl = null,
          ),
          GenreContentItem(
            id = "2",
            name = "Interstellar",
            productionYear = 2014,
            imageUrl = null,
          ),
        ),
      ),
      onIntent = {},
    )
  }
}
