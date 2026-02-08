package com.eygraber.jellyfin.screens.tvshow.seasons

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
import com.eygraber.jellyfin.screens.tvshow.seasons.components.SeasonsList
import com.eygraber.jellyfin.ui.compose.PreviewJellyfinScreen
import com.eygraber.jellyfin.ui.icons.ArrowBack
import com.eygraber.jellyfin.ui.icons.JellyfinIcons
import com.eygraber.jellyfin.ui.material.theme.JellyfinPreviewTheme
import com.eygraber.jellyfin.ui.material.theme.JellyfinTheme
import com.eygraber.vice.ViceView

internal typealias TvShowSeasonsView = ViceView<TvShowSeasonsIntent, TvShowSeasonsViewState>

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TvShowSeasonsView(
  state: TvShowSeasonsViewState,
  onIntent: (TvShowSeasonsIntent) -> Unit,
) {
  JellyfinTheme {
    Scaffold(
      topBar = {
        TopAppBar(
          title = {
            Text(
              text = state.showName.ifEmpty { "Seasons" },
            )
          },
          navigationIcon = {
            IconButton(onClick = { onIntent(TvShowSeasonsIntent.NavigateBack) }) {
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
            onRetry = { onIntent(TvShowSeasonsIntent.RetryLoad) },
          )
          state.isEmpty -> EmptyContent()
          else -> SeasonsList(
            seasons = state.seasons,
            onSeasonClick = { seasonId -> onIntent(TvShowSeasonsIntent.SelectSeason(seasonId)) },
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
  error: TvShowSeasonsError,
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
      text = "No seasons found",
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@PreviewJellyfinScreen
@Composable
private fun TvShowSeasonsLoadingPreview() {
  JellyfinPreviewTheme {
    TvShowSeasonsView(
      state = TvShowSeasonsViewState.Loading,
      onIntent = {},
    )
  }
}

@PreviewJellyfinScreen
@Composable
private fun TvShowSeasonsErrorPreview() {
  JellyfinPreviewTheme {
    TvShowSeasonsView(
      state = TvShowSeasonsViewState(
        isLoading = false,
        error = TvShowSeasonsError.Network(),
      ),
      onIntent = {},
    )
  }
}

@PreviewJellyfinScreen
@Composable
private fun TvShowSeasonsContentPreview() {
  JellyfinPreviewTheme {
    TvShowSeasonsView(
      state = TvShowSeasonsViewState(
        showName = "Breaking Bad",
        isLoading = false,
        seasons = listOf(
          SeasonItem(
            id = "1",
            name = "Season 1",
            seasonNumber = 1,
            episodeCount = 7,
            imageUrl = null,
          ),
          SeasonItem(
            id = "2",
            name = "Season 2",
            seasonNumber = 2,
            episodeCount = 13,
            imageUrl = null,
          ),
          SeasonItem(
            id = "3",
            name = "Season 3",
            seasonNumber = 3,
            episodeCount = 13,
            imageUrl = null,
          ),
        ),
      ),
      onIntent = {},
    )
  }
}
