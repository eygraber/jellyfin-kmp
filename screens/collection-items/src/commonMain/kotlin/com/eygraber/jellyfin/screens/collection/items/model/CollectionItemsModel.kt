package com.eygraber.jellyfin.screens.collection.items.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.data.items.ItemSortBy
import com.eygraber.jellyfin.data.items.ItemsRepository
import com.eygraber.jellyfin.data.items.LibraryItem
import com.eygraber.jellyfin.data.items.SortOrder
import com.eygraber.jellyfin.screens.collection.items.CollectionContentItem
import com.eygraber.jellyfin.sdk.core.model.ImageType
import com.eygraber.jellyfin.services.sdk.JellyfinLibraryService
import com.eygraber.vice.ViceSource
import dev.zacsweers.metro.Inject

data class CollectionItemsState(
  val collectionName: String = "",
  val items: List<CollectionContentItem> = emptyList(),
  val isLoading: Boolean = true,
  val isLoadingMore: Boolean = false,
  val error: CollectionItemsModelError? = null,
  val hasMoreItems: Boolean = true,
  val totalRecordCount: Int = 0,
)

enum class CollectionItemsModelError {
  LoadFailed,
}

@Inject
class CollectionItemsModel(
  private val itemsRepository: ItemsRepository,
  private val libraryService: JellyfinLibraryService,
) : ViceSource<CollectionItemsState> {
  private var state by mutableStateOf(CollectionItemsState())

  internal val stateForTest: CollectionItemsState get() = state

  @Composable
  override fun currentState(): CollectionItemsState = state

  suspend fun loadInitial(collectionId: String) {
    state = CollectionItemsState(isLoading = true)

    val collectionResult = itemsRepository.getItem(collectionId)
    val collectionName = if(collectionResult.isSuccess()) collectionResult.value.name else ""

    val result = itemsRepository.getItems(
      parentId = collectionId,
      sortBy = ItemSortBy.SortName,
      sortOrder = SortOrder.Ascending,
      startIndex = 0,
      limit = PAGE_SIZE,
    )

    state = if(result.isSuccess()) {
      val items = result.value.items.map { it.toContentItem() }

      CollectionItemsState(
        collectionName = collectionName,
        items = items,
        isLoading = false,
        hasMoreItems = items.size < result.value.totalRecordCount,
        totalRecordCount = result.value.totalRecordCount,
      )
    }
    else {
      CollectionItemsState(
        collectionName = collectionName,
        isLoading = false,
        error = CollectionItemsModelError.LoadFailed,
      )
    }
  }

  suspend fun loadMore(collectionId: String) {
    if(state.isLoadingMore || !state.hasMoreItems) return

    state = state.copy(isLoadingMore = true)

    val result = itemsRepository.getItems(
      parentId = collectionId,
      sortBy = ItemSortBy.SortName,
      sortOrder = SortOrder.Ascending,
      startIndex = state.items.size,
      limit = PAGE_SIZE,
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

  private fun LibraryItem.toContentItem(): CollectionContentItem = CollectionContentItem(
    id = id,
    name = name,
    type = type,
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
