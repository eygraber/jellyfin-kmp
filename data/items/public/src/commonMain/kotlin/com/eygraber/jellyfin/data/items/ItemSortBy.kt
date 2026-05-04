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

  /**
   * Sort by episode index within a season. Use [ParentIndexNumberThenIndexNumber]
   * when listing episodes across multiple seasons.
   */
  IndexNumber("IndexNumber"),

  /**
   * Sort by season index then episode index. Useful for cross-season episode lists.
   */
  ParentIndexNumberThenIndexNumber("ParentIndexNumber,IndexNumber"),
}

/**
 * Sort direction.
 */
enum class SortOrder(val apiValue: String) {
  Ascending("Ascending"),
  Descending("Descending"),
}
