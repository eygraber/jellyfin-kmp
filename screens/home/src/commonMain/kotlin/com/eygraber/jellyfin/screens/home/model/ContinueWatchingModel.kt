package com.eygraber.jellyfin.screens.home.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.screens.home.ContinueWatchingItem
import com.eygraber.jellyfin.screens.home.ContinueWatchingState
import com.eygraber.jellyfin.sdk.core.model.BaseItemDto
import com.eygraber.jellyfin.sdk.core.model.ImageType
import com.eygraber.jellyfin.services.sdk.JellyfinLibraryService
import com.eygraber.vice.ViceSource
import dev.zacsweers.metro.Inject

@Inject
class ContinueWatchingModel(
  private val libraryService: JellyfinLibraryService,
) : ViceSource<ContinueWatchingState> {
  private var state by mutableStateOf<ContinueWatchingState>(ContinueWatchingState.Loading)

  internal val stateForTest: ContinueWatchingState get() = state

  @Composable
  override fun currentState(): ContinueWatchingState {
    LaunchedEffect(Unit) {
      load()
    }

    return state
  }

  suspend fun refresh() {
    load()
  }

  private suspend fun load() {
    state = ContinueWatchingState.Loading

    val result = libraryService.getResumeItems(
      limit = RESUME_ITEMS_LIMIT,
      mediaTypes = listOf("Video"),
      fields = listOf("PrimaryImageAspectRatio"),
    )

    state = if(result.isSuccess()) {
      val items = result.value.items
        .filter { it.id != null }
        .map { dto ->
          val itemId = requireNotNull(dto.id)

          val runTimeTicks = dto.runTimeTicks ?: 0L
          val positionTicks = dto.userData?.playbackPositionTicks ?: 0L
          val progressPercent = if(runTimeTicks > 0L) {
            (positionTicks.toFloat() / runTimeTicks.toFloat()).coerceIn(
              minimumValue = 0F,
              maximumValue = 1F,
            )
          }
          else {
            0F
          }

          ContinueWatchingItem(
            id = itemId,
            name = dto.name.orEmpty(),
            type = dto.type.orEmpty(),
            seriesName = dto.seriesName,
            seasonName = dto.seasonName,
            indexNumber = dto.indexNumber,
            parentIndexNumber = dto.parentIndexNumber,
            progressPercent = progressPercent,
            imageUrl = libraryService.getImageUrl(
              itemId = itemId,
              imageType = ImageType.Primary,
              maxWidth = IMAGE_MAX_WIDTH,
              tag = dto.imageTags["Primary"],
            ),
            backdropImageUrl = resolveBackdropUrl(dto, itemId),
          )
        }

      if(items.isEmpty()) {
        ContinueWatchingState.Empty
      }
      else {
        ContinueWatchingState.Loaded(items = items)
      }
    }
    else {
      ContinueWatchingState.Error
    }
  }

  private fun resolveBackdropUrl(dto: BaseItemDto, itemId: String): String? {
    val backdropTag = dto.backdropImageTags.firstOrNull()
    if(backdropTag != null) {
      return libraryService.getImageUrl(
        itemId = itemId,
        imageType = ImageType.Backdrop,
        maxWidth = BACKDROP_MAX_WIDTH,
        tag = backdropTag,
      )
    }

    return dto.parentBackdropItemId?.let { parentId ->
      dto.parentBackdropImageTags.firstOrNull()?.let { parentTag ->
        libraryService.getImageUrl(
          itemId = parentId,
          imageType = ImageType.Backdrop,
          maxWidth = BACKDROP_MAX_WIDTH,
          tag = parentTag,
        )
      }
    }
  }

  companion object {
    private const val RESUME_ITEMS_LIMIT = 12
    private const val IMAGE_MAX_WIDTH = 300
    private const val BACKDROP_MAX_WIDTH = 600
  }
}
