package com.eygraber.jellyfin.screens.library.genres.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eygraber.jellyfin.screens.library.genres.GenreItem

@Composable
internal fun GenresList(
  genres: List<GenreItem>,
  onGenreClick: (genreName: String) -> Unit,
  modifier: Modifier = Modifier,
) {
  LazyColumn(
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    modifier = modifier.fillMaxSize(),
  ) {
    items(
      items = genres,
      key = { it.id },
    ) { genre ->
      GenreCard(
        genre = genre,
        onClick = { onGenreClick(genre.name) },
      )
    }
  }
}

@Composable
private fun GenreCard(
  genre: GenreItem,
  onClick: () -> Unit,
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
      .clickable(onClick = onClick),
  ) {
    Text(
      text = genre.name,
      style = MaterialTheme.typography.titleMedium,
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
    )
  }
}
