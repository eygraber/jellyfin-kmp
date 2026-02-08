package com.eygraber.jellyfin.screens.genre.items

sealed interface GenreItemsIntent {
  data object LoadMore : GenreItemsIntent
  data object RetryLoad : GenreItemsIntent
  data class SelectItem(val itemId: String) : GenreItemsIntent
  data object NavigateBack : GenreItemsIntent
}
