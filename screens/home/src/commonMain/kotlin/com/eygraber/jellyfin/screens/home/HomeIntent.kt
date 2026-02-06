package com.eygraber.jellyfin.screens.home

sealed interface HomeIntent {
  data object Refresh : HomeIntent
  data object RetryLoad : HomeIntent
}
