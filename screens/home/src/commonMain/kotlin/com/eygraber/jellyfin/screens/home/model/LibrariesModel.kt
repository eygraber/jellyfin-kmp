package com.eygraber.jellyfin.screens.home.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.screens.home.CollectionType
import com.eygraber.jellyfin.screens.home.LibrariesState
import com.eygraber.jellyfin.screens.home.LibraryView
import com.eygraber.jellyfin.sdk.core.model.ImageType
import com.eygraber.jellyfin.services.sdk.JellyfinLibraryService
import com.eygraber.vice.ViceSource
import dev.zacsweers.metro.Inject

@Inject
class LibrariesModel(
  private val libraryService: JellyfinLibraryService,
) : ViceSource<LibrariesState> {
  private var state by mutableStateOf<LibrariesState>(LibrariesState.Loading)

  internal val stateForTest: LibrariesState get() = state

  @Composable
  override fun currentState(): LibrariesState {
    LaunchedEffect(Unit) {
      load()
    }

    return state
  }

  suspend fun refresh() {
    load()
  }

  private suspend fun load() {
    state = LibrariesState.Loading

    val result = libraryService.getUserViews()

    state = if(result.isSuccess()) {
      val libraries = result.value.items
        .filter { it.id != null }
        .map { dto ->
          val itemId = requireNotNull(dto.id)
          LibraryView(
            id = itemId,
            name = dto.name.orEmpty(),
            collectionType = CollectionType.fromApiValue(dto.collectionType),
            imageUrl = dto.imageTags["Primary"]?.let { tag ->
              libraryService.getImageUrl(
                itemId = itemId,
                imageType = ImageType.Primary,
                maxWidth = IMAGE_MAX_WIDTH,
                tag = tag,
              )
            },
          )
        }

      if(libraries.isEmpty()) {
        LibrariesState.Empty
      }
      else {
        LibrariesState.Loaded(libraries = libraries)
      }
    }
    else {
      LibrariesState.Error
    }
  }

  companion object {
    private const val IMAGE_MAX_WIDTH = 300
  }
}
