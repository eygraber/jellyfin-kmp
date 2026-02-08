package com.eygraber.jellyfin.screens.movie.detail.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.data.items.ItemsRepository
import com.eygraber.jellyfin.data.items.LibraryItem
import com.eygraber.jellyfin.data.items.PersonItem
import com.eygraber.jellyfin.screens.movie.detail.CastMember
import com.eygraber.jellyfin.screens.movie.detail.CrewMember
import com.eygraber.jellyfin.screens.movie.detail.MovieDetail
import com.eygraber.jellyfin.screens.movie.detail.SimilarItem
import com.eygraber.jellyfin.sdk.core.model.ImageType
import com.eygraber.jellyfin.services.sdk.JellyfinLibraryService
import com.eygraber.vice.ViceSource
import dev.zacsweers.metro.Inject

data class MovieDetailState(
  val movie: MovieDetail? = null,
  val cast: List<CastMember> = emptyList(),
  val crew: List<CrewMember> = emptyList(),
  val similarItems: List<SimilarItem> = emptyList(),
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

    if(result.isSuccess()) {
      val item = result.value
      val movieDetail = item.toMovieDetail()
      val cast = item.people
        .filter { it.type == PERSON_TYPE_ACTOR }
        .map { it.toCastMember() }
      val crew = item.people
        .filter { it.type != PERSON_TYPE_ACTOR }
        .map { it.toCrewMember() }

      state = MovieDetailState(
        movie = movieDetail,
        cast = cast,
        crew = crew,
        isLoading = false,
      )

      loadSimilarItems(movieId)
    }
    else {
      state = MovieDetailState(
        isLoading = false,
        error = MovieDetailModelError.LoadFailed,
      )
    }
  }

  private suspend fun loadSimilarItems(movieId: String) {
    val similarResult = itemsRepository.getSimilarItems(
      itemId = movieId,
      limit = MAX_SIMILAR_ITEMS,
    )

    if(similarResult.isSuccess()) {
      state = state.copy(
        similarItems = similarResult.value.map { it.toSimilarItem() },
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

  private fun PersonItem.toCastMember(): CastMember = CastMember(
    id = id,
    name = name,
    role = role,
    imageUrl = primaryImageTag?.let { tag ->
      libraryService.getImageUrl(
        itemId = id,
        imageType = ImageType.Primary,
        maxWidth = PERSON_IMAGE_MAX_WIDTH,
        tag = tag,
      )
    },
  )

  private fun PersonItem.toCrewMember(): CrewMember = CrewMember(
    id = id,
    name = name,
    job = role,
    imageUrl = primaryImageTag?.let { tag ->
      libraryService.getImageUrl(
        itemId = id,
        imageType = ImageType.Primary,
        maxWidth = PERSON_IMAGE_MAX_WIDTH,
        tag = tag,
      )
    },
  )

  private fun LibraryItem.toSimilarItem(): SimilarItem = SimilarItem(
    id = id,
    name = name,
    productionYear = productionYear,
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
    private const val PERSON_TYPE_ACTOR = "Actor"
    private const val TICKS_PER_MINUTE = 600_000_000L
    private const val BACKDROP_MAX_WIDTH = 1_920
    private const val POSTER_MAX_WIDTH = 400
    private const val PERSON_IMAGE_MAX_WIDTH = 200
    private const val MAX_SIMILAR_ITEMS = 12
  }
}
