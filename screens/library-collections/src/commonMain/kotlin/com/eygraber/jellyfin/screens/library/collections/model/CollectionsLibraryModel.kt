package com.eygraber.jellyfin.screens.library.collections.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.data.items.ItemSortBy
import com.eygraber.jellyfin.data.items.ItemsRepository
import com.eygraber.jellyfin.data.items.LibraryItem
import com.eygraber.jellyfin.data.items.SortOrder
import com.eygraber.jellyfin.screens.library.collections.CollectionItem
import com.eygraber.jellyfin.sdk.core.model.ImageType
import com.eygraber.jellyfin.services.sdk.JellyfinLibraryService
import com.eygraber.vice.ViceSource
import dev.zacsweers.metro.Inject

data class CollectionsLibraryState(
  val collections: List<CollectionItem> = emptyList(),
  val isLoading: Boolean = true,
  val isLoadingMore: Boolean = false,
  val error: CollectionsLibraryModelError? = null,
  val hasMoreItems: Boolean = true,
  val totalRecordCount: Int = 0,
)

enum class CollectionsLibraryModelError {
  LoadFailed,
}

@Inject
class CollectionsLibraryModel(
  private val itemsRepository: ItemsRepository,
  private val libraryService: JellyfinLibraryService,
) : ViceSource<CollectionsLibraryState> {
  private var state by mutableStateOf(CollectionsLibraryState())

  internal val stateForTest: CollectionsLibraryState get() = state

  @Composable
  override fun currentState(): CollectionsLibraryState = state

  suspend fun loadInitial(libraryId: String) {
    state = CollectionsLibraryState(isLoading = true)

    val result = itemsRepository.getItems(
      parentId = libraryId,
      includeItemTypes = listOf("BoxSet"),
      sortBy = ItemSortBy.SortName,
      sortOrder = SortOrder.Ascending,
      startIndex = 0,
      limit = PAGE_SIZE,
    )

    state = if(result.isSuccess()) {
      val collections = result.value.items.map { it.toCollectionItem() }

      CollectionsLibraryState(
        collections = collections,
        isLoading = false,
        hasMoreItems = collections.size < result.value.totalRecordCount,
        totalRecordCount = result.value.totalRecordCount,
      )
    }
    else {
      CollectionsLibraryState(
        isLoading = false,
        error = CollectionsLibraryModelError.LoadFailed,
      )
    }
  }

  suspend fun loadMore(libraryId: String) {
    if(state.isLoadingMore || !state.hasMoreItems) return

    state = state.copy(isLoadingMore = true)

    val result = itemsRepository.getItems(
      parentId = libraryId,
      includeItemTypes = listOf("BoxSet"),
      sortBy = ItemSortBy.SortName,
      sortOrder = SortOrder.Ascending,
      startIndex = state.collections.size,
      limit = PAGE_SIZE,
    )

    state = if(result.isSuccess()) {
      val newCollections = result.value.items.map { it.toCollectionItem() }
      val allCollections = state.collections + newCollections

      state.copy(
        collections = allCollections,
        isLoadingMore = false,
        hasMoreItems = allCollections.size < result.value.totalRecordCount,
      )
    }
    else {
      state.copy(isLoadingMore = false)
    }
  }

  suspend fun refresh(libraryId: String) {
    loadInitial(libraryId)
  }

  private fun LibraryItem.toCollectionItem(): CollectionItem = CollectionItem(
    id = id,
    name = name,
    itemCount = childCount,
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
