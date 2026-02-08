package com.eygraber.jellyfin.screens.library.music

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.eygraber.jellyfin.screens.library.music.model.MusicLibraryModel
import com.eygraber.jellyfin.screens.library.music.model.MusicLibraryModelError
import com.eygraber.jellyfin.ui.library.controls.LibrarySortConfig
import com.eygraber.vice.ViceCompositor
import dev.zacsweers.metro.Inject

@Inject
class MusicLibraryCompositor(
  private val key: MusicLibraryKey,
  private val navigator: MusicLibraryNavigator,
  private val musicModel: MusicLibraryModel,
) : ViceCompositor<MusicLibraryIntent, MusicLibraryViewState> {

  @Composable
  override fun composite(): MusicLibraryViewState {
    val modelState = musicModel.currentState()

    LaunchedEffect(Unit) {
      musicModel.loadInitial(key.libraryId)
    }

    return MusicLibraryViewState(
      selectedTab = modelState.selectedTab,
      artists = modelState.artists,
      albums = modelState.albums,
      isLoading = modelState.isLoading,
      isLoadingMore = modelState.isLoadingMore,
      error = modelState.error?.toViewError(),
      hasMore = modelState.hasMore,
      isEmpty = !modelState.isLoading &&
        modelState.error == null &&
        modelState.artists.isEmpty() &&
        modelState.albums.isEmpty(),
      sortConfig = modelState.sortConfig,
    )
  }

  override suspend fun onIntent(intent: MusicLibraryIntent) {
    when(intent) {
      MusicLibraryIntent.LoadMore -> musicModel.loadMore(key.libraryId)
      MusicLibraryIntent.Refresh -> musicModel.refresh(key.libraryId)
      MusicLibraryIntent.RetryLoad -> musicModel.loadInitial(key.libraryId)
      is MusicLibraryIntent.SelectTab -> musicModel.switchTab(key.libraryId, intent.tab)
      is MusicLibraryIntent.SelectArtist -> navigator.navigateToArtistAlbums(intent.artistId)
      is MusicLibraryIntent.SelectAlbum -> navigator.navigateToAlbumTracks(intent.albumId)

      is MusicLibraryIntent.ChangeSortOption -> {
        musicModel.updateSortConfig(
          LibrarySortConfig(sortBy = intent.sortBy, sortOrder = intent.sortOrder),
        )
        musicModel.loadInitial(key.libraryId)
      }

      MusicLibraryIntent.NavigateBack -> navigator.navigateBack()
    }
  }

  private fun MusicLibraryModelError.toViewError(): MusicLibraryError = when(this) {
    MusicLibraryModelError.LoadFailed -> MusicLibraryError.Network()
  }
}
