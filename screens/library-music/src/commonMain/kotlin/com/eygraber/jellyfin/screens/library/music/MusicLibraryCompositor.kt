package com.eygraber.jellyfin.screens.library.music

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.eygraber.jellyfin.screens.library.music.model.MusicLibraryModel
import com.eygraber.jellyfin.screens.library.music.model.MusicLibraryModelError
import com.eygraber.jellyfin.ui.library.controls.LibrarySortConfig
import com.eygraber.jellyfin.ui.library.controls.rememberLibrarySortConfig
import com.eygraber.vice.ViceCompositor
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.channels.Channel
import kotlin.concurrent.Volatile

@Inject
class MusicLibraryCompositor(
  private val key: MusicLibraryKey,
  private val navigator: MusicLibraryNavigator,
  private val musicModel: MusicLibraryModel,
) : ViceCompositor<MusicLibraryIntent, MusicLibraryViewState> {
  // Sort lives in `composite()` via rememberSaveable so it survives composition disposal when the
  // user navigates into an item and back. `onIntent` writes through this channel and the drain
  // coroutine updates the saved state inside the composition.
  private val sortMutations = Channel<LibrarySortConfig>(Channel.CONFLATED)
  @Volatile private var currentSortConfig: LibrarySortConfig = LibrarySortConfig()

  @Composable
  override fun composite(): MusicLibraryViewState {
    var sortConfig by rememberLibrarySortConfig()

    LaunchedEffect(Unit) {
      for(next in sortMutations) sortConfig = next
    }

    val modelState = musicModel.currentState()

    LaunchedEffect(sortConfig) {
      currentSortConfig = sortConfig
      musicModel.loadInitial(key.libraryId, sortConfig)
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
      sortConfig = sortConfig,
    )
  }

  override suspend fun onIntent(intent: MusicLibraryIntent) {
    when(intent) {
      MusicLibraryIntent.LoadMore -> musicModel.loadMore(key.libraryId, currentSortConfig)
      MusicLibraryIntent.Refresh -> musicModel.loadInitial(key.libraryId, currentSortConfig)
      MusicLibraryIntent.RetryLoad -> musicModel.loadInitial(key.libraryId, currentSortConfig)
      is MusicLibraryIntent.SelectTab -> musicModel.switchTab(key.libraryId, intent.tab, currentSortConfig)
      is MusicLibraryIntent.SelectArtist -> navigator.navigateToArtistAlbums(intent.artistId)
      is MusicLibraryIntent.SelectAlbum -> navigator.navigateToAlbumTracks(intent.albumId)

      is MusicLibraryIntent.ChangeSortOption ->
        sortMutations.send(LibrarySortConfig(sortBy = intent.sortBy, sortOrder = intent.sortOrder))

      MusicLibraryIntent.NavigateBack -> navigator.navigateBack()
    }
  }

  private fun MusicLibraryModelError.toViewError(): MusicLibraryError = when(this) {
    MusicLibraryModelError.LoadFailed -> MusicLibraryError.Network()
  }
}
