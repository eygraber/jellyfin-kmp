package com.eygraber.jellyfin.screens.library.movies

import androidx.compose.runtime.Immutable
import com.eygraber.jellyfin.ui.library.controls.LibraryFilters
import com.eygraber.jellyfin.ui.library.controls.LibrarySortConfig
import com.eygraber.jellyfin.ui.library.controls.LibraryViewMode

@Immutable
data class MoviesLibraryViewState(
  val items: List<MovieItem> = emptyList(),
  val isLoading: Boolean = true,
  val isLoadingMore: Boolean = false,
  val error: MoviesLibraryError? = null,
  val hasMore: Boolean = false,
  val isEmpty: Boolean = false,
  val sortConfig: LibrarySortConfig = LibrarySortConfig(),
  val filters: LibraryFilters = LibraryFilters(),
  val viewMode: LibraryViewMode = LibraryViewMode.Grid,
  val availableGenres: List<String> = emptyList(),
  val availableYears: List<Int> = emptyList(),
  val isFilterSheetVisible: Boolean = false,
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
