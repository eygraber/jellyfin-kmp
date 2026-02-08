package com.eygraber.jellyfin.screens.tvshow.episodes.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.data.items.ItemSortBy
import com.eygraber.jellyfin.data.items.ItemsRepository
import com.eygraber.jellyfin.data.items.LibraryItem
import com.eygraber.jellyfin.data.items.SortOrder
import com.eygraber.jellyfin.screens.tvshow.episodes.EpisodeItem
import com.eygraber.jellyfin.sdk.core.model.ImageType
import com.eygraber.jellyfin.services.sdk.JellyfinLibraryService
import com.eygraber.vice.ViceSource
import dev.zacsweers.metro.Inject

data class TvShowEpisodesState(
  val seasonName: String = "",
  val episodes: List<EpisodeItem> = emptyList(),
  val isLoading: Boolean = true,
  val error: TvShowEpisodesModelError? = null,
)

enum class TvShowEpisodesModelError {
  LoadFailed,
}

@Inject
class TvShowEpisodesModel(
  private val itemsRepository: ItemsRepository,
  private val libraryService: JellyfinLibraryService,
) : ViceSource<TvShowEpisodesState> {
  private var state by mutableStateOf(TvShowEpisodesState())

  internal val stateForTest: TvShowEpisodesState get() = state

  @Composable
  override fun currentState(): TvShowEpisodesState = state

  suspend fun loadEpisodes(seasonId: String) {
    state = state.copy(isLoading = true, error = null)

    val seasonResult = itemsRepository.getItem(seasonId)
    val seasonName = if(seasonResult.isSuccess()) seasonResult.value.name else ""

    val result = itemsRepository.getItems(
      parentId = seasonId,
      includeItemTypes = listOf("Episode"),
      sortBy = ItemSortBy.SortName,
      sortOrder = SortOrder.Ascending,
      startIndex = 0,
      limit = MAX_EPISODES,
    )

    state = if(result.isSuccess()) {
      val episodes = result.value.items.map { item -> item.toEpisodeItem() }

      TvShowEpisodesState(
        seasonName = seasonName,
        episodes = episodes,
        isLoading = false,
      )
    }
    else {
      TvShowEpisodesState(
        seasonName = seasonName,
        isLoading = false,
        error = TvShowEpisodesModelError.LoadFailed,
      )
    }
  }

  private fun LibraryItem.toEpisodeItem(): EpisodeItem = EpisodeItem(
    id = id,
    name = name,
    episodeNumber = productionYear,
    overview = overview,
    runtimeMinutes = runTimeTicks?.let { ticks -> (ticks / TICKS_PER_MINUTE).toInt() },
    imageUrl = primaryImageTag?.let { tag ->
      libraryService.getImageUrl(
        itemId = id,
        imageType = ImageType.Primary,
        maxWidth = THUMBNAIL_MAX_WIDTH,
        tag = tag,
      )
    },
  )

  companion object {
    private const val MAX_EPISODES = 100
    private const val THUMBNAIL_MAX_WIDTH = 400
    private const val TICKS_PER_MINUTE = 600_000_000L
  }
}
