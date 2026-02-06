package com.eygraber.jellyfin.screens.home

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eygraber.jellyfin.ui.compose.PreviewJellyfinScreen
import com.eygraber.jellyfin.ui.material.theme.JellyfinPreviewTheme
import com.eygraber.jellyfin.ui.material.theme.JellyfinTheme
import com.eygraber.vice.ViceView

internal typealias HomeView = ViceView<HomeIntent, HomeViewState>

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeView(
  state: HomeViewState,
  onIntent: (HomeIntent) -> Unit,
) {
  JellyfinTheme {
    Scaffold(
      topBar = {
        TopAppBar(
          title = {
            if(state.userName.isNotEmpty()) {
              Text("Welcome, ${state.userName}")
            }
            else {
              Text("Home")
            }
          },
        )
      },
    ) { contentPadding ->
      PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = { onIntent(HomeIntent.Refresh) },
        modifier = Modifier
          .fillMaxSize()
          .padding(contentPadding),
      ) {
        when {
          state.isLoading -> LoadingContent()
          state.error != null -> ErrorContent(
            error = state.error,
            onRetry = { onIntent(HomeIntent.RetryLoad) },
          )
          else -> HomeContent(state = state)
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
  error: HomeError,
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

@Suppress("UNUSED_PARAMETER")
@Composable
private fun HomeContent(state: HomeViewState) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
  ) {
    Text(
      text = "Content coming soon",
      style = MaterialTheme.typography.bodyLarge,
    )
  }
}

@PreviewJellyfinScreen
@Composable
private fun HomeLoadingPreview() {
  JellyfinPreviewTheme {
    HomeView(
      state = HomeViewState.Loading,
      onIntent = {},
    )
  }
}

@PreviewJellyfinScreen
@Composable
private fun HomeErrorPreview() {
  JellyfinPreviewTheme {
    HomeView(
      state = HomeViewState(
        isLoading = false,
        error = HomeError.Network(),
      ),
      onIntent = {},
    )
  }
}

@PreviewJellyfinScreen
@Composable
private fun HomeContentPreview() {
  JellyfinPreviewTheme {
    HomeView(
      state = HomeViewState(
        userName = "TestUser",
        isLoading = false,
      ),
      onIntent = {},
    )
  }
}
