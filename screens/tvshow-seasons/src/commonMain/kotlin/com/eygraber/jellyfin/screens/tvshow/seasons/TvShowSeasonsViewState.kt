package com.eygraber.jellyfin.screens.tvshow.seasons

import androidx.compose.runtime.Immutable

@Immutable
data class TvShowSeasonsViewState(
  val showName: String = "",
  val seasons: List<SeasonItem> = emptyList(),
  val isLoading: Boolean = true,
  val error: TvShowSeasonsError? = null,
  val isEmpty: Boolean = false,
) {
  companion object {
    val Loading = TvShowSeasonsViewState(isLoading = true)
  }
}

@Immutable
sealed interface TvShowSeasonsError {
  val message: String

  data class Network(
    override val message: String = "Unable to connect to server",
  ) : TvShowSeasonsError

  data class Generic(
    override val message: String = "Something went wrong",
  ) : TvShowSeasonsError
}

@Immutable
data class SeasonItem(
  val id: String,
  val name: String,
  val seasonNumber: Int?,
  val episodeCount: Int?,
  val imageUrl: String?,
)
