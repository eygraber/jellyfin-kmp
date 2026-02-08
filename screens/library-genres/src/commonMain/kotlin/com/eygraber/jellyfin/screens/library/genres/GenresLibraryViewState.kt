package com.eygraber.jellyfin.screens.library.genres

import androidx.compose.runtime.Immutable

@Immutable
data class GenresLibraryViewState(
  val genres: List<GenreItem> = emptyList(),
  val isLoading: Boolean = true,
  val error: GenresLibraryError? = null,
  val isEmpty: Boolean = false,
) {
  companion object {
    val Loading = GenresLibraryViewState(isLoading = true)
  }
}

@Immutable
sealed interface GenresLibraryError {
  val message: String

  data class Network(
    override val message: String = "Unable to connect to server",
  ) : GenresLibraryError
}

@Immutable
data class GenreItem(
  val id: String,
  val name: String,
)
