package com.eygraber.jellyfin.screens.library.genres

sealed interface GenresLibraryIntent {
  data object RetryLoad : GenresLibraryIntent
  data class SelectGenre(val genreName: String) : GenresLibraryIntent
  data object NavigateBack : GenresLibraryIntent
}
