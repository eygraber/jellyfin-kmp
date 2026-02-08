package com.eygraber.jellyfin.screens.home

sealed interface HomeIntent {
  data object Refresh : HomeIntent
  data object RetryLoad : HomeIntent
  data object SearchClicked : HomeIntent
  data class ContinueWatchingItemClicked(val itemId: String) : HomeIntent
  data class NextUpItemClicked(val itemId: String) : HomeIntent
  data class RecentlyAddedItemClicked(val itemId: String) : HomeIntent
  data class LibraryClicked(
    val libraryId: String,
    val collectionType: CollectionType,
  ) : HomeIntent
}
