package com.eygraber.jellyfin.screens.collection.items

import androidx.compose.runtime.Immutable

@Immutable
data class CollectionItemsViewState(
  val collectionName: String = "",
  val items: List<CollectionContentItem> = emptyList(),
  val isLoading: Boolean = true,
  val isLoadingMore: Boolean = false,
  val error: CollectionItemsError? = null,
  val isEmpty: Boolean = false,
  val hasMoreItems: Boolean = true,
) {
  companion object {
    val Loading = CollectionItemsViewState(isLoading = true)
  }
}

@Immutable
sealed interface CollectionItemsError {
  val message: String

  data class Network(
    override val message: String = "Unable to connect to server",
  ) : CollectionItemsError
}

@Immutable
data class CollectionContentItem(
  val id: String,
  val name: String,
  val type: String,
  val productionYear: Int?,
  val imageUrl: String?,
)
