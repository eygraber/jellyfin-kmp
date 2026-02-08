package com.eygraber.jellyfin.screens.tvshow.detail.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.data.items.ItemSortBy
import com.eygraber.jellyfin.data.items.ItemsRepository
import com.eygraber.jellyfin.data.items.LibraryItem
import com.eygraber.jellyfin.data.items.SortOrder
import com.eygraber.jellyfin.screens.tvshow.detail.TvShowDetail
import com.eygraber.jellyfin.screens.tvshow.detail.TvShowSeasonSummary
import com.eygraber.jellyfin.sdk.core.model.ImageType
import com.eygraber.jellyfin.services.sdk.JellyfinLibraryService
import com.eygraber.vice.ViceSource
import dev.zacsweers.metro.Inject

data class TvShowDetailState(
  val show: TvShowDetail? = null,
  val seasons: List<TvShowSeasonSummary> = emptyList(),
  val isLoading: Boolean = true,
  val error: TvShowDetailModelError? = null,
)

enum class TvShowDetailModelError {
  LoadFailed,
}

@Inject
class TvShowDetailModel(
  private val itemsRepository: ItemsRepository,
  private val libraryService: JellyfinLibraryService,
) : ViceSource<TvShowDetailState> {
  private var state by mutableStateOf(TvShowDetailState())

  internal val stateForTest: TvShowDetailState get() = state

  @Composable
  override fun currentState(): TvShowDetailState = state

  suspend fun loadShow(seriesId: String) {
    state = state.copy(isLoading = true, error = null)

    val showResult = itemsRepository.getItem(itemId = seriesId)

    if(!showResult.isSuccess()) {
      state = TvShowDetailState(
        isLoading = false,
        error = TvShowDetailModelError.LoadFailed,
      )
      return
    }

    val show = showResult.value.toTvShowDetail()

    val seasonsResult = itemsRepository.getItems(
      parentId = seriesId,
      includeItemTypes = listOf("Season"),
      sortBy = ItemSortBy.SortName,
      sortOrder = SortOrder.Ascending,
      startIndex = 0,
      limit = MAX_SEASONS,
    )

    val seasons = if(seasonsResult.isSuccess()) {
      seasonsResult.value.items.map { item -> item.toSeasonSummary() }
    }
    else {
      emptyList()
    }

    state = TvShowDetailState(
      show = show.copy(seasonCount = seasons.size),
      seasons = seasons,
      isLoading = false,
    )
  }

  private fun LibraryItem.toTvShowDetail(): TvShowDetail = TvShowDetail(
    id = id,
    name = name,
    overview = overview,
    productionYear = productionYear,
    communityRating = communityRating,
    officialRating = officialRating,
    seasonCount = 0,
    backdropImageUrl = backdropImageTags.firstOrNull()?.let { tag ->
      libraryService.getImageUrl(
        itemId = id,
        imageType = ImageType.Backdrop,
        maxWidth = BACKDROP_MAX_WIDTH,
        tag = tag,
      )
    },
    posterImageUrl = primaryImageTag?.let { tag ->
      libraryService.getImageUrl(
        itemId = id,
        imageType = ImageType.Primary,
        maxWidth = POSTER_MAX_WIDTH,
        tag = tag,
      )
    },
  )

  private fun LibraryItem.toSeasonSummary(): TvShowSeasonSummary = TvShowSeasonSummary(
    id = id,
    name = name,
    seasonNumber = productionYear,
    episodeCount = childCount,
    imageUrl = primaryImageTag?.let { tag ->
      libraryService.getImageUrl(
        itemId = id,
        imageType = ImageType.Primary,
        maxWidth = SEASON_POSTER_MAX_WIDTH,
        tag = tag,
      )
    },
  )

  companion object {
    private const val MAX_SEASONS = 100
    private const val BACKDROP_MAX_WIDTH = 1_920
    private const val POSTER_MAX_WIDTH = 400
    private const val SEASON_POSTER_MAX_WIDTH = 200
  }
}
