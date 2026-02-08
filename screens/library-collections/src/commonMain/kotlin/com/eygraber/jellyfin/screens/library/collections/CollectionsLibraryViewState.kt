package com.eygraber.jellyfin.screens.library.collections

import androidx.compose.runtime.Immutable

@Immutable
data class CollectionsLibraryViewState(
  val collections: List<CollectionItem> = emptyList(),
  val isLoading: Boolean = true,
  val isLoadingMore: Boolean = false,
  val error: CollectionsLibraryError? = null,
  val isEmpty: Boolean = false,
  val hasMoreItems: Boolean = true,
) {
  companion object {
    val Loading = CollectionsLibraryViewState(isLoading = true)
  }
}

@Immutable
sealed interface CollectionsLibraryError {
  val message: String

  data class Network(
    override val message: String = "Unable to connect to server",
  ) : CollectionsLibraryError
}

@Immutable
data class CollectionItem(
  val id: String,
  val name: String,
  val itemCount: Int?,
  val imageUrl: String?,
)
