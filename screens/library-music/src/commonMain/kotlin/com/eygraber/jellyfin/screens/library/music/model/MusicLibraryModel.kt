package com.eygraber.jellyfin.screens.library.music.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.data.items.ItemsRepository
import com.eygraber.jellyfin.data.items.LibraryItem
import com.eygraber.jellyfin.screens.library.music.AlbumItem
import com.eygraber.jellyfin.screens.library.music.ArtistItem
import com.eygraber.jellyfin.screens.library.music.MusicTab
import com.eygraber.jellyfin.sdk.core.model.ImageType
import com.eygraber.jellyfin.services.sdk.JellyfinLibraryService
import com.eygraber.jellyfin.ui.library.controls.LibrarySortConfig
import com.eygraber.vice.ViceSource
import dev.zacsweers.metro.Inject

data class MusicLibraryState(
  val selectedTab: MusicTab = MusicTab.Artists,
  val artists: List<ArtistItem> = emptyList(),
  val albums: List<AlbumItem> = emptyList(),
  val isLoading: Boolean = true,
  val isLoadingMore: Boolean = false,
  val hasMore: Boolean = false,
  val error: MusicLibraryModelError? = null,
  val sortConfig: LibrarySortConfig = LibrarySortConfig(),
)

enum class MusicLibraryModelError {
  LoadFailed,
}

@Inject
class MusicLibraryModel(
  private val itemsRepository: ItemsRepository,
  private val libraryService: JellyfinLibraryService,
) : ViceSource<MusicLibraryState> {
  private var state by mutableStateOf(MusicLibraryState())
  private var currentStartIndex = 0

  internal val stateForTest: MusicLibraryState get() = state

  @Composable
  override fun currentState(): MusicLibraryState = state

  suspend fun loadInitial(libraryId: String) {
    currentStartIndex = 0
    state = state.copy(isLoading = true, error = null, artists = emptyList(), albums = emptyList())

    when(state.selectedTab) {
      MusicTab.Artists -> loadArtists(libraryId, isInitial = true)
      MusicTab.Albums -> loadAlbums(libraryId, isInitial = true)
    }
  }

  suspend fun loadMore(libraryId: String) {
    if(state.isLoadingMore || !state.hasMore) return

    state = state.copy(isLoadingMore = true)

    when(state.selectedTab) {
      MusicTab.Artists -> loadArtists(libraryId, isInitial = false)
      MusicTab.Albums -> loadAlbums(libraryId, isInitial = false)
    }
  }

  suspend fun switchTab(libraryId: String, tab: MusicTab) {
    if(state.selectedTab == tab) return

    currentStartIndex = 0
    state = MusicLibraryState(selectedTab = tab, isLoading = true)

    when(tab) {
      MusicTab.Artists -> loadArtists(libraryId, isInitial = true)
      MusicTab.Albums -> loadAlbums(libraryId, isInitial = true)
    }
  }

  suspend fun refresh(libraryId: String) {
    loadInitial(libraryId)
  }

  fun updateSortConfig(sortConfig: LibrarySortConfig) {
    state = state.copy(sortConfig = sortConfig)
  }

  private suspend fun loadArtists(libraryId: String, isInitial: Boolean) {
    val result = itemsRepository.getItems(
      parentId = libraryId,
      includeItemTypes = listOf("MusicArtist"),
      sortBy = state.sortConfig.sortBy,
      sortOrder = state.sortConfig.sortOrder,
      startIndex = if(isInitial) 0 else currentStartIndex,
      limit = PAGE_SIZE,
    )

    state = if(result.isSuccess()) {
      val paginatedResult = result.value
      val newArtists = paginatedResult.items.map { it.toArtistItem() }
      val allArtists = if(isInitial) newArtists else state.artists + newArtists
      currentStartIndex = allArtists.size

      state.copy(
        artists = allArtists,
        isLoading = false,
        isLoadingMore = false,
        hasMore = paginatedResult.hasMore,
        error = null,
      )
    }
    else {
      state.copy(
        isLoading = false,
        isLoadingMore = false,
        error = MusicLibraryModelError.LoadFailed,
      )
    }
  }

  private suspend fun loadAlbums(libraryId: String, isInitial: Boolean) {
    val result = itemsRepository.getItems(
      parentId = libraryId,
      includeItemTypes = listOf("MusicAlbum"),
      sortBy = state.sortConfig.sortBy,
      sortOrder = state.sortConfig.sortOrder,
      startIndex = if(isInitial) 0 else currentStartIndex,
      limit = PAGE_SIZE,
    )

    state = if(result.isSuccess()) {
      val paginatedResult = result.value
      val newAlbums = paginatedResult.items.map { it.toAlbumItem() }
      val allAlbums = if(isInitial) newAlbums else state.albums + newAlbums
      currentStartIndex = allAlbums.size

      state.copy(
        albums = allAlbums,
        isLoading = false,
        isLoadingMore = false,
        hasMore = paginatedResult.hasMore,
        error = null,
      )
    }
    else {
      state.copy(
        isLoading = false,
        isLoadingMore = false,
        error = MusicLibraryModelError.LoadFailed,
      )
    }
  }

  private fun LibraryItem.toArtistItem(): ArtistItem = ArtistItem(
    id = id,
    name = name,
    albumCount = childCount,
    imageUrl = primaryImageTag?.let { tag ->
      libraryService.getImageUrl(
        itemId = id,
        imageType = ImageType.Primary,
        maxWidth = IMAGE_MAX_WIDTH,
        tag = tag,
      )
    },
  )

  private fun LibraryItem.toAlbumItem(): AlbumItem = AlbumItem(
    id = id,
    name = name,
    artistName = seriesName,
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
