package com.eygraber.jellyfin.screens.library.tvshows

sealed interface TvShowsLibraryIntent {
  data object LoadMore : TvShowsLibraryIntent
  data object Refresh : TvShowsLibraryIntent
  data object RetryLoad : TvShowsLibraryIntent
  data class SelectShow(val showId: String) : TvShowsLibraryIntent
  data object NavigateBack : TvShowsLibraryIntent
}
