package com.eygraber.jellyfin.screens.episode.detail

sealed interface EpisodeDetailIntent {
  data object RetryLoad : EpisodeDetailIntent
  data object NavigateBack : EpisodeDetailIntent
}
