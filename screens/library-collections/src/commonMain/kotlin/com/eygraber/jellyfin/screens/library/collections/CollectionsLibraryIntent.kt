package com.eygraber.jellyfin.screens.library.collections

sealed interface CollectionsLibraryIntent {
  data object LoadMore : CollectionsLibraryIntent
  data object Refresh : CollectionsLibraryIntent
  data object RetryLoad : CollectionsLibraryIntent
  data class SelectCollection(val collectionId: String) : CollectionsLibraryIntent
  data object NavigateBack : CollectionsLibraryIntent
}
