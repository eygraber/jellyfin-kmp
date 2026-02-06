package com.eygraber.jellyfin.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.eygraber.jellyfin.screens.home.components.ContinueWatchingLoading
import com.eygraber.jellyfin.screens.home.components.ContinueWatchingRow
import com.eygraber.jellyfin.screens.home.components.NextUpRow
import com.eygraber.jellyfin.screens.home.components.RecentlyAddedSection
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
          else -> HomeContent(
            state = state,
            onIntent = onIntent,
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

@Composable
private fun HomeContent(
  state: HomeViewState,
  onIntent: (HomeIntent) -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState()),
  ) {
    Spacer(modifier = Modifier.height(8.dp))

    ContinueWatchingSection(
      state = state.continueWatchingState,
      onItemClick = { itemId -> onIntent(HomeIntent.ContinueWatchingItemClicked(itemId)) },
    )

    NextUpSection(
      state = state.nextUpState,
      onItemClick = { itemId -> onIntent(HomeIntent.NextUpItemClicked(itemId)) },
    )

    RecentlyAddedHomeSection(
      state = state.recentlyAddedState,
      onItemClick = { itemId -> onIntent(HomeIntent.RecentlyAddedItemClicked(itemId)) },
    )

    Spacer(modifier = Modifier.height(16.dp))
  }
}

@Composable
private fun ContinueWatchingSection(
  state: ContinueWatchingState,
  onItemClick: (itemId: String) -> Unit,
) {
  when(state) {
    is ContinueWatchingState.Loading -> ContinueWatchingLoading()

    is ContinueWatchingState.Loaded -> ContinueWatchingRow(
      items = state.items,
      onItemClick = onItemClick,
    )

    is ContinueWatchingState.Empty,
    is ContinueWatchingState.Error,
    -> Unit
  }
}

@Composable
private fun NextUpSection(
  state: NextUpState,
  onItemClick: (itemId: String) -> Unit,
) {
  when(state) {
    is NextUpState.Loading -> Unit

    is NextUpState.Loaded -> {
      Spacer(modifier = Modifier.height(16.dp))
      NextUpRow(
        items = state.items,
        onItemClick = onItemClick,
      )
    }

    is NextUpState.Empty,
    is NextUpState.Error,
    -> Unit
  }
}

@Composable
private fun RecentlyAddedHomeSection(
  state: RecentlyAddedState,
  onItemClick: (itemId: String) -> Unit,
) {
  when(state) {
    is RecentlyAddedState.Loading -> Unit

    is RecentlyAddedState.Loaded -> {
      Spacer(modifier = Modifier.height(16.dp))
      RecentlyAddedSection(
        items = state.items,
        onItemClick = onItemClick,
      )
    }

    is RecentlyAddedState.Empty,
    is RecentlyAddedState.Error,
    -> Unit
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

@PreviewJellyfinScreen
@Composable
private fun HomeContinueWatchingPreview() {
  JellyfinPreviewTheme {
    HomeView(
      state = HomeViewState(
        userName = "TestUser",
        isLoading = false,
        continueWatchingState = ContinueWatchingState.Loaded(
          items = listOf(
            ContinueWatchingItem(
              id = "1",
              name = "The Dark Knight",
              type = "Movie",
              seriesName = null,
              seasonName = null,
              indexNumber = null,
              parentIndexNumber = null,
              progressPercent = 0.45F,
              imageUrl = "",
              backdropImageUrl = null,
            ),
            ContinueWatchingItem(
              id = "2",
              name = "Ozymandias",
              type = "Episode",
              seriesName = "Breaking Bad",
              seasonName = "Season 5",
              indexNumber = 14,
              parentIndexNumber = 5,
              progressPercent = 0.72F,
              imageUrl = "",
              backdropImageUrl = null,
            ),
            ContinueWatchingItem(
              id = "3",
              name = "Battle of the Bastards",
              type = "Episode",
              seriesName = "Game of Thrones",
              seasonName = "Season 6",
              indexNumber = 9,
              parentIndexNumber = 6,
              progressPercent = 0.15F,
              imageUrl = "",
              backdropImageUrl = null,
            ),
          ),
        ),
      ),
      onIntent = {},
    )
  }
}

@PreviewJellyfinScreen
@Composable
private fun HomeContinueWatchingEmptyPreview() {
  JellyfinPreviewTheme {
    HomeView(
      state = HomeViewState(
        userName = "TestUser",
        isLoading = false,
        continueWatchingState = ContinueWatchingState.Empty,
      ),
      onIntent = {},
    )
  }
}
