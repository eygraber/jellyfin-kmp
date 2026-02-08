package com.eygraber.jellyfin.screens.tvshow.detail

import androidx.compose.runtime.Immutable

@Immutable
data class TvShowDetailViewState(
  val show: TvShowDetail? = null,
  val seasons: List<TvShowSeasonSummary> = emptyList(),
  val isLoading: Boolean = true,
  val error: TvShowDetailError? = null,
) {
  companion object {
    val Loading = TvShowDetailViewState(isLoading = true)
  }
}

@Immutable
sealed interface TvShowDetailError {
  val message: String

  data class Network(
    override val message: String = "Unable to connect to server",
  ) : TvShowDetailError
}

@Immutable
data class TvShowDetail(
  val id: String,
  val name: String,
  val overview: String?,
  val productionYear: Int?,
  val communityRating: Float?,
  val officialRating: String?,
  val seasonCount: Int,
  val backdropImageUrl: String?,
  val posterImageUrl: String?,
)

@Immutable
data class TvShowSeasonSummary(
  val id: String,
  val name: String,
  val seasonNumber: Int?,
  val episodeCount: Int?,
  val imageUrl: String?,
)
