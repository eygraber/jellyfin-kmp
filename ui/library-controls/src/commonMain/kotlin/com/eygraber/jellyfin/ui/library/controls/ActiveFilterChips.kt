package com.eygraber.jellyfin.ui.library.controls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eygraber.jellyfin.ui.icons.Close
import com.eygraber.jellyfin.ui.icons.JellyfinIcons

/**
 * Horizontal scrollable row showing active filter chips with dismiss action.
 */
@Composable
fun ActiveFilterChips(
  filters: LibraryFilters,
  onRemoveGenre: (String) -> Unit,
  onRemoveYear: (Int) -> Unit,
  modifier: Modifier = Modifier,
) {
  if(!filters.hasActiveFilters) return

  LazyRow(
    modifier = modifier,
    contentPadding = PaddingValues(horizontal = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    items(
      items = filters.genres,
      key = { "genre-$it" },
    ) { genre ->
      FilterChip(
        selected = true,
        onClick = { onRemoveGenre(genre) },
        label = { Text(genre) },
        trailingIcon = {
          Icon(
            imageVector = JellyfinIcons.Close,
            contentDescription = "Remove $genre filter",
          )
        },
      )
    }

    items(
      items = filters.years,
      key = { "year-$it" },
    ) { year ->
      FilterChip(
        selected = true,
        onClick = { onRemoveYear(year) },
        label = { Text(year.toString()) },
        trailingIcon = {
          Icon(
            imageVector = JellyfinIcons.Close,
            contentDescription = "Remove $year filter",
          )
        },
      )
    }
  }
}
