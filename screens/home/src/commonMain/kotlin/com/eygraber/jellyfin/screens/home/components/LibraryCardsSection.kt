package com.eygraber.jellyfin.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.eygraber.jellyfin.screens.home.LibraryView

@Composable
internal fun LibraryCardsSection(
  libraries: List<LibraryView>,
  onLibraryClick: (libraryId: String) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier) {
    Text(
      text = "My Libraries",
      style = MaterialTheme.typography.titleMedium,
      modifier = Modifier.padding(horizontal = 16.dp),
    )

    Spacer(modifier = Modifier.height(8.dp))

    LazyRow(
      contentPadding = PaddingValues(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      items(
        items = libraries,
        key = { it.id },
      ) { library ->
        LibraryCard(
          library = library,
          onClick = { onLibraryClick(library.id) },
        )
      }
    }
  }
}

@Composable
private fun LibraryCard(
  library: LibraryView,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Card(
    onClick = onClick,
    modifier = modifier.width(libraryCardWidth),
  ) {
    Column {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(libraryCardImageHeight)
          .clip(MaterialTheme.shapes.medium)
          .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = library.name.take(1).uppercase(),
          style = MaterialTheme.typography.headlineLarge,
          color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
      }

      Text(
        text = library.name,
        style = MaterialTheme.typography.bodySmall,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center,
        modifier = Modifier
          .fillMaxWidth()
          .padding(8.dp),
      )
    }
  }
}

private val libraryCardWidth = 120.dp
private val libraryCardImageHeight = 80.dp
