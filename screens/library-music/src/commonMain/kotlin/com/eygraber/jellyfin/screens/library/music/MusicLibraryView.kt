package com.eygraber.jellyfin.screens.library.music

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
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eygraber.jellyfin.screens.library.music.components.AlbumsGrid
import com.eygraber.jellyfin.screens.library.music.components.ArtistsList
import com.eygraber.jellyfin.ui.compose.PreviewJellyfinScreen
import com.eygraber.jellyfin.ui.icons.ArrowBack
import com.eygraber.jellyfin.ui.icons.JellyfinIcons
import com.eygraber.jellyfin.ui.library.controls.SortMenu
import com.eygraber.jellyfin.ui.library.controls.musicSortOptions
import com.eygraber.jellyfin.ui.material.theme.JellyfinPreviewTheme
import com.eygraber.jellyfin.ui.material.theme.JellyfinTheme
import com.eygraber.vice.ViceView

internal typealias MusicLibraryView = ViceView<MusicLibraryIntent, MusicLibraryViewState>

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MusicLibraryView(
  state: MusicLibraryViewState,
  onIntent: (MusicLibraryIntent) -> Unit,
) {
  JellyfinTheme {
    Scaffold(
      topBar = {
        Column {
          TopAppBar(
            title = { Text("Music") },
            navigationIcon = {
              IconButton(onClick = { onIntent(MusicLibraryIntent.NavigateBack) }) {
                Icon(
                  imageVector = JellyfinIcons.ArrowBack,
                  contentDescription = "Navigate back",
                )
              }
            },
            actions = {
              SortMenu(
                sortConfig = state.sortConfig,
                sortOptions = musicSortOptions,
                onSortChange = { sortBy, sortOrder ->
                  onIntent(MusicLibraryIntent.ChangeSortOption(sortBy = sortBy, sortOrder = sortOrder))
                },
              )
            },
          )

          PrimaryTabRow(
            selectedTabIndex = MusicTab.entries.indexOf(state.selectedTab),
          ) {
            MusicTab.entries.forEach { tab ->
              Tab(
                selected = state.selectedTab == tab,
                onClick = { onIntent(MusicLibraryIntent.SelectTab(tab)) },
                text = { Text(tab.name) },
              )
            }
          }
        }
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
            onRetry = { onIntent(MusicLibraryIntent.RetryLoad) },
          )
          state.isEmpty -> EmptyContent(tab = state.selectedTab)
          else -> when(state.selectedTab) {
            MusicTab.Artists -> ArtistsList(
              artists = state.artists,
              isLoadingMore = state.isLoadingMore,
              hasMore = state.hasMore,
              onArtistClick = { artistId ->
                onIntent(MusicLibraryIntent.SelectArtist(artistId))
              },
              onLoadMore = { onIntent(MusicLibraryIntent.LoadMore) },
            )
            MusicTab.Albums -> AlbumsGrid(
              albums = state.albums,
              isLoadingMore = state.isLoadingMore,
              hasMore = state.hasMore,
              onAlbumClick = { albumId ->
                onIntent(MusicLibraryIntent.SelectAlbum(albumId))
              },
              onLoadMore = { onIntent(MusicLibraryIntent.LoadMore) },
            )
          }
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
  error: MusicLibraryError,
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
private fun EmptyContent(tab: MusicTab) {
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Text(
      text = when(tab) {
        MusicTab.Artists -> "No artists found"
        MusicTab.Albums -> "No albums found"
      },
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@PreviewJellyfinScreen
@Composable
private fun MusicLibraryLoadingPreview() {
  JellyfinPreviewTheme {
    MusicLibraryView(
      state = MusicLibraryViewState.Loading,
      onIntent = {},
    )
  }
}

@PreviewJellyfinScreen
@Composable
private fun MusicLibraryArtistsPreview() {
  JellyfinPreviewTheme {
    MusicLibraryView(
      state = MusicLibraryViewState(
        isLoading = false,
        selectedTab = MusicTab.Artists,
        artists = listOf(
          ArtistItem(id = "1", name = "Pink Floyd", albumCount = 15, imageUrl = null),
          ArtistItem(id = "2", name = "Led Zeppelin", albumCount = 9, imageUrl = null),
        ),
      ),
      onIntent = {},
    )
  }
}

@PreviewJellyfinScreen
@Composable
private fun MusicLibraryAlbumsPreview() {
  JellyfinPreviewTheme {
    MusicLibraryView(
      state = MusicLibraryViewState(
        isLoading = false,
        selectedTab = MusicTab.Albums,
        albums = listOf(
          AlbumItem(
            id = "1",
            name = "The Dark Side of the Moon",
            artistName = "Pink Floyd",
            productionYear = 1973,
            imageUrl = null,
          ),
          AlbumItem(
            id = "2",
            name = "Led Zeppelin IV",
            artistName = "Led Zeppelin",
            productionYear = 1971,
            imageUrl = null,
          ),
        ),
      ),
      onIntent = {},
    )
  }
}
