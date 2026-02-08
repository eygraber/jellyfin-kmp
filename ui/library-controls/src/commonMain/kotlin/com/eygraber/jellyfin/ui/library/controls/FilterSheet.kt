package com.eygraber.jellyfin.ui.library.controls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Bottom sheet for selecting genre and year filters.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterSheet(
  filters: LibraryFilters,
  availableGenres: List<String>,
  availableYears: List<Int>,
  onFilterChange: (LibraryFilters) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    modifier = modifier,
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .padding(bottom = 32.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = "Filters",
          style = MaterialTheme.typography.titleLarge,
        )

        if(filters.hasActiveFilters) {
          TextButton(onClick = { onFilterChange(LibraryFilters()) }) {
            Text("Clear All")
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      if(availableGenres.isNotEmpty()) {
        Text(
          text = "Genres",
          style = MaterialTheme.typography.titleMedium,
          modifier = Modifier.padding(bottom = 8.dp),
        )

        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          availableGenres.forEach { genre ->
            val isSelected = genre in filters.genres
            FilterChip(
              selected = isSelected,
              onClick = {
                val newGenres = if(isSelected) {
                  filters.genres - genre
                }
                else {
                  filters.genres + genre
                }
                onFilterChange(filters.copy(genres = newGenres))
              },
              label = { Text(genre) },
            )
          }
        }
      }

      if(availableYears.isNotEmpty()) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "Year",
          style = MaterialTheme.typography.titleMedium,
          modifier = Modifier.padding(bottom = 8.dp),
        )

        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          availableYears.forEach { year ->
            val isSelected = year in filters.years
            FilterChip(
              selected = isSelected,
              onClick = {
                val newYears = if(isSelected) {
                  filters.years - year
                }
                else {
                  filters.years + year
                }
                onFilterChange(filters.copy(years = newYears))
              },
              label = { Text(year.toString()) },
            )
          }
        }
      }
    }
  }
}
