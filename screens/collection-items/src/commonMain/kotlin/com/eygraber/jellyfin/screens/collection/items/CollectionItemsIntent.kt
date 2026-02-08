package com.eygraber.jellyfin.screens.collection.items

sealed interface CollectionItemsIntent {
  data object LoadMore : CollectionItemsIntent
  data object RetryLoad : CollectionItemsIntent
  data class SelectItem(val itemId: String) : CollectionItemsIntent
  data object NavigateBack : CollectionItemsIntent
}
