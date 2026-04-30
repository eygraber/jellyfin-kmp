package com.eygraber.jellyfin.screens.episode.detail

sealed interface EpisodeDetailIntent {
  data object RetryLoad : EpisodeDetailIntent
  data class PlayEpisode(val itemId: String, val itemName: String) : EpisodeDetailIntent
  data object NavigateBack : EpisodeDetailIntent
}
