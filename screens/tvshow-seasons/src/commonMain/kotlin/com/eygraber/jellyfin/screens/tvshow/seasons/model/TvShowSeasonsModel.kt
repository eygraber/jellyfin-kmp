package com.eygraber.jellyfin.screens.tvshow.seasons.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.data.items.ItemSortBy
import com.eygraber.jellyfin.data.items.ItemsRepository
import com.eygraber.jellyfin.data.items.LibraryItem
import com.eygraber.jellyfin.data.items.SortOrder
import com.eygraber.jellyfin.screens.tvshow.seasons.SeasonItem
import com.eygraber.jellyfin.sdk.core.model.ImageType
import com.eygraber.jellyfin.services.sdk.JellyfinLibraryService
import com.eygraber.vice.ViceSource
import dev.zacsweers.metro.Inject

data class TvShowSeasonsState(
  val showName: String = "",
  val seasons: List<SeasonItem> = emptyList(),
  val isLoading: Boolean = true,
  val error: TvShowSeasonsModelError? = null,
)

enum class TvShowSeasonsModelError {
  LoadFailed,
}

@Inject
class TvShowSeasonsModel(
  private val itemsRepository: ItemsRepository,
  private val libraryService: JellyfinLibraryService,
) : ViceSource<TvShowSeasonsState> {
  private var state by mutableStateOf(TvShowSeasonsState())

  internal val stateForTest: TvShowSeasonsState get() = state

  @Composable
  override fun currentState(): TvShowSeasonsState = state

  suspend fun loadSeasons(seriesId: String) {
    state = state.copy(isLoading = true, error = null)

    val showResult = itemsRepository.getItem(seriesId)
    val showName = if(showResult.isSuccess()) showResult.value.name else ""

    val result = itemsRepository.getItems(
      parentId = seriesId,
      includeItemTypes = listOf("Season"),
      sortBy = ItemSortBy.SortName,
      sortOrder = SortOrder.Ascending,
      startIndex = 0,
      limit = MAX_SEASONS,
    )

    state = if(result.isSuccess()) {
      val seasons = result.value.items.map { item -> item.toSeasonItem() }

      TvShowSeasonsState(
        showName = showName,
        seasons = seasons,
        isLoading = false,
      )
    }
    else {
      TvShowSeasonsState(
        showName = showName,
        isLoading = false,
        error = TvShowSeasonsModelError.LoadFailed,
      )
    }
  }

  private fun LibraryItem.toSeasonItem(): SeasonItem = SeasonItem(
    id = id,
    name = name,
    seasonNumber = productionYear,
    episodeCount = childCount,
    imageUrl = primaryImageTag?.let { tag ->
      libraryService.getImageUrl(
        itemId = id,
        imageType = ImageType.Primary,
        maxWidth = POSTER_MAX_WIDTH,
        tag = tag,
      )
    },
  )

  companion object {
    private const val MAX_SEASONS = 100
    private const val POSTER_MAX_WIDTH = 300
  }
}
