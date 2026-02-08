package com.eygraber.jellyfin.screens.search

import androidx.compose.runtime.Composable
import com.eygraber.jellyfin.screens.search.model.SearchHistoryModel
import com.eygraber.jellyfin.screens.search.model.SearchModel
import com.eygraber.jellyfin.screens.search.model.SearchModelError
import com.eygraber.vice.ViceCompositor
import dev.zacsweers.metro.Inject

@Inject
class SearchCompositor(
  private val navigator: SearchNavigator,
  private val searchModel: SearchModel,
  private val searchHistoryModel: SearchHistoryModel,
) : ViceCompositor<SearchIntent, SearchViewState> {

  @Composable
  override fun composite(): SearchViewState {
    val modelState = searchModel.currentState()
    val historyState = searchHistoryModel.currentState()

    return SearchViewState(
      query = modelState.query,
      movieResults = modelState.movieResults,
      seriesResults = modelState.seriesResults,
      episodeResults = modelState.episodeResults,
      musicResults = modelState.musicResults,
      peopleResults = modelState.peopleResults,
      recentSearches = historyState.recentSearches,
      isLoading = modelState.isLoading,
      isEmptyResults = modelState.isEmptyResults,
      error = modelState.error?.toViewError(),
    )
  }

  override suspend fun onIntent(intent: SearchIntent) {
    when(intent) {
      is SearchIntent.QueryChanged -> searchModel.search(intent.query)

      SearchIntent.ClearQuery -> searchModel.clearSearch()

      is SearchIntent.ResultClicked -> {
        searchHistoryModel.saveSearch(searchModel.currentQuery)
        navigator.navigateToItemDetail(
          itemId = intent.itemId,
          itemType = intent.itemType,
        )
      }

      is SearchIntent.HistoryItemClicked -> searchModel.search(intent.query)

      is SearchIntent.DeleteHistoryItem -> searchHistoryModel.deleteSearch(intent.query)

      SearchIntent.ClearHistory -> searchHistoryModel.clearHistory()

      SearchIntent.NavigateBack -> navigator.navigateBack()
    }
  }

  private fun SearchModelError.toViewError(): SearchError = when(this) {
    SearchModelError.SearchFailed -> SearchError.Network()
  }
}
