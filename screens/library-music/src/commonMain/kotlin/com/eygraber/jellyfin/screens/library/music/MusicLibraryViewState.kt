package com.eygraber.jellyfin.screens.library.music

import androidx.compose.runtime.Immutable
import com.eygraber.jellyfin.ui.library.controls.LibrarySortConfig

@Immutable
data class MusicLibraryViewState(
  val selectedTab: MusicTab = MusicTab.Artists,
  val artists: List<ArtistItem> = emptyList(),
  val albums: List<AlbumItem> = emptyList(),
  val isLoading: Boolean = true,
  val isLoadingMore: Boolean = false,
  val error: MusicLibraryError? = null,
  val hasMore: Boolean = false,
  val isEmpty: Boolean = false,
  val sortConfig: LibrarySortConfig = LibrarySortConfig(),
) {
  companion object {
    val Loading = MusicLibraryViewState(isLoading = true)
  }
}

enum class MusicTab {
  Artists,
  Albums,
}

@Immutable
sealed interface MusicLibraryError {
  val message: String

  data class Network(
    override val message: String = "Unable to connect to server",
  ) : MusicLibraryError

  data class Generic(
    override val message: String = "Something went wrong",
  ) : MusicLibraryError
}

@Immutable
data class ArtistItem(
  val id: String,
  val name: String,
  val albumCount: Int?,
  val imageUrl: String?,
)

@Immutable
data class AlbumItem(
  val id: String,
  val name: String,
  val artistName: String?,
  val productionYear: Int?,
  val imageUrl: String?,
)
