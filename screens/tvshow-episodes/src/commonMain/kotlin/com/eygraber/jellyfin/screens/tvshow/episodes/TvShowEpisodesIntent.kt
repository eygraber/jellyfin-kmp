package com.eygraber.jellyfin.screens.tvshow.episodes

sealed interface TvShowEpisodesIntent {
  data object RetryLoad : TvShowEpisodesIntent
  data class SelectEpisode(val episodeId: String) : TvShowEpisodesIntent
  data object NavigateBack : TvShowEpisodesIntent
}
