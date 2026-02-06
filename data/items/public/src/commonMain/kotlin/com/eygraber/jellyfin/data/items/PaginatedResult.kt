package com.eygraber.jellyfin.data.items

/**
 * A page of results from a paginated query.
 *
 * @param items The items in this page.
 * @param totalRecordCount The total number of items available on the server.
 * @param startIndex The index of the first item in this page.
 */
data class PaginatedResult<T>(
  val items: List<T>,
  val totalRecordCount: Int,
  val startIndex: Int,
) {
  /**
   * Whether there are more items to load beyond this page.
   */
  val hasMore: Boolean get() = startIndex + items.size < totalRecordCount
}
