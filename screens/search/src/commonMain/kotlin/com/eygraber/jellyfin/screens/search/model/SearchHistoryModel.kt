package com.eygraber.jellyfin.screens.search.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.eygraber.jellyfin.data.search.history.SearchHistoryRepository
import com.eygraber.vice.ViceSource
import dev.zacsweers.metro.Inject

data class SearchHistoryModelState(
  val recentSearches: List<String> = emptyList(),
)

@Inject
class SearchHistoryModel(
  private val searchHistoryRepository: SearchHistoryRepository,
) : ViceSource<SearchHistoryModelState> {

  @Composable
  override fun currentState(): SearchHistoryModelState {
    val entries by searchHistoryRepository
      .observeRecentSearches()
      .collectAsState(initial = emptyList())

    return SearchHistoryModelState(
      recentSearches = entries.map { it.query },
    )
  }

  suspend fun saveSearch(query: String) {
    searchHistoryRepository.saveSearch(query = query)
  }

  suspend fun deleteSearch(query: String) {
    searchHistoryRepository.deleteSearch(query = query)
  }

  suspend fun clearHistory() {
    searchHistoryRepository.clearHistory()
  }
}
