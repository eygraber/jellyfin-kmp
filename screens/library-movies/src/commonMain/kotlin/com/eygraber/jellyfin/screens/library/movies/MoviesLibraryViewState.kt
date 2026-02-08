package com.eygraber.jellyfin.screens.library.movies

import androidx.compose.runtime.Immutable

@Immutable
data class MoviesLibraryViewState(
  val items: List<MovieItem> = emptyList(),
  val isLoading: Boolean = true,
  val isLoadingMore: Boolean = false,
  val error: MoviesLibraryError? = null,
  val hasMore: Boolean = false,
  val isEmpty: Boolean = false,
) {
  companion object {
    val Loading = MoviesLibraryViewState(isLoading = true)
  }
}

@Immutable
sealed interface MoviesLibraryError {
  val message: String

  data class Network(
    override val message: String = "Unable to connect to server",
  ) : MoviesLibraryError

  data class Generic(
    override val message: String = "Something went wrong",
  ) : MoviesLibraryError
}

@Immutable
data class MovieItem(
  val id: String,
  val name: String,
  val productionYear: Int?,
  val communityRating: Float?,
  val officialRating: String?,
  val imageUrl: String?,
)
