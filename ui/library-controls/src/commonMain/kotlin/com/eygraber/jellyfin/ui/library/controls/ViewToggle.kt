package com.eygraber.jellyfin.ui.library.controls

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.eygraber.jellyfin.ui.icons.GridView
import com.eygraber.jellyfin.ui.icons.JellyfinIcons
import com.eygraber.jellyfin.ui.icons.ViewList

/**
 * View modes for library content display.
 */
enum class LibraryViewMode {
  Grid,
  List,
}

/**
 * Toggle button for switching between grid and list view modes.
 */
@Composable
fun ViewToggle(
  viewMode: LibraryViewMode,
  onViewModeChange: (LibraryViewMode) -> Unit,
  modifier: Modifier = Modifier,
) {
  IconButton(
    onClick = {
      val newMode = when(viewMode) {
        LibraryViewMode.Grid -> LibraryViewMode.List
        LibraryViewMode.List -> LibraryViewMode.Grid
      }
      onViewModeChange(newMode)
    },
    modifier = modifier,
  ) {
    Icon(
      imageVector = when(viewMode) {
        LibraryViewMode.Grid -> JellyfinIcons.ViewList
        LibraryViewMode.List -> JellyfinIcons.GridView
      },
      contentDescription = when(viewMode) {
        LibraryViewMode.Grid -> "Switch to list view"
        LibraryViewMode.List -> "Switch to grid view"
      },
    )
  }
}
