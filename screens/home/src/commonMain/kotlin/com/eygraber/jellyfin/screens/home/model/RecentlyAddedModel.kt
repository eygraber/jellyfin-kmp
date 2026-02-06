package com.eygraber.jellyfin.screens.home.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.screens.home.RecentlyAddedItem
import com.eygraber.jellyfin.screens.home.RecentlyAddedState
import com.eygraber.jellyfin.sdk.core.model.ImageType
import com.eygraber.jellyfin.services.sdk.JellyfinLibraryService
import com.eygraber.vice.ViceSource
import dev.zacsweers.metro.Inject

@Inject
class RecentlyAddedModel(
  private val libraryService: JellyfinLibraryService,
) : ViceSource<RecentlyAddedState> {
  private var state by mutableStateOf<RecentlyAddedState>(RecentlyAddedState.Loading)

  internal val stateForTest: RecentlyAddedState get() = state

  @Composable
  override fun currentState(): RecentlyAddedState {
    LaunchedEffect(Unit) {
      load()
    }

    return state
  }

  suspend fun refresh() {
    load()
  }

  private suspend fun load() {
    state = RecentlyAddedState.Loading

    val result = libraryService.getLatestItems(
      limit = RECENTLY_ADDED_LIMIT,
      fields = listOf("PrimaryImageAspectRatio"),
    )

    state = if(result.isSuccess()) {
      val items = result.value
        .filter { it.id != null }
        .map { dto ->
          val itemId = requireNotNull(dto.id)
          RecentlyAddedItem(
            id = itemId,
            name = dto.name.orEmpty(),
            type = dto.type.orEmpty(),
            productionYear = dto.productionYear,
            imageUrl = libraryService.getImageUrl(
              itemId = itemId,
              imageType = ImageType.Primary,
              maxWidth = IMAGE_MAX_WIDTH,
              tag = dto.imageTags["Primary"],
            ),
            seriesName = dto.seriesName,
          )
        }

      if(items.isEmpty()) {
        RecentlyAddedState.Empty
      }
      else {
        RecentlyAddedState.Loaded(items = items)
      }
    }
    else {
      RecentlyAddedState.Error
    }
  }

  companion object {
    private const val RECENTLY_ADDED_LIMIT = 16
    private const val IMAGE_MAX_WIDTH = 300
  }
}
