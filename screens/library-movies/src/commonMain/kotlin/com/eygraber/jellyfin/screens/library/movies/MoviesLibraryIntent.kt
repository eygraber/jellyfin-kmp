package com.eygraber.jellyfin.screens.library.movies

import com.eygraber.jellyfin.data.items.ItemSortBy
import com.eygraber.jellyfin.data.items.SortOrder
import com.eygraber.jellyfin.ui.library.controls.LibraryFilters
import com.eygraber.jellyfin.ui.library.controls.LibraryViewMode

sealed interface MoviesLibraryIntent {
  data object LoadMore : MoviesLibraryIntent
  data object Refresh : MoviesLibraryIntent
  data object RetryLoad : MoviesLibraryIntent
  data class SelectMovie(val movieId: String) : MoviesLibraryIntent
  data class ChangeSortOption(val sortBy: ItemSortBy, val sortOrder: SortOrder) : MoviesLibraryIntent
  data class ChangeFilters(val filters: LibraryFilters) : MoviesLibraryIntent
  data class ChangeViewMode(val viewMode: LibraryViewMode) : MoviesLibraryIntent
  data object ToggleFilterSheet : MoviesLibraryIntent
  data object NavigateBack : MoviesLibraryIntent
}
