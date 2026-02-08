package com.eygraber.jellyfin.screens.tvshow.episodes

import androidx.compose.runtime.Immutable

@Immutable
data class TvShowEpisodesViewState(
  val seasonName: String = "",
  val episodes: List<EpisodeItem> = emptyList(),
  val isLoading: Boolean = true,
  val error: TvShowEpisodesError? = null,
  val isEmpty: Boolean = false,
) {
  companion object {
    val Loading = TvShowEpisodesViewState(isLoading = true)
  }
}

@Immutable
sealed interface TvShowEpisodesError {
  val message: String

  data class Network(
    override val message: String = "Unable to connect to server",
  ) : TvShowEpisodesError

  data class Generic(
    override val message: String = "Something went wrong",
  ) : TvShowEpisodesError
}

@Immutable
data class EpisodeItem(
  val id: String,
  val name: String,
  val episodeNumber: Int?,
  val overview: String?,
  val runtimeMinutes: Int?,
  val imageUrl: String?,
)
