package com.eygraber.jellyfin.screens.movie.detail

import androidx.compose.runtime.Immutable

@Immutable
data class MovieDetailViewState(
  val movie: MovieDetail? = null,
  val cast: List<CastMember> = emptyList(),
  val crew: List<CrewMember> = emptyList(),
  val similarItems: List<SimilarItem> = emptyList(),
  val isLoading: Boolean = true,
  val error: MovieDetailError? = null,
) {
  companion object {
    val Loading = MovieDetailViewState(isLoading = true)
  }
}

@Immutable
sealed interface MovieDetailError {
  val message: String

  data class Network(
    override val message: String = "Unable to connect to server",
  ) : MovieDetailError
}

@Immutable
data class MovieDetail(
  val id: String,
  val name: String,
  val overview: String?,
  val productionYear: Int?,
  val communityRating: Float?,
  val officialRating: String?,
  val runtimeMinutes: Int?,
  val backdropImageUrl: String?,
  val posterImageUrl: String?,
)

@Immutable
data class CastMember(
  val id: String,
  val name: String,
  val role: String?,
  val imageUrl: String?,
)

@Immutable
data class CrewMember(
  val id: String,
  val name: String,
  val job: String?,
  val imageUrl: String?,
)

@Immutable
data class SimilarItem(
  val id: String,
  val name: String,
  val productionYear: Int?,
  val imageUrl: String?,
)
