package com.eygraber.jellyfin.screens.library.tvshows.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.data.items.ItemSortBy
import com.eygraber.jellyfin.data.items.ItemsRepository
import com.eygraber.jellyfin.data.items.LibraryItem
import com.eygraber.jellyfin.data.items.SortOrder
import com.eygraber.jellyfin.screens.library.tvshows.TvShowItem
import com.eygraber.jellyfin.sdk.core.model.ImageType
import com.eygraber.jellyfin.services.sdk.JellyfinLibraryService
import com.eygraber.jellyfin.ui.library.controls.LibraryFilters
import com.eygraber.jellyfin.ui.library.controls.LibrarySortConfig
import com.eygraber.vice.ViceSource
import dev.zacsweers.metro.Inject

data class TvShowsLibraryState(
  val items: List<TvShowItem> = emptyList(),
  val isLoading: Boolean = true,
  val isLoadingMore: Boolean = false,
  val hasMore: Boolean = false,
  val error: TvShowsLibraryModelError? = null,
  val sortConfig: LibrarySortConfig = LibrarySortConfig(),
  val filters: LibraryFilters = LibraryFilters(),
  val availableGenres: List<String> = emptyList(),
  val availableYears: List<Int> = emptyList(),
)

enum class TvShowsLibraryModelError {
  LoadFailed,
}

@Inject
class TvShowsLibraryModel(
  private val itemsRepository: ItemsRepository,
  private val libraryService: JellyfinLibraryService,
) : ViceSource<TvShowsLibraryState> {
  private var state by mutableStateOf(TvShowsLibraryState())
  private var currentStartIndex = 0

  internal val stateForTest: TvShowsLibraryState get() = state

  @Composable
  override fun currentState(): TvShowsLibraryState = state

  suspend fun loadInitial(libraryId: String) {
    currentStartIndex = 0
    state = state.copy(isLoading = true, error = null, items = emptyList())

    val result = itemsRepository.getItems(
      parentId = libraryId,
      includeItemTypes = listOf("Series"),
      sortBy = state.sortConfig.sortBy,
      sortOrder = state.sortConfig.sortOrder,
      startIndex = 0,
      limit = PAGE_SIZE,
      genres = state.filters.genres.ifEmpty { null },
      years = state.filters.years.ifEmpty { null },
    )

    state = if(result.isSuccess()) {
      val paginatedResult = result.value
      val showItems = paginatedResult.items.map { item -> item.toTvShowItem() }
      currentStartIndex = showItems.size

      state.copy(
        items = showItems,
        isLoading = false,
        hasMore = paginatedResult.hasMore,
        error = null,
      )
    }
    else {
      state.copy(
        isLoading = false,
        error = TvShowsLibraryModelError.LoadFailed,
      )
    }
  }

  suspend fun loadMore(libraryId: String) {
    if(state.isLoadingMore || !state.hasMore) return

    state = state.copy(isLoadingMore = true)

    val result = itemsRepository.getItems(
      parentId = libraryId,
      includeItemTypes = listOf("Series"),
      sortBy = state.sortConfig.sortBy,
      sortOrder = state.sortConfig.sortOrder,
      startIndex = currentStartIndex,
      limit = PAGE_SIZE,
      genres = state.filters.genres.ifEmpty { null },
      years = state.filters.years.ifEmpty { null },
    )

    state = if(result.isSuccess()) {
      val paginatedResult = result.value
      val newItems = paginatedResult.items.map { item -> item.toTvShowItem() }
      currentStartIndex += newItems.size

      state.copy(
        items = state.items + newItems,
        isLoadingMore = false,
        hasMore = paginatedResult.hasMore,
      )
    }
    else {
      state.copy(isLoadingMore = false)
    }
  }

  suspend fun refresh(libraryId: String) {
    loadInitial(libraryId)
  }

  fun updateSortConfig(sortConfig: LibrarySortConfig) {
    state = state.copy(sortConfig = sortConfig)
  }

  fun updateFilters(filters: LibraryFilters) {
    state = state.copy(filters = filters)
  }

  suspend fun loadAvailableFilters(libraryId: String) {
    val genresResult = itemsRepository.getItems(
      parentId = libraryId,
      includeItemTypes = listOf("Genre"),
      sortBy = ItemSortBy.SortName,
      sortOrder = SortOrder.Ascending,
      limit = FILTER_LIMIT,
    )

    if(genresResult.isSuccess()) {
      state = state.copy(
        availableGenres = genresResult.value.items.map { it.name },
      )
    }
  }

  private fun LibraryItem.toTvShowItem(): TvShowItem = TvShowItem(
    id = id,
    name = name,
    productionYear = productionYear,
    communityRating = communityRating,
    officialRating = officialRating,
    seasonCount = childCount,
    imageUrl = primaryImageTag?.let { tag ->
      libraryService.getImageUrl(
        itemId = id,
        imageType = ImageType.Primary,
        maxWidth = POSTER_MAX_WIDTH,
        tag = tag,
      )
    },
  )

  companion object {
    private const val PAGE_SIZE = 50
    private const val POSTER_MAX_WIDTH = 300
    private const val FILTER_LIMIT = 200
  }
}
