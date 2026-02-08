package com.eygraber.jellyfin.screens.music.album.tracks.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.data.items.ItemSortBy
import com.eygraber.jellyfin.data.items.ItemsRepository
import com.eygraber.jellyfin.data.items.LibraryItem
import com.eygraber.jellyfin.data.items.SortOrder
import com.eygraber.jellyfin.screens.music.album.tracks.TrackItem
import com.eygraber.vice.ViceSource
import dev.zacsweers.metro.Inject

data class AlbumTracksState(
  val albumName: String = "",
  val artistName: String = "",
  val tracks: List<TrackItem> = emptyList(),
  val isLoading: Boolean = true,
  val error: AlbumTracksModelError? = null,
)

enum class AlbumTracksModelError {
  LoadFailed,
}

@Inject
class AlbumTracksModel(
  private val itemsRepository: ItemsRepository,
) : ViceSource<AlbumTracksState> {
  private var state by mutableStateOf(AlbumTracksState())

  internal val stateForTest: AlbumTracksState get() = state

  @Composable
  override fun currentState(): AlbumTracksState = state

  suspend fun loadTracks(albumId: String) {
    state = state.copy(isLoading = true, error = null)

    val albumResult = itemsRepository.getItem(albumId)
    val albumName = if(albumResult.isSuccess()) albumResult.value.name else ""
    val artistName = if(albumResult.isSuccess()) albumResult.value.seriesName.orEmpty() else ""

    val result = itemsRepository.getItems(
      parentId = albumId,
      includeItemTypes = listOf("Audio"),
      sortBy = ItemSortBy.SortName,
      sortOrder = SortOrder.Ascending,
      startIndex = 0,
      limit = MAX_TRACKS,
    )

    state = if(result.isSuccess()) {
      val tracks = result.value.items.map { it.toTrackItem() }

      AlbumTracksState(
        albumName = albumName,
        artistName = artistName,
        tracks = tracks,
        isLoading = false,
      )
    }
    else {
      AlbumTracksState(
        albumName = albumName,
        artistName = artistName,
        isLoading = false,
        error = AlbumTracksModelError.LoadFailed,
      )
    }
  }

  private fun LibraryItem.toTrackItem(): TrackItem = TrackItem(
    id = id,
    name = name,
    trackNumber = productionYear,
    durationText = runTimeTicks?.let { ticks -> formatDuration(ticks) },
  )

  companion object {
    private const val MAX_TRACKS = 200
    private const val TICKS_PER_SECOND = 10_000_000L
    private const val SECONDS_PER_MINUTE = 60

    internal fun formatDuration(ticks: Long): String {
      val totalSeconds = (ticks / TICKS_PER_SECOND).toInt()
      val minutes = totalSeconds / SECONDS_PER_MINUTE
      val seconds = totalSeconds % SECONDS_PER_MINUTE
      return "$minutes:${seconds.toString().padStart(2, '0')}"
    }
  }
}
