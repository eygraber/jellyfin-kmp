package com.eygraber.jellyfin.data.livetv

/**
 * A page of Live TV results from a paginated server query.
 *
 * Mirrors the shape used by other data layers (e.g. `data/items`) but is
 * defined separately to keep the Live TV module from depending on `data/items`.
 */
data class LiveTvPaginatedResult<T>(
  val items: List<T>,
  val totalRecordCount: Int,
  val startIndex: Int,
) {
  /**
   * Whether there are more items to load beyond this page.
   */
  val hasMore: Boolean get() = startIndex + items.size < totalRecordCount
}
