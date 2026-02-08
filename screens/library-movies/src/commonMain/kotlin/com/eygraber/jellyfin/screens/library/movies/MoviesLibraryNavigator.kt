package com.eygraber.jellyfin.screens.library.movies

class MoviesLibraryNavigator(
  private val onNavigateBack: () -> Unit,
  private val onNavigateToMovieDetail: (movieId: String) -> Unit,
) {
  fun navigateBack() {
    onNavigateBack()
  }

  fun navigateToMovieDetail(movieId: String) {
    onNavigateToMovieDetail(movieId)
  }
}
