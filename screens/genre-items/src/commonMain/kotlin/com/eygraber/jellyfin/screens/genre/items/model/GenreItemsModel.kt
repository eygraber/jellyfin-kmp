package com.eygraber.jellyfin.screens.genre.items.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.data.items.ItemSortBy
import com.eygraber.jellyfin.data.items.ItemsRepository
import com.eygraber.jellyfin.data.items.LibraryItem
import com.eygraber.jellyfin.data.items.SortOrder
import com.eygraber.jellyfin.screens.genre.items.GenreContentItem
import com.eygraber.jellyfin.sdk.core.model.ImageType
import com.eygraber.jellyfin.services.sdk.JellyfinLibraryService
import com.eygraber.vice.ViceSource
import dev.zacsweers.metro.Inject

data class GenreItemsState(
  val genreName: String = "",
  val items: List<GenreContentItem> = emptyList(),
  val isLoading: Boolean = true,
  val isLoadingMore: Boolean = false,
  val error: GenreItemsModelError? = null,
  val hasMoreItems: Boolean = true,
  val totalRecordCount: Int = 0,
)

enum class GenreItemsModelError {
  LoadFailed,
}

@Inject
class GenreItemsModel(
  private val itemsRepository: ItemsRepository,
  private val libraryService: JellyfinLibraryService,
) : ViceSource<GenreItemsState> {
  private var state by mutableStateOf(GenreItemsState())

  internal val stateForTest: GenreItemsState get() = state

  @Composable
  override fun currentState(): GenreItemsState = state

  suspend fun loadInitial(libraryId: String, genreName: String) {
    state = GenreItemsState(genreName = genreName, isLoading = true)

    val result = itemsRepository.getItems(
      parentId = libraryId,
      sortBy = ItemSortBy.SortName,
      sortOrder = SortOrder.Ascending,
      startIndex = 0,
      limit = PAGE_SIZE,
      genres = listOf(genreName),
    )

    state = if(result.isSuccess()) {
      val items = result.value.items.map { it.toContentItem() }

      GenreItemsState(
        genreName = genreName,
        items = items,
        isLoading = false,
        hasMoreItems = items.size < result.value.totalRecordCount,
        totalRecordCount = result.value.totalRecordCount,
      )
    }
    else {
      GenreItemsState(
        genreName = genreName,
        isLoading = false,
        error = GenreItemsModelError.LoadFailed,
      )
    }
  }

  suspend fun loadMore(libraryId: String, genreName: String) {
    if(state.isLoadingMore || !state.hasMoreItems) return

    state = state.copy(isLoadingMore = true)

    val result = itemsRepository.getItems(
      parentId = libraryId,
      sortBy = ItemSortBy.SortName,
      sortOrder = SortOrder.Ascending,
      startIndex = state.items.size,
      limit = PAGE_SIZE,
      genres = listOf(genreName),
    )

    state = if(result.isSuccess()) {
      val newItems = result.value.items.map { it.toContentItem() }
      val allItems = state.items + newItems

      state.copy(
        items = allItems,
        isLoadingMore = false,
        hasMoreItems = allItems.size < result.value.totalRecordCount,
      )
    }
    else {
      state.copy(isLoadingMore = false)
    }
  }

  private fun LibraryItem.toContentItem(): GenreContentItem = GenreContentItem(
    id = id,
    name = name,
    productionYear = productionYear,
    imageUrl = primaryImageTag?.let { tag ->
      libraryService.getImageUrl(
        itemId = id,
        imageType = ImageType.Primary,
        maxWidth = IMAGE_MAX_WIDTH,
        tag = tag,
      )
    },
  )

  companion object {
    private const val PAGE_SIZE = 50
    private const val IMAGE_MAX_WIDTH = 300
  }
}
