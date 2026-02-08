package com.eygraber.jellyfin.screens.movie.detail

class MovieDetailNavigator(
  private val onNavigateBack: () -> Unit,
) {
  fun navigateBack() {
    onNavigateBack()
  }
}
