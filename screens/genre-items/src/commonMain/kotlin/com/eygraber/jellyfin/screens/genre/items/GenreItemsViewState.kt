package com.eygraber.jellyfin.screens.genre.items

import androidx.compose.runtime.Immutable

@Immutable
data class GenreItemsViewState(
  val genreName: String = "",
  val items: List<GenreContentItem> = emptyList(),
  val isLoading: Boolean = true,
  val isLoadingMore: Boolean = false,
  val error: GenreItemsError? = null,
  val isEmpty: Boolean = false,
  val hasMoreItems: Boolean = true,
) {
  companion object {
    val Loading = GenreItemsViewState(isLoading = true)
  }
}

@Immutable
sealed interface GenreItemsError {
  val message: String

  data class Network(
    override val message: String = "Unable to connect to server",
  ) : GenreItemsError
}

@Immutable
data class GenreContentItem(
  val id: String,
  val name: String,
  val productionYear: Int?,
  val imageUrl: String?,
)
