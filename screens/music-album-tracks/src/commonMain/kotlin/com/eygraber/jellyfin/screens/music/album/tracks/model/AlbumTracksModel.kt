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
import com.eygraber.jellyfin.screens.music.album.tracks.AlbumDetail
import com.eygraber.jellyfin.screens.music.album.tracks.TrackItem
import com.eygraber.jellyfin.sdk.core.model.ImageType
import com.eygraber.jellyfin.services.sdk.JellyfinLibraryService
import com.eygraber.vice.ViceSource
import dev.zacsweers.metro.Inject

data class AlbumTracksState(
  val album: AlbumDetail? = null,
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
  private val libraryService: JellyfinLibraryService,
) : ViceSource<AlbumTracksState> {
  private var state by mutableStateOf(AlbumTracksState())

  internal val stateForTest: AlbumTracksState get() = state

  @Composable
  override fun currentState(): AlbumTracksState = state

  fun currentAlbumArtistId(): String? = state.album?.artistId

  suspend fun loadTracks(albumId: String) {
    state = state.copy(isLoading = true, error = null)

    val albumResult = itemsRepository.getItem(albumId)
    val albumDetail = if(albumResult.isSuccess()) {
      albumResult.value.toAlbumDetail()
    }
    else {
      null
    }

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
        album = albumDetail,
        tracks = tracks,
        isLoading = false,
      )
    }
    else {
      AlbumTracksState(
        album = albumDetail,
        isLoading = false,
        error = AlbumTracksModelError.LoadFailed,
      )
    }
  }

  private fun LibraryItem.toAlbumDetail(): AlbumDetail = AlbumDetail(
    id = id,
    name = name,
    artistName = seriesName.orEmpty(),
    artistId = seriesId,
    productionYear = productionYear,
    genre = officialRating,
    albumArtUrl = primaryImageTag?.let { tag ->
      libraryService.getImageUrl(
        itemId = id,
        imageType = ImageType.Primary,
        maxWidth = ALBUM_ART_MAX_WIDTH,
        tag = tag,
      )
    },
  )

  private fun LibraryItem.toTrackItem(): TrackItem = TrackItem(
    id = id,
    name = name,
    trackNumber = productionYear,
    durationText = runTimeTicks?.let { ticks -> formatDuration(ticks) },
  )

  companion object {
    private const val MAX_TRACKS = 200
    private const val ALBUM_ART_MAX_WIDTH = 600
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
