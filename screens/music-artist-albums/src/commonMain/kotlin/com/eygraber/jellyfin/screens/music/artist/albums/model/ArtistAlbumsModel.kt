package com.eygraber.jellyfin.screens.music.artist.albums.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.data.items.ItemSortBy
import com.eygraber.jellyfin.data.items.ItemsRepository
import com.eygraber.jellyfin.data.items.LibraryItem
import com.eygraber.jellyfin.data.items.SortOrder
import com.eygraber.jellyfin.screens.music.artist.albums.ArtistAlbumItem
import com.eygraber.jellyfin.screens.music.artist.albums.ArtistDetail
import com.eygraber.jellyfin.sdk.core.model.ImageType
import com.eygraber.jellyfin.services.sdk.JellyfinLibraryService
import com.eygraber.vice.ViceSource
import dev.zacsweers.metro.Inject

data class ArtistAlbumsState(
  val artist: ArtistDetail? = null,
  val albums: List<ArtistAlbumItem> = emptyList(),
  val isLoading: Boolean = true,
  val error: ArtistAlbumsModelError? = null,
)

enum class ArtistAlbumsModelError {
  LoadFailed,
}

@Inject
class ArtistAlbumsModel(
  private val itemsRepository: ItemsRepository,
  private val libraryService: JellyfinLibraryService,
) : ViceSource<ArtistAlbumsState> {
  private var state by mutableStateOf(ArtistAlbumsState())

  internal val stateForTest: ArtistAlbumsState get() = state

  @Composable
  override fun currentState(): ArtistAlbumsState = state

  suspend fun loadAlbums(artistId: String) {
    state = state.copy(isLoading = true, error = null)

    val artistResult = itemsRepository.getItem(artistId)
    val artistDetail = if(artistResult.isSuccess()) {
      artistResult.value.toArtistDetail()
    }
    else {
      null
    }

    val result = itemsRepository.getItems(
      parentId = artistId,
      includeItemTypes = listOf("MusicAlbum"),
      sortBy = ItemSortBy.ProductionYear,
      sortOrder = SortOrder.Descending,
      startIndex = 0,
      limit = MAX_ALBUMS,
    )

    state = if(result.isSuccess()) {
      val albums = result.value.items.map { it.toAlbumItem() }

      ArtistAlbumsState(
        artist = artistDetail,
        albums = albums,
        isLoading = false,
      )
    }
    else {
      ArtistAlbumsState(
        artist = artistDetail,
        isLoading = false,
        error = ArtistAlbumsModelError.LoadFailed,
      )
    }
  }

  private fun LibraryItem.toArtistDetail(): ArtistDetail = ArtistDetail(
    id = id,
    name = name,
    overview = overview,
    genre = officialRating,
    imageUrl = primaryImageTag?.let { tag ->
      libraryService.getImageUrl(
        itemId = id,
        imageType = ImageType.Primary,
        maxWidth = ARTIST_IMAGE_MAX_WIDTH,
        tag = tag,
      )
    },
  )

  private fun LibraryItem.toAlbumItem(): ArtistAlbumItem = ArtistAlbumItem(
    id = id,
    name = name,
    productionYear = productionYear,
    trackCount = childCount,
    imageUrl = primaryImageTag?.let { tag ->
      libraryService.getImageUrl(
        itemId = id,
        imageType = ImageType.Primary,
        maxWidth = ALBUM_IMAGE_MAX_WIDTH,
        tag = tag,
      )
    },
  )

  companion object {
    private const val MAX_ALBUMS = 100
    private const val ARTIST_IMAGE_MAX_WIDTH = 600
    private const val ALBUM_IMAGE_MAX_WIDTH = 300
  }
}
