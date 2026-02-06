package com.eygraber.jellyfin.screens.home

import androidx.compose.runtime.Immutable

@Immutable
data class HomeViewState(
  val userName: String = "",
  val isLoading: Boolean = true,
  val error: HomeError? = null,
  val isRefreshing: Boolean = false,
) {
  companion object {
    val Loading = HomeViewState(isLoading = true)
  }
}

@Immutable
sealed interface HomeError {
  val message: String

  data class Network(override val message: String = "Unable to connect to server") : HomeError
  data class Generic(override val message: String = "Something went wrong") : HomeError
}
