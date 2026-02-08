package com.eygraber.jellyfin.data.search

/**
 * A single search result item.
 *
 * Provides the essential information needed to display search results
 * and navigate to the item's detail screen.
 */
data class SearchResultItem(
  val id: String,
  val name: String,
  val type: String,
  val productionYear: Int?,
  val primaryImageTag: String?,
  val seriesName: String?,
  val overview: String?,
)
