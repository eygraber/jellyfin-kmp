package com.eygraber.jellyfin.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.eygraber.jellyfin.ui.compose.PreviewJellyfinScreen
import com.eygraber.jellyfin.ui.icons.ArrowBack
import com.eygraber.jellyfin.ui.icons.Close
import com.eygraber.jellyfin.ui.icons.JellyfinIcons
import com.eygraber.jellyfin.ui.material.theme.JellyfinPreviewTheme
import com.eygraber.jellyfin.ui.material.theme.JellyfinTheme
import com.eygraber.vice.ViceView

internal typealias SearchView = ViceView<SearchIntent, SearchViewState>

private const val POSTER_ASPECT_RATIO = 2F / 3F

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchView(
  state: SearchViewState,
  onIntent: (SearchIntent) -> Unit,
) {
  JellyfinTheme {
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text("Search") },
          navigationIcon = {
            IconButton(onClick = { onIntent(SearchIntent.NavigateBack) }) {
              Icon(
                imageVector = JellyfinIcons.ArrowBack,
                contentDescription = "Navigate back",
              )
            }
          },
        )
      },
    ) { contentPadding ->
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(contentPadding),
      ) {
        SearchField(
          query = state.query,
          onQueryChange = { onIntent(SearchIntent.QueryChanged(it)) },
          onClear = { onIntent(SearchIntent.ClearQuery) },
        )

        when {
          state.isLoading -> LoadingContent()

          state.error != null -> ErrorContent(error = state.error)

          state.query.isBlank() -> EmptyQueryContent()

          state.isEmptyResults -> NoResultsContent(query = state.query)

          state.hasResults -> SearchResultsContent(
            state = state,
            onResultClick = { id, type -> onIntent(SearchIntent.ResultClicked(itemId = id, itemType = type)) },
          )
        }
      }
    }
  }
}

@Composable
private fun SearchField(
  query: String,
  onQueryChange: (String) -> Unit,
  onClear: () -> Unit,
) {
  OutlinedTextField(
    value = query,
    onValueChange = onQueryChange,
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    placeholder = { Text("Search movies, shows, music...") },
    singleLine = true,
    trailingIcon = {
      if(query.isNotEmpty()) {
        IconButton(onClick = onClear) {
          Icon(
            imageVector = JellyfinIcons.Close,
            contentDescription = "Clear search",
          )
        }
      }
    },
  )
}

@Composable
private fun SearchResultsContent(
  state: SearchViewState,
  onResultClick: (itemId: String, itemType: String) -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState()),
  ) {
    ResultSection(
      title = "Movies",
      items = state.movieResults,
      onResultClick = onResultClick,
    )

    ResultSection(
      title = "TV Shows",
      items = state.seriesResults,
      onResultClick = onResultClick,
    )

    ResultSection(
      title = "Episodes",
      items = state.episodeResults,
      onResultClick = onResultClick,
    )

    ResultSection(
      title = "Music",
      items = state.musicResults,
      onResultClick = onResultClick,
    )

    ResultSection(
      title = "People",
      items = state.peopleResults,
      onResultClick = onResultClick,
    )

    Spacer(modifier = Modifier.height(16.dp))
  }
}

@Composable
private fun ResultSection(
  title: String,
  items: List<SearchViewItem>,
  onResultClick: (itemId: String, itemType: String) -> Unit,
) {
  if(items.isEmpty()) return

  Column {
    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = title,
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.SemiBold,
      modifier = Modifier.padding(horizontal = 16.dp),
    )

    Spacer(modifier = Modifier.height(8.dp))

    LazyRow(
      contentPadding = PaddingValues(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      items(
        items = items,
        key = { it.id },
      ) { item ->
        SearchResultCard(
          item = item,
          onClick = { onResultClick(item.id, item.type) },
        )
      }
    }
  }
}

@Composable
private fun SearchResultCard(
  item: SearchViewItem,
  onClick: () -> Unit,
) {
  Card(
    modifier = Modifier
      .width(120.dp)
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
          style = MaterialTheme.typography.headlineSmall,
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

        item.subtitle?.let { subtitle ->
          Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        }

        item.year?.let { year ->
          Text(
            text = year,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
private fun ErrorContent(error: SearchError) {
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = error.message,
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.error,
    )
  }
}

@Composable
private fun EmptyQueryContent() {
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = "Search for movies, TV shows, and more",
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@Composable
private fun NoResultsContent(query: String) {
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = "No results for \"$query\"",
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@PreviewJellyfinScreen
@Composable
private fun SearchEmptyPreview() {
  JellyfinPreviewTheme {
    SearchView(
      state = SearchViewState.Initial,
      onIntent = {},
    )
  }
}

@Suppress("LongMethod")
@PreviewJellyfinScreen
@Composable
private fun SearchResultsPreview() {
  JellyfinPreviewTheme {
    SearchView(
      state = SearchViewState(
        query = "inception",
        movieResults = listOf(
          SearchViewItem(
            id = "m1",
            name = "Inception",
            type = "Movie",
            year = "2010",
            imageUrl = null,
            subtitle = null,
          ),
          SearchViewItem(
            id = "m2",
            name = "Interstellar",
            type = "Movie",
            year = "2014",
            imageUrl = null,
            subtitle = null,
          ),
        ),
        seriesResults = listOf(
          SearchViewItem(
            id = "s1",
            name = "Breaking Bad",
            type = "Series",
            year = "2008",
            imageUrl = null,
            subtitle = null,
          ),
        ),
      ),
      onIntent = {},
    )
  }
}
