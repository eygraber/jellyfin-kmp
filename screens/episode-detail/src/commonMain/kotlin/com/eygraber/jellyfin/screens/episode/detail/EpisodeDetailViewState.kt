package com.eygraber.jellyfin.screens.episode.detail

import androidx.compose.runtime.Immutable

@Immutable
data class EpisodeDetailViewState(
  val episode: EpisodeDetail? = null,
  val isLoading: Boolean = true,
  val error: EpisodeDetailError? = null,
) {
  companion object {
    val Loading = EpisodeDetailViewState(isLoading = true)
  }
}

@Immutable
sealed interface EpisodeDetailError {
  val message: String

  data class Network(
    override val message: String = "Unable to connect to server",
  ) : EpisodeDetailError
}

@Immutable
data class EpisodeDetail(
  val id: String,
  val name: String,
  val seriesName: String?,
  val seasonEpisodeLabel: String?,
  val overview: String?,
  val runtimeMinutes: Int?,
  val thumbnailImageUrl: String?,
)
