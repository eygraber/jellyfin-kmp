package com.eygraber.jellyfin.data.search.history

import com.eygraber.jellyfin.common.JellyfinResult
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing search history.
 *
 * Provides operations to save, retrieve, and clear recent search queries.
 */
interface SearchHistoryRepository {
  /**
   * Observes the recent search history entries, ordered by most recent first.
   *
   * @param limit Maximum number of entries to return.
   * @return A [Flow] emitting the current list of [SearchHistoryEntry].
   */
  fun observeRecentSearches(limit: Int = DEFAULT_HISTORY_LIMIT): Flow<List<SearchHistoryEntry>>

  /**
   * Saves a search query to the history.
   *
   * If the query already exists, its timestamp is updated.
   * Old entries beyond [DEFAULT_HISTORY_LIMIT] are automatically pruned.
   *
   * @param query The search query to save.
   * @return A [JellyfinResult] indicating success or failure.
   */
  suspend fun saveSearch(query: String): JellyfinResult<Unit>

  /**
   * Deletes a specific search history entry.
   *
   * @param query The search query to remove from history.
   * @return A [JellyfinResult] indicating success or failure.
   */
  suspend fun deleteSearch(query: String): JellyfinResult<Unit>

  /**
   * Clears all search history.
   *
   * @return A [JellyfinResult] indicating success or failure.
   */
  suspend fun clearHistory(): JellyfinResult<Unit>

  companion object {
    const val DEFAULT_HISTORY_LIMIT = 20
  }
}
