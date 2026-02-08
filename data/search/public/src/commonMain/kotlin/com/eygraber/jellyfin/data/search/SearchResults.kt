package com.eygraber.jellyfin.data.search

/**
 * Search results grouped by item type.
 */
data class SearchResults(
  val movies: List<SearchResultItem> = emptyList(),
  val series: List<SearchResultItem> = emptyList(),
  val episodes: List<SearchResultItem> = emptyList(),
  val music: List<SearchResultItem> = emptyList(),
  val people: List<SearchResultItem> = emptyList(),
) {
  /**
   * Whether the search returned any results at all.
   */
  val isEmpty: Boolean get() =
    movies.isEmpty() &&
      series.isEmpty() &&
      episodes.isEmpty() &&
      music.isEmpty() &&
      people.isEmpty()

  /**
   * Total number of results across all types.
   */
  val totalCount: Int get() =
    movies.size + series.size + episodes.size + music.size + people.size
}
