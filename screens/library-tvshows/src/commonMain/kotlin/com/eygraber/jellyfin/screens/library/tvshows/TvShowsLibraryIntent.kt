package com.eygraber.jellyfin.screens.library.tvshows

import com.eygraber.jellyfin.data.items.ItemSortBy
import com.eygraber.jellyfin.data.items.SortOrder
import com.eygraber.jellyfin.ui.library.controls.LibraryFilters
import com.eygraber.jellyfin.ui.library.controls.LibraryViewMode

sealed interface TvShowsLibraryIntent {
  data object LoadMore : TvShowsLibraryIntent
  data object Refresh : TvShowsLibraryIntent
  data object RetryLoad : TvShowsLibraryIntent
  data class SelectShow(val showId: String) : TvShowsLibraryIntent
  data class ChangeSortOption(val sortBy: ItemSortBy, val sortOrder: SortOrder) : TvShowsLibraryIntent
  data class ChangeFilters(val filters: LibraryFilters) : TvShowsLibraryIntent
  data class ChangeViewMode(val viewMode: LibraryViewMode) : TvShowsLibraryIntent
  data object ToggleFilterSheet : TvShowsLibraryIntent
  data object NavigateBack : TvShowsLibraryIntent
}
