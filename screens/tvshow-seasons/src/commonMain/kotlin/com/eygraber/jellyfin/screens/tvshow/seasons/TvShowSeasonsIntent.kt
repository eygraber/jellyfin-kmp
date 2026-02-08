package com.eygraber.jellyfin.screens.tvshow.seasons

sealed interface TvShowSeasonsIntent {
  data object RetryLoad : TvShowSeasonsIntent
  data class SelectSeason(val seasonId: String) : TvShowSeasonsIntent
  data object NavigateBack : TvShowSeasonsIntent
}
