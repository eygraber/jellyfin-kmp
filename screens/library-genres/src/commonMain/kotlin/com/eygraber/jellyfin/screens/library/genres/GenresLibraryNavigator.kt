package com.eygraber.jellyfin.screens.library.genres

class GenresLibraryNavigator(
  private val onNavigateBack: () -> Unit,
  private val onNavigateToGenreItems: (libraryId: String, genreName: String) -> Unit,
) {
  fun navigateBack() {
    onNavigateBack()
  }

  fun navigateToGenreItems(libraryId: String, genreName: String) {
    onNavigateToGenreItems(libraryId, genreName)
  }
}
