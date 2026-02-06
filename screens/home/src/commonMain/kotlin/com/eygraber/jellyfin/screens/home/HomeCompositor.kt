package com.eygraber.jellyfin.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eygraber.jellyfin.domain.session.SessionManager
import com.eygraber.jellyfin.domain.session.SessionState
import com.eygraber.jellyfin.screens.home.model.ContinueWatchingModel
import com.eygraber.vice.ViceCompositor
import dev.zacsweers.metro.Inject

@Inject
class HomeCompositor(
  private val sessionManager: SessionManager,
  private val navigator: HomeNavigator,
  private val continueWatchingModel: ContinueWatchingModel,
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

    val continueWatchingState = continueWatchingModel.currentState()

    return HomeViewState(
      userName = userName,
      isLoading = isLoading,
      error = error,
      isRefreshing = isRefreshing,
      continueWatchingState = continueWatchingState,
    )
  }

  override suspend fun onIntent(intent: HomeIntent) {
    when(intent) {
      HomeIntent.Refresh -> refresh()
      HomeIntent.RetryLoad -> retryLoad()
      is HomeIntent.ContinueWatchingItemClicked -> navigator.navigateToItemDetail(intent.itemId)
    }
  }

  private suspend fun refresh() {
    isRefreshing = true
    error = null

    val isValid = sessionManager.validateSession()
    if(!isValid) {
      error = HomeError.Network()
    }

    continueWatchingModel.refresh()

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
