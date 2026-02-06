package com.eygraber.jellyfin.screens.home.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.screens.home.NextUpItem
import com.eygraber.jellyfin.screens.home.NextUpState
import com.eygraber.jellyfin.sdk.core.model.BaseItemDto
import com.eygraber.jellyfin.sdk.core.model.ImageType
import com.eygraber.jellyfin.services.sdk.JellyfinLibraryService
import com.eygraber.vice.ViceSource
import dev.zacsweers.metro.Inject

@Inject
class NextUpModel(
  private val libraryService: JellyfinLibraryService,
) : ViceSource<NextUpState> {
  private var state by mutableStateOf<NextUpState>(NextUpState.Loading)

  internal val stateForTest: NextUpState get() = state

  @Composable
  override fun currentState(): NextUpState {
    LaunchedEffect(Unit) {
      load()
    }

    return state
  }

  suspend fun refresh() {
    load()
  }

  private suspend fun load() {
    state = NextUpState.Loading

    val result = libraryService.getNextUpEpisodes(
      limit = NEXT_UP_LIMIT,
      fields = listOf("PrimaryImageAspectRatio", "Overview"),
    )

    state = if(result.isSuccess()) {
      val items = result.value.items
        .filter { it.id != null }
        .map { dto -> mapToNextUpItem(dto) }

      if(items.isEmpty()) {
        NextUpState.Empty
      }
      else {
        NextUpState.Loaded(items = items)
      }
    }
    else {
      NextUpState.Error
    }
  }

  private fun mapToNextUpItem(dto: BaseItemDto): NextUpItem {
    val itemId = requireNotNull(dto.id)

    return NextUpItem(
      id = itemId,
      name = dto.name.orEmpty(),
      seriesName = dto.seriesName,
      seasonName = dto.seasonName,
      indexNumber = dto.indexNumber,
      parentIndexNumber = dto.parentIndexNumber,
      imageUrl = libraryService.getImageUrl(
        itemId = itemId,
        imageType = ImageType.Primary,
        maxWidth = IMAGE_MAX_WIDTH,
        tag = dto.imageTags["Primary"],
      ),
      backdropImageUrl = resolveBackdropUrl(dto, itemId),
    )
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
    private const val NEXT_UP_LIMIT = 12
    private const val IMAGE_MAX_WIDTH = 300
    private const val BACKDROP_MAX_WIDTH = 600
  }
}
