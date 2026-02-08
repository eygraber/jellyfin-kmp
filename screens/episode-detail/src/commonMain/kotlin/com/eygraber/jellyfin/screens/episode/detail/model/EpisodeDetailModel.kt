package com.eygraber.jellyfin.screens.episode.detail.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.data.items.ItemsRepository
import com.eygraber.jellyfin.data.items.LibraryItem
import com.eygraber.jellyfin.screens.episode.detail.EpisodeDetail
import com.eygraber.jellyfin.sdk.core.model.ImageType
import com.eygraber.jellyfin.services.sdk.JellyfinLibraryService
import com.eygraber.vice.ViceSource
import dev.zacsweers.metro.Inject

data class EpisodeDetailState(
  val episode: EpisodeDetail? = null,
  val isLoading: Boolean = true,
  val error: EpisodeDetailModelError? = null,
)

enum class EpisodeDetailModelError {
  LoadFailed,
}

@Inject
class EpisodeDetailModel(
  private val itemsRepository: ItemsRepository,
  private val libraryService: JellyfinLibraryService,
) : ViceSource<EpisodeDetailState> {
  private var state by mutableStateOf(EpisodeDetailState())

  internal val stateForTest: EpisodeDetailState get() = state

  @Composable
  override fun currentState(): EpisodeDetailState = state

  suspend fun loadEpisode(episodeId: String) {
    state = state.copy(isLoading = true, error = null)

    val result = itemsRepository.getItem(itemId = episodeId)

    state = if(result.isSuccess()) {
      EpisodeDetailState(
        episode = result.value.toEpisodeDetail(),
        isLoading = false,
      )
    }
    else {
      EpisodeDetailState(
        isLoading = false,
        error = EpisodeDetailModelError.LoadFailed,
      )
    }
  }

  private fun LibraryItem.toEpisodeDetail(): EpisodeDetail {
    val episodeNumber = productionYear
    val seasonEpisodeLabel = episodeNumber?.let { ep ->
      "Episode $ep"
    }

    return EpisodeDetail(
      id = id,
      name = name,
      seriesName = seriesName,
      seasonEpisodeLabel = seasonEpisodeLabel,
      overview = overview,
      runtimeMinutes = runTimeTicks?.let { ticks -> (ticks / TICKS_PER_MINUTE).toInt() },
      thumbnailImageUrl = primaryImageTag?.let { tag ->
        libraryService.getImageUrl(
          itemId = id,
          imageType = ImageType.Primary,
          maxWidth = THUMBNAIL_MAX_WIDTH,
          tag = tag,
        )
      },
    )
  }

  companion object {
    private const val TICKS_PER_MINUTE = 600_000_000L
    private const val THUMBNAIL_MAX_WIDTH = 640
  }
}
