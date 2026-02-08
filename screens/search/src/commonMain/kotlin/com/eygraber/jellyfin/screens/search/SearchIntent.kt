package com.eygraber.jellyfin.screens.search

sealed interface SearchIntent {
  data class QueryChanged(val query: String) : SearchIntent
  data object ClearQuery : SearchIntent
  data class ResultClicked(val itemId: String, val itemType: String) : SearchIntent
  data class HistoryItemClicked(val query: String) : SearchIntent
  data class DeleteHistoryItem(val query: String) : SearchIntent
  data object ClearHistory : SearchIntent
  data object NavigateBack : SearchIntent
}
