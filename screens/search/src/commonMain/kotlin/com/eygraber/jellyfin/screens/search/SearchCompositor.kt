package com.eygraber.jellyfin.screens.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import com.eygraber.jellyfin.screens.search.model.SearchFieldsModel
import com.eygraber.jellyfin.screens.search.model.SearchHistoryModel
import com.eygraber.jellyfin.screens.search.model.SearchModel
import com.eygraber.jellyfin.screens.search.model.SearchModelError
import com.eygraber.vice.ViceCompositor
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.collectLatest

@Inject
class SearchCompositor(
  private val navigator: SearchNavigator,
  private val searchModel: SearchModel,
  private val searchHistoryModel: SearchHistoryModel,
  private val searchFieldsModel: SearchFieldsModel,
) : ViceCompositor<SearchIntent, SearchViewState> {

  @Composable
  override fun composite(): SearchViewState {
    val fields = searchFieldsModel.currentState()
    val modelState = searchModel.currentState()
    val historyState = searchHistoryModel.currentState()

    LaunchedEffect(fields.query) {
      snapshotFlow { fields.query.text.toString() }
        .collectLatest { text ->
          searchModel.search(text)
        }
    }

    return SearchViewState(
      fields = fields,
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
      is SearchIntent.ResultClicked -> {
        searchHistoryModel.saveSearch(searchModel.currentQuery)
        navigator.navigateToItemDetail(
          itemId = intent.itemId,
          itemType = intent.itemType,
        )
      }

      is SearchIntent.HistoryItemClicked -> searchFieldsModel.setQuery(intent.query)

      is SearchIntent.DeleteHistoryItem -> searchHistoryModel.deleteSearch(intent.query)

      SearchIntent.ClearHistory -> searchHistoryModel.clearHistory()

      SearchIntent.NavigateBack -> navigator.navigateBack()
    }
  }

  private fun SearchModelError.toViewError(): SearchError = when(this) {
    SearchModelError.SearchFailed -> SearchError.Network()
  }
}
