package com.eygraber.jellyfin.screens.movie.detail

sealed interface MovieDetailIntent {
  data object RetryLoad : MovieDetailIntent
  data object NavigateBack : MovieDetailIntent
}
