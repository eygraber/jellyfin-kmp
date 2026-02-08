package com.eygraber.jellyfin.data.search

import com.eygraber.jellyfin.common.JellyfinResult

/**
 * Repository for searching across all Jellyfin libraries.
 *
 * Provides global search functionality that returns results grouped
 * by item type (Movies, Series, Episodes, etc.).
 */
interface SearchRepository {
  /**
   * Searches across all libraries for items matching the query.
   *
   * @param query The search term.
   * @param limit Maximum number of results per item type.
   * @return A [JellyfinResult] containing [SearchResults] grouped by type.
   */
  suspend fun search(
    query: String,
    limit: Int = DEFAULT_RESULTS_PER_TYPE,
  ): JellyfinResult<SearchResults>

  /**
   * Searches for items of a specific type.
   *
   * @param query The search term.
   * @param itemType The item type to search for (e.g., "Movie", "Series").
   * @param limit Maximum number of results.
   * @return A [JellyfinResult] containing a list of [SearchResultItem].
   */
  suspend fun searchByType(
    query: String,
    itemType: String,
    limit: Int = DEFAULT_RESULTS_PER_TYPE,
  ): JellyfinResult<List<SearchResultItem>>

  companion object {
    const val DEFAULT_RESULTS_PER_TYPE = 10
  }
}
