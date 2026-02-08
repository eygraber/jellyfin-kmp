package com.eygraber.jellyfin.screens.library.genres.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.data.items.ItemSortBy
import com.eygraber.jellyfin.data.items.ItemsRepository
import com.eygraber.jellyfin.data.items.SortOrder
import com.eygraber.jellyfin.screens.library.genres.GenreItem
import com.eygraber.vice.ViceSource
import dev.zacsweers.metro.Inject

data class GenresLibraryState(
  val genres: List<GenreItem> = emptyList(),
  val isLoading: Boolean = true,
  val error: GenresLibraryModelError? = null,
)

enum class GenresLibraryModelError {
  LoadFailed,
}

@Inject
class GenresLibraryModel(
  private val itemsRepository: ItemsRepository,
) : ViceSource<GenresLibraryState> {
  private var state by mutableStateOf(GenresLibraryState())

  internal val stateForTest: GenresLibraryState get() = state

  @Composable
  override fun currentState(): GenresLibraryState = state

  suspend fun loadGenres(libraryId: String) {
    state = GenresLibraryState(isLoading = true)

    val result = itemsRepository.getItems(
      parentId = libraryId,
      includeItemTypes = listOf("Genre"),
      sortBy = ItemSortBy.SortName,
      sortOrder = SortOrder.Ascending,
      startIndex = 0,
      limit = MAX_GENRES,
    )

    state = if(result.isSuccess()) {
      val genres = result.value.items.map { item ->
        GenreItem(
          id = item.id,
          name = item.name,
        )
      }

      GenresLibraryState(
        genres = genres,
        isLoading = false,
      )
    }
    else {
      GenresLibraryState(
        isLoading = false,
        error = GenresLibraryModelError.LoadFailed,
      )
    }
  }

  companion object {
    private const val MAX_GENRES = 500
  }
}
