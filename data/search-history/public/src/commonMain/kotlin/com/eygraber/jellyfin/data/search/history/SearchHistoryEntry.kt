package com.eygraber.jellyfin.data.search.history

/**
 * A single search history entry.
 *
 * @param query The search query text.
 * @param searchedAt The timestamp (epoch millis) when the search was performed.
 */
data class SearchHistoryEntry(
  val query: String,
  val searchedAt: Long,
)
