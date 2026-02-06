package com.eygraber.jellyfin.data.items

import com.eygraber.jellyfin.common.JellyfinResult

/**
 * Repository for browsing library items.
 *
 * Provides paginated access to media items within Jellyfin libraries,
 * supporting filtering, sorting, and search capabilities.
 */
interface ItemsRepository {
  /**
   * Gets a page of items from a library.
   *
   * @param parentId The library/folder ID to fetch items from.
   * @param includeItemTypes Filter by item types (e.g., "Movie", "Series").
   * @param sortBy The field to sort by.
   * @param sortOrder The sort direction.
   * @param startIndex The index of the first item to return (for pagination).
   * @param limit The maximum number of items to return.
   * @param genres Filter by genre names.
   * @param years Filter by production years.
   * @param searchTerm Optional search term to filter results.
   * @param fields Additional fields to include in the response.
   * @return A [JellyfinResult] containing a [PaginatedResult] of [LibraryItem].
   */
  @Suppress("LongParameterList")
  suspend fun getItems(
    parentId: String,
    includeItemTypes: List<String>? = null,
    sortBy: ItemSortBy = ItemSortBy.SortName,
    sortOrder: SortOrder = SortOrder.Ascending,
    startIndex: Int = 0,
    limit: Int = DEFAULT_PAGE_SIZE,
    genres: List<String>? = null,
    years: List<Int>? = null,
    searchTerm: String? = null,
    fields: List<String>? = null,
  ): JellyfinResult<PaginatedResult<LibraryItem>>

  /**
   * Gets a single item by its ID.
   *
   * @param itemId The item's unique ID.
   * @return A [JellyfinResult] containing the [LibraryItem].
   */
  suspend fun getItem(itemId: String): JellyfinResult<LibraryItem>

  /**
   * Gets items similar to the specified item.
   *
   * @param itemId The item to find similar items for.
   * @param limit Maximum number of similar items to return.
   * @return A [JellyfinResult] containing a list of similar [LibraryItem].
   */
  suspend fun getSimilarItems(
    itemId: String,
    limit: Int? = null,
  ): JellyfinResult<List<LibraryItem>>

  companion object {
    const val DEFAULT_PAGE_SIZE = 50
  }
}
