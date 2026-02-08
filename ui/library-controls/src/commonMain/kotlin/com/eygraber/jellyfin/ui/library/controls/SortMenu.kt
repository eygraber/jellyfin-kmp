package com.eygraber.jellyfin.ui.library.controls

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eygraber.jellyfin.data.items.ItemSortBy
import com.eygraber.jellyfin.data.items.SortOrder
import com.eygraber.jellyfin.ui.icons.Check
import com.eygraber.jellyfin.ui.icons.JellyfinIcons
import com.eygraber.jellyfin.ui.icons.Sort

/**
 * Dropdown menu for selecting sort field and order.
 */
@Composable
fun SortMenu(
  sortConfig: LibrarySortConfig,
  sortOptions: List<LibrarySortOption>,
  onSortChange: (sortBy: ItemSortBy, sortOrder: SortOrder) -> Unit,
  modifier: Modifier = Modifier,
) {
  var isExpanded by remember { mutableStateOf(false) }

  IconButton(
    onClick = { isExpanded = true },
    modifier = modifier,
  ) {
    Icon(
      imageVector = JellyfinIcons.Sort,
      contentDescription = "Sort",
    )
  }

  DropdownMenu(
    expanded = isExpanded,
    onDismissRequest = { isExpanded = false },
  ) {
    sortOptions.forEach { option ->
      val isSelected = sortConfig.sortBy == option.sortBy

      DropdownMenuItem(
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = option.label)

            if(isSelected) {
              Spacer(modifier = Modifier.width(8.dp))

              Text(
                text = if(sortConfig.sortOrder == SortOrder.Ascending) "\u2191" else "\u2193",
                style = MaterialTheme.typography.bodyMedium,
              )
            }
          }
        },
        onClick = {
          val newOrder = if(isSelected) {
            sortConfig.sortOrder.toggle()
          }
          else {
            SortOrder.Ascending
          }
          onSortChange(option.sortBy, newOrder)
          isExpanded = false
        },
        leadingIcon = if(isSelected) {
          {
            Icon(
              imageVector = JellyfinIcons.Check,
              contentDescription = null,
            )
          }
        }
        else {
          null
        },
      )
    }
  }
}

private fun SortOrder.toggle(): SortOrder = when(this) {
  SortOrder.Ascending -> SortOrder.Descending
  SortOrder.Descending -> SortOrder.Ascending
}
