package com.eygraber.jellyfin.screens.movie.detail.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.data.items.ItemsRepository
import com.eygraber.jellyfin.data.items.LibraryItem
import com.eygraber.jellyfin.screens.movie.detail.MovieDetail
import com.eygraber.jellyfin.sdk.core.model.ImageType
import com.eygraber.jellyfin.services.sdk.JellyfinLibraryService
import com.eygraber.vice.ViceSource
import dev.zacsweers.metro.Inject

data class MovieDetailState(
  val movie: MovieDetail? = null,
  val isLoading: Boolean = true,
  val error: MovieDetailModelError? = null,
)

enum class MovieDetailModelError {
  LoadFailed,
}

@Inject
class MovieDetailModel(
  private val itemsRepository: ItemsRepository,
  private val libraryService: JellyfinLibraryService,
) : ViceSource<MovieDetailState> {
  private var state by mutableStateOf(MovieDetailState())

  internal val stateForTest: MovieDetailState get() = state

  @Composable
  override fun currentState(): MovieDetailState = state

  suspend fun loadMovie(movieId: String) {
    state = state.copy(isLoading = true, error = null)

    val result = itemsRepository.getItem(itemId = movieId)

    state = if(result.isSuccess()) {
      MovieDetailState(
        movie = result.value.toMovieDetail(),
        isLoading = false,
      )
    }
    else {
      MovieDetailState(
        isLoading = false,
        error = MovieDetailModelError.LoadFailed,
      )
    }
  }

  private fun LibraryItem.toMovieDetail(): MovieDetail = MovieDetail(
    id = id,
    name = name,
    overview = overview,
    productionYear = productionYear,
    communityRating = communityRating,
    officialRating = officialRating,
    runtimeMinutes = runTimeTicks?.let { ticks -> (ticks / TICKS_PER_MINUTE).toInt() },
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

  companion object {
    private const val TICKS_PER_MINUTE = 600_000_000L
    private const val BACKDROP_MAX_WIDTH = 1_920
    private const val POSTER_MAX_WIDTH = 400
  }
}
