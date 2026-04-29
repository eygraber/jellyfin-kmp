package com.eygraber.jellyfin.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eygraber.jellyfin.common.successOrNull
import com.eygraber.jellyfin.data.server.ServerRepository
import com.eygraber.jellyfin.domain.session.SessionManager
import com.eygraber.jellyfin.domain.session.SessionState
import com.eygraber.vice.ViceCompositor
import dev.zacsweers.metro.Inject

@Inject
class SettingsCompositor(
  private val sessionManager: SessionManager,
  private val serverRepository: ServerRepository,
  private val navigator: SettingsNavigator,
) : ViceCompositor<SettingsIntent, SettingsViewState> {
  private var userInfo by mutableStateOf<SettingsUserInfo?>(null)
  private var isSignOutDialogVisible by mutableStateOf(false)
  private var isSigningOut by mutableStateOf(false)

  internal val userInfoForTest: SettingsUserInfo? get() = userInfo
  internal val isSignOutDialogVisibleForTest: Boolean get() = isSignOutDialogVisible
  internal val isSigningOutForTest: Boolean get() = isSigningOut

  @Composable
  override fun composite(): SettingsViewState {
    val sessionState = sessionManager.sessionState.value

    LaunchedEffect(sessionState) {
      userInfo = buildUserInfo(sessionState)
    }

    return SettingsViewState(
      userInfo = userInfo,
      isSignOutDialogVisible = isSignOutDialogVisible,
      isSigningOut = isSigningOut,
    )
  }

  override suspend fun onIntent(intent: SettingsIntent) {
    when(intent) {
      SettingsIntent.BackClicked -> navigator.navigateBack()
      is SettingsIntent.CategoryClicked -> navigator.navigateToCategory(intent.category)
      SettingsIntent.SignOutClicked -> isSignOutDialogVisible = true
      SettingsIntent.SignOutDismissed -> isSignOutDialogVisible = false
      SettingsIntent.SignOutConfirmed -> performSignOut()
    }
  }

  private suspend fun buildUserInfo(sessionState: SessionState): SettingsUserInfo? {
    val session = (sessionState as? SessionState.Authenticated)?.session ?: return null
    val server = serverRepository.getServerById(session.serverId).successOrNull
    return SettingsUserInfo(
      username = session.username,
      serverName = server?.name.orEmpty(),
      serverUrl = server?.url.orEmpty(),
    )
  }

  private suspend fun performSignOut() {
    if(isSigningOut) return

    isSigningOut = true
    sessionManager.logout()
    isSigningOut = false
    isSignOutDialogVisible = false
    navigator.navigateToWelcome()
  }
}
