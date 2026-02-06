package com.eygraber.jellyfin.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eygraber.jellyfin.domain.session.SessionManager
import com.eygraber.jellyfin.domain.session.SessionState
import com.eygraber.vice.ViceCompositor
import dev.zacsweers.metro.Inject

@Inject
class HomeCompositor(
  private val sessionManager: SessionManager,
) : ViceCompositor<HomeIntent, HomeViewState> {
  private var isLoading by mutableStateOf(true)
  private var isRefreshing by mutableStateOf(false)
  private var error by mutableStateOf<HomeError?>(null)

  @Composable
  override fun composite(): HomeViewState {
    val sessionState = sessionManager.sessionState.value
    val userName = when(sessionState) {
      is SessionState.Authenticated -> sessionState.session.username
      is SessionState.Loading,
      is SessionState.NoSession,
      is SessionState.SessionExpired,
      -> ""
    }

    return HomeViewState(
      userName = userName,
      isLoading = isLoading,
      error = error,
      isRefreshing = isRefreshing,
    )
  }

  override suspend fun onIntent(intent: HomeIntent) {
    when(intent) {
      HomeIntent.Refresh -> refresh()
      HomeIntent.RetryLoad -> retryLoad()
    }
  }

  private suspend fun refresh() {
    isRefreshing = true
    error = null

    val isValid = sessionManager.validateSession()
    if(!isValid) {
      error = HomeError.Network()
    }

    isRefreshing = false
  }

  private suspend fun retryLoad() {
    isLoading = true
    error = null

    val isValid = sessionManager.validateSession()
    if(!isValid) {
      error = HomeError.Network()
    }

    isLoading = false
  }
}
