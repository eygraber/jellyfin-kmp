package com.eygraber.jellyfin.screens.library.tvshows

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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eygraber.jellyfin.screens.library.tvshows.components.TvShowPosterGrid
import com.eygraber.jellyfin.ui.compose.PreviewJellyfinScreen
import com.eygraber.jellyfin.ui.icons.ArrowBack
import com.eygraber.jellyfin.ui.icons.JellyfinIcons
import com.eygraber.jellyfin.ui.material.theme.JellyfinPreviewTheme
import com.eygraber.jellyfin.ui.material.theme.JellyfinTheme
import com.eygraber.vice.ViceView

internal typealias TvShowsLibraryView = ViceView<TvShowsLibraryIntent, TvShowsLibraryViewState>

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TvShowsLibraryView(
  state: TvShowsLibraryViewState,
  onIntent: (TvShowsLibraryIntent) -> Unit,
) {
  JellyfinTheme {
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text("TV Shows") },
          navigationIcon = {
            IconButton(onClick = { onIntent(TvShowsLibraryIntent.NavigateBack) }) {
              Icon(
                imageVector = JellyfinIcons.ArrowBack,
                contentDescription = "Navigate back",
              )
            }
          },
        )
      },
    ) { contentPadding ->
      PullToRefreshBox(
        isRefreshing = false,
        onRefresh = { onIntent(TvShowsLibraryIntent.Refresh) },
        modifier = Modifier
          .fillMaxSize()
          .padding(contentPadding),
      ) {
        when {
          state.isLoading -> LoadingContent()
          state.error != null -> ErrorContent(
            error = state.error,
            onRetry = { onIntent(TvShowsLibraryIntent.RetryLoad) },
          )
          state.isEmpty -> EmptyContent()
          else -> TvShowPosterGrid(
            items = state.items,
            isLoadingMore = state.isLoadingMore,
            hasMore = state.hasMore,
            onShowClick = { showId -> onIntent(TvShowsLibraryIntent.SelectShow(showId)) },
            onLoadMore = { onIntent(TvShowsLibraryIntent.LoadMore) },
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
  error: TvShowsLibraryError,
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
      text = "No TV shows found",
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@PreviewJellyfinScreen
@Composable
private fun TvShowsLibraryLoadingPreview() {
  JellyfinPreviewTheme {
    TvShowsLibraryView(
      state = TvShowsLibraryViewState.Loading,
      onIntent = {},
    )
  }
}

@PreviewJellyfinScreen
@Composable
private fun TvShowsLibraryErrorPreview() {
  JellyfinPreviewTheme {
    TvShowsLibraryView(
      state = TvShowsLibraryViewState(
        isLoading = false,
        error = TvShowsLibraryError.Network(),
      ),
      onIntent = {},
    )
  }
}

@PreviewJellyfinScreen
@Composable
private fun TvShowsLibraryEmptyPreview() {
  JellyfinPreviewTheme {
    TvShowsLibraryView(
      state = TvShowsLibraryViewState(
        isLoading = false,
        isEmpty = true,
      ),
      onIntent = {},
    )
  }
}

@PreviewJellyfinScreen
@Composable
private fun TvShowsLibraryContentPreview() {
  JellyfinPreviewTheme {
    TvShowsLibraryView(
      state = TvShowsLibraryViewState(
        isLoading = false,
        items = listOf(
          TvShowItem(
            id = "1",
            name = "Breaking Bad",
            productionYear = 2008,
            communityRating = 9.5F,
            officialRating = "TV-MA",
            seasonCount = 5,
            imageUrl = null,
          ),
          TvShowItem(
            id = "2",
            name = "Game of Thrones",
            productionYear = 2011,
            communityRating = 9.3F,
            officialRating = "TV-MA",
            seasonCount = 8,
            imageUrl = null,
          ),
        ),
        hasMore = true,
      ),
      onIntent = {},
    )
  }
}
