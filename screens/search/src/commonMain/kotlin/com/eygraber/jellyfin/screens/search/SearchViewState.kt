package com.eygraber.jellyfin.screens.search

import androidx.compose.runtime.Immutable

@Immutable
data class SearchViewState(
  val query: String = "",
  val movieResults: List<SearchViewItem> = emptyList(),
  val seriesResults: List<SearchViewItem> = emptyList(),
  val episodeResults: List<SearchViewItem> = emptyList(),
  val musicResults: List<SearchViewItem> = emptyList(),
  val peopleResults: List<SearchViewItem> = emptyList(),
  val isLoading: Boolean = false,
  val isEmptyResults: Boolean = false,
  val error: SearchError? = null,
) {
  val hasResults: Boolean get() =
    movieResults.isNotEmpty() ||
      seriesResults.isNotEmpty() ||
      episodeResults.isNotEmpty() ||
      musicResults.isNotEmpty() ||
      peopleResults.isNotEmpty()

  companion object {
    val Initial = SearchViewState()
  }
}

@Immutable
sealed interface SearchError {
  val message: String

  data class Network(
    override val message: String = "Unable to connect to server",
  ) : SearchError
}

@Immutable
data class SearchViewItem(
  val id: String,
  val name: String,
  val type: String,
  val year: String?,
  val imageUrl: String?,
  val subtitle: String?,
)
