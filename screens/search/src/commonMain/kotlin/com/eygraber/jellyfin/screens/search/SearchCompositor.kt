package com.eygraber.jellyfin.screens.search

import androidx.compose.runtime.Composable
import com.eygraber.jellyfin.screens.search.model.SearchModel
import com.eygraber.jellyfin.screens.search.model.SearchModelError
import com.eygraber.vice.ViceCompositor
import dev.zacsweers.metro.Inject

@Inject
class SearchCompositor(
  private val navigator: SearchNavigator,
  private val searchModel: SearchModel,
) : ViceCompositor<SearchIntent, SearchViewState> {

  @Composable
  override fun composite(): SearchViewState {
    val modelState = searchModel.currentState()

    return SearchViewState(
      query = modelState.query,
      movieResults = modelState.movieResults,
      seriesResults = modelState.seriesResults,
      episodeResults = modelState.episodeResults,
      musicResults = modelState.musicResults,
      peopleResults = modelState.peopleResults,
      isLoading = modelState.isLoading,
      isEmptyResults = modelState.isEmptyResults,
      error = modelState.error?.toViewError(),
    )
  }

  override suspend fun onIntent(intent: SearchIntent) {
    when(intent) {
      is SearchIntent.QueryChanged -> searchModel.search(intent.query)
      SearchIntent.ClearQuery -> searchModel.clearSearch()
      is SearchIntent.ResultClicked -> navigator.navigateToItemDetail(
        itemId = intent.itemId,
        itemType = intent.itemType,
      )
      SearchIntent.NavigateBack -> navigator.navigateBack()
    }
  }

  private fun SearchModelError.toViewError(): SearchError = when(this) {
    SearchModelError.SearchFailed -> SearchError.Network()
  }
}
