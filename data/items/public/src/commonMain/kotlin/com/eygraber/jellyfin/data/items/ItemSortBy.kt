package com.eygraber.jellyfin.data.items

/**
 * Sort options for library items.
 */
enum class ItemSortBy(val apiValue: String) {
  SortName("SortName"),
  DateCreated("DateCreated"),
  DatePlayed("DatePlayed"),
  PremiereDate("PremiereDate"),
  ProductionYear("ProductionYear"),
  CommunityRating("CommunityRating"),
  Runtime("Runtime"),
  Random("Random"),
}

/**
 * Sort direction.
 */
enum class SortOrder(val apiValue: String) {
  Ascending("Ascending"),
  Descending("Descending"),
}
