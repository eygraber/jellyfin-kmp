package com.eygraber.jellyfin.ui.library.controls

import androidx.compose.runtime.Immutable
import com.eygraber.jellyfin.data.items.ItemSortBy
import com.eygraber.jellyfin.data.items.SortOrder

/**
 * Represents a sort option for library browsing screens.
 */
@Immutable
data class LibrarySortOption(
  val sortBy: ItemSortBy,
  val label: String,
)

/**
 * Combined sort configuration with sort field and direction.
 */
@Immutable
data class LibrarySortConfig(
  val sortBy: ItemSortBy = ItemSortBy.SortName,
  val sortOrder: SortOrder = SortOrder.Ascending,
)

/**
 * Standard sort options for movie libraries.
 */
val movieSortOptions = listOf(
  LibrarySortOption(sortBy = ItemSortBy.SortName, label = "Title"),
  LibrarySortOption(sortBy = ItemSortBy.DateCreated, label = "Date Added"),
  LibrarySortOption(sortBy = ItemSortBy.PremiereDate, label = "Release Date"),
  LibrarySortOption(sortBy = ItemSortBy.CommunityRating, label = "Rating"),
  LibrarySortOption(sortBy = ItemSortBy.Runtime, label = "Runtime"),
)

/**
 * Standard sort options for TV show libraries.
 */
val tvShowSortOptions = listOf(
  LibrarySortOption(sortBy = ItemSortBy.SortName, label = "Title"),
  LibrarySortOption(sortBy = ItemSortBy.DateCreated, label = "Date Added"),
  LibrarySortOption(sortBy = ItemSortBy.PremiereDate, label = "Release Date"),
  LibrarySortOption(sortBy = ItemSortBy.CommunityRating, label = "Rating"),
)

/**
 * Standard sort options for music libraries.
 */
val musicSortOptions = listOf(
  LibrarySortOption(sortBy = ItemSortBy.SortName, label = "Title"),
  LibrarySortOption(sortBy = ItemSortBy.DateCreated, label = "Date Added"),
  LibrarySortOption(sortBy = ItemSortBy.ProductionYear, label = "Year"),
)
