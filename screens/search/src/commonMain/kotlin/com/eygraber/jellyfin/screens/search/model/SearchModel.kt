package com.eygraber.jellyfin.screens.search.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.data.search.SearchRepository
import com.eygraber.jellyfin.data.search.SearchResultItem
import com.eygraber.jellyfin.screens.search.SearchViewItem
import com.eygraber.jellyfin.sdk.core.model.ImageType
import com.eygraber.jellyfin.services.sdk.JellyfinLibraryService
import com.eygraber.vice.ViceSource
import dev.zacsweers.metro.Inject

data class SearchModelState(
  val query: String = "",
  val movieResults: List<SearchViewItem> = emptyList(),
  val seriesResults: List<SearchViewItem> = emptyList(),
  val episodeResults: List<SearchViewItem> = emptyList(),
  val musicResults: List<SearchViewItem> = emptyList(),
  val peopleResults: List<SearchViewItem> = emptyList(),
  val isLoading: Boolean = false,
  val isEmptyResults: Boolean = false,
  val error: SearchModelError? = null,
)

enum class SearchModelError {
  SearchFailed,
}

@Inject
class SearchModel(
  private val searchRepository: SearchRepository,
  private val libraryService: JellyfinLibraryService,
) : ViceSource<SearchModelState> {
  private var state by mutableStateOf(SearchModelState())

  internal val stateForTest: SearchModelState get() = state

  @Composable
  override fun currentState(): SearchModelState = state

  suspend fun search(query: String) {
    if(query.isBlank()) {
      state = SearchModelState(query = query)
      return
    }

    state = state.copy(
      query = query,
      isLoading = true,
      error = null,
    )

    val result = searchRepository.search(query = query)

    if(result.isSuccess()) {
      val searchResults = result.value
      state = SearchModelState(
        query = query,
        movieResults = searchResults.movies.map { it.toViewItem() },
        seriesResults = searchResults.series.map { it.toViewItem() },
        episodeResults = searchResults.episodes.map { it.toViewItem() },
        musicResults = searchResults.music.map { it.toViewItem() },
        peopleResults = searchResults.people.map { it.toViewItem() },
        isLoading = false,
        isEmptyResults = searchResults.isEmpty,
      )
    }
    else {
      state = state.copy(
        isLoading = false,
        error = SearchModelError.SearchFailed,
      )
    }
  }

  fun clearSearch() {
    state = SearchModelState()
  }

  private fun SearchResultItem.toViewItem(): SearchViewItem = SearchViewItem(
    id = id,
    name = name,
    type = type,
    year = productionYear?.toString(),
    imageUrl = primaryImageTag?.let { tag ->
      libraryService.getImageUrl(
        itemId = id,
        imageType = ImageType.Primary,
        maxWidth = POSTER_MAX_WIDTH,
        tag = tag,
      )
    },
    subtitle = seriesName,
  )

  companion object {
    private const val POSTER_MAX_WIDTH = 200
  }
}
