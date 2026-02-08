package com.eygraber.jellyfin.screens.tvshow.detail

sealed interface TvShowDetailIntent {
  data object RetryLoad : TvShowDetailIntent
  data class SelectSeason(val seasonId: String) : TvShowDetailIntent
  data object NavigateBack : TvShowDetailIntent
}
