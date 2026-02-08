package com.eygraber.jellyfin.screens.library.movies

sealed interface MoviesLibraryIntent {
  data object LoadMore : MoviesLibraryIntent
  data object Refresh : MoviesLibraryIntent
  data object RetryLoad : MoviesLibraryIntent
  data class SelectMovie(val movieId: String) : MoviesLibraryIntent
  data object NavigateBack : MoviesLibraryIntent
}
