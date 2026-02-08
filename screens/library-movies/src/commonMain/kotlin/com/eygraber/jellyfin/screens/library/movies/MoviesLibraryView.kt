package com.eygraber.jellyfin.screens.library.movies

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.eygraber.jellyfin.screens.library.movies.components.MoviePosterGrid
import com.eygraber.jellyfin.ui.compose.PreviewJellyfinScreen
import com.eygraber.jellyfin.ui.icons.ArrowBack
import com.eygraber.jellyfin.ui.icons.JellyfinIcons
import com.eygraber.jellyfin.ui.library.controls.ActiveFilterChips
import com.eygraber.jellyfin.ui.library.controls.FilterButton
import com.eygraber.jellyfin.ui.library.controls.FilterSheet
import com.eygraber.jellyfin.ui.library.controls.SortMenu
import com.eygraber.jellyfin.ui.library.controls.ViewToggle
import com.eygraber.jellyfin.ui.library.controls.movieSortOptions
import com.eygraber.jellyfin.ui.material.theme.JellyfinPreviewTheme
import com.eygraber.jellyfin.ui.material.theme.JellyfinTheme
import com.eygraber.vice.ViceView

internal typealias MoviesLibraryView = ViceView<MoviesLibraryIntent, MoviesLibraryViewState>

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MoviesLibraryView(
  state: MoviesLibraryViewState,
  onIntent: (MoviesLibraryIntent) -> Unit,
) {
  JellyfinTheme {
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text("Movies") },
          navigationIcon = {
            IconButton(onClick = { onIntent(MoviesLibraryIntent.NavigateBack) }) {
              Icon(
                imageVector = JellyfinIcons.ArrowBack,
                contentDescription = "Navigate back",
              )
            }
          },
          actions = {
            Row {
              SortMenu(
                sortConfig = state.sortConfig,
                sortOptions = movieSortOptions,
                onSortChange = { sortBy, sortOrder ->
                  onIntent(MoviesLibraryIntent.ChangeSortOption(sortBy = sortBy, sortOrder = sortOrder))
                },
              )

              FilterButton(
                activeFilterCount = state.filters.activeFilterCount,
                onClick = { onIntent(MoviesLibraryIntent.ToggleFilterSheet) },
              )

              ViewToggle(
                viewMode = state.viewMode,
                onViewModeChange = { onIntent(MoviesLibraryIntent.ChangeViewMode(viewMode = it)) },
              )
            }
          },
        )
      },
    ) { contentPadding ->
      PullToRefreshBox(
        isRefreshing = false,
        onRefresh = { onIntent(MoviesLibraryIntent.Refresh) },
        modifier = Modifier
          .fillMaxSize()
          .padding(contentPadding),
      ) {
        Column {
          ActiveFilterChips(
            filters = state.filters,
            onRemoveGenre = { genre ->
              onIntent(
                MoviesLibraryIntent.ChangeFilters(
                  filters = state.filters.copy(genres = state.filters.genres - genre),
                ),
              )
            },
            onRemoveYear = { year ->
              onIntent(
                MoviesLibraryIntent.ChangeFilters(
                  filters = state.filters.copy(years = state.filters.years - year),
                ),
              )
            },
          )

          when {
            state.isLoading -> LoadingContent()
            state.error != null -> ErrorContent(
              error = state.error,
              onRetry = { onIntent(MoviesLibraryIntent.RetryLoad) },
            )
            state.isEmpty -> EmptyContent()
            else -> MoviePosterGrid(
              items = state.items,
              isLoadingMore = state.isLoadingMore,
              hasMore = state.hasMore,
              onMovieClick = { movieId -> onIntent(MoviesLibraryIntent.SelectMovie(movieId)) },
              onLoadMore = { onIntent(MoviesLibraryIntent.LoadMore) },
            )
          }
        }
      }

      if(state.isFilterSheetVisible) {
        FilterSheet(
          filters = state.filters,
          availableGenres = state.availableGenres,
          availableYears = state.availableYears,
          onFilterChange = { onIntent(MoviesLibraryIntent.ChangeFilters(filters = it)) },
          onDismiss = { onIntent(MoviesLibraryIntent.ToggleFilterSheet) },
        )
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
  error: MoviesLibraryError,
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
      text = "No movies found",
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@PreviewJellyfinScreen
@Composable
private fun MoviesLibraryLoadingPreview() {
  JellyfinPreviewTheme {
    MoviesLibraryView(
      state = MoviesLibraryViewState.Loading,
      onIntent = {},
    )
  }
}

@PreviewJellyfinScreen
@Composable
private fun MoviesLibraryErrorPreview() {
  JellyfinPreviewTheme {
    MoviesLibraryView(
      state = MoviesLibraryViewState(
        isLoading = false,
        error = MoviesLibraryError.Network(),
      ),
      onIntent = {},
    )
  }
}

@PreviewJellyfinScreen
@Composable
private fun MoviesLibraryEmptyPreview() {
  JellyfinPreviewTheme {
    MoviesLibraryView(
      state = MoviesLibraryViewState(
        isLoading = false,
        isEmpty = true,
      ),
      onIntent = {},
    )
  }
}

@PreviewJellyfinScreen
@Composable
private fun MoviesLibraryContentPreview() {
  JellyfinPreviewTheme {
    MoviesLibraryView(
      state = MoviesLibraryViewState(
        isLoading = false,
        items = listOf(
          MovieItem(
            id = "1",
            name = "Inception",
            productionYear = 2010,
            communityRating = 8.8F,
            officialRating = "PG-13",
            imageUrl = null,
          ),
          MovieItem(
            id = "2",
            name = "The Dark Knight",
            productionYear = 2008,
            communityRating = 9.0F,
            officialRating = "PG-13",
            imageUrl = null,
          ),
        ),
        hasMore = true,
      ),
      onIntent = {},
    )
  }
}
