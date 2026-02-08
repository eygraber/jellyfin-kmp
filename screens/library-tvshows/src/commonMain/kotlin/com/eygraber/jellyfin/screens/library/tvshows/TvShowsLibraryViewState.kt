package com.eygraber.jellyfin.screens.library.tvshows

import androidx.compose.runtime.Immutable
import com.eygraber.jellyfin.ui.library.controls.LibraryFilters
import com.eygraber.jellyfin.ui.library.controls.LibrarySortConfig
import com.eygraber.jellyfin.ui.library.controls.LibraryViewMode

@Immutable
data class TvShowsLibraryViewState(
  val items: List<TvShowItem> = emptyList(),
  val isLoading: Boolean = true,
  val isLoadingMore: Boolean = false,
  val error: TvShowsLibraryError? = null,
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
    val Loading = TvShowsLibraryViewState(isLoading = true)
  }
}

@Immutable
sealed interface TvShowsLibraryError {
  val message: String

  data class Network(
    override val message: String = "Unable to connect to server",
  ) : TvShowsLibraryError

  data class Generic(
    override val message: String = "Something went wrong",
  ) : TvShowsLibraryError
}

@Immutable
data class TvShowItem(
  val id: String,
  val name: String,
  val productionYear: Int?,
  val communityRating: Float?,
  val officialRating: String?,
  val seasonCount: Int?,
  val imageUrl: String?,
)
