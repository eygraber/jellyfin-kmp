package com.eygraber.jellyfin.screens.library.tvshows

class TvShowsLibraryNavigator(
  private val onNavigateBack: () -> Unit,
  private val onNavigateToShowSeasons: (showId: String) -> Unit,
) {
  fun navigateBack() {
    onNavigateBack()
  }

  fun navigateToShowSeasons(showId: String) {
    onNavigateToShowSeasons(showId)
  }
}
