package com.eygraber.jellyfin.domain.session.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.common.successOrNull
import com.eygraber.jellyfin.data.auth.AuthRepository
import com.eygraber.jellyfin.data.server.ServerRepository
import com.eygraber.jellyfin.domain.session.SessionManager
import com.eygraber.jellyfin.domain.session.SessionState
import com.eygraber.jellyfin.services.logging.JellyfinLogger
import com.eygraber.jellyfin.services.sdk.JellyfinAuthService
import com.eygraber.jellyfin.services.sdk.JellyfinSessionManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Default implementation of [SessionManager].
 *
 * Coordinates between the auth repository (local persistence),
 * the server repository (server info), and the SDK session manager
 * (in-memory SDK state) to manage the app's session lifecycle.
 *
 * This is scoped as a singleton because it holds session state
 * that must be consistent across the entire application.
 */
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultSessionManager(
  private val authRepository: AuthRepository,
  private val serverRepository: ServerRepository,
  private val authService: JellyfinAuthService,
  private val sdkSessionManager: JellyfinSessionManager,
  private val logger: JellyfinLogger,
) : SessionManager {
  private val _sessionState = MutableStateFlow<SessionState>(SessionState.Loading)
  override val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

  @Suppress("ReturnCount")
  override suspend fun restoreSession(): SessionState {
    logger.debug(tag = TAG, message = "Restoring session")

    val activeResult = authRepository.getActiveSession()
    if(!activeResult.isSuccess()) {
      logger.info(tag = TAG, message = "No active session found")
      _sessionState.value = SessionState.NoSession
      return SessionState.NoSession
    }

    val session = activeResult.value
      ?: run {
        logger.info(tag = TAG, message = "No active session in database")
        _sessionState.value = SessionState.NoSession
        return SessionState.NoSession
      }

    // Get the server URL for this session
    val serverResult = serverRepository.getServerById(session.serverId)
    if(!serverResult.isSuccess()) {
      logger.warn(
        tag = TAG,
        message = "Server not found for session: ${session.serverId}",
      )
      _sessionState.value = SessionState.NoSession
      return SessionState.NoSession
    }

    val server = serverResult.value

    // Configure the SDK session manager with the stored credentials
    sdkSessionManager.setServer(
      serverUrl = server.url,
      serverId = server.id,
      serverName = server.name,
    )
    sdkSessionManager.setAuthentication(
      accessToken = session.accessToken,
      userId = session.userId,
    )

    // Validate the token by making an API call
    val userResult = authService.getCurrentUser()
    if(userResult.isSuccess()) {
      logger.info(tag = TAG, message = "Session restored successfully for user: ${session.username}")
      val state = SessionState.Authenticated(session = session)
      _sessionState.value = state
      return state
    }

    // Token is invalid/expired
    logger.warn(tag = TAG, message = "Session token invalid for user: ${session.username}")
    sdkSessionManager.clearAuthentication()
    val state = SessionState.SessionExpired(session = session)
    _sessionState.value = state
    return state
  }

  override suspend fun validateSession(): Boolean {
    val current = _sessionState.value
    if(current !is SessionState.Authenticated) {
      return false
    }

    val isValid = authService.getCurrentUser().isSuccess()
    if(!isValid) {
      logger.warn(tag = TAG, message = "Session validation failed")
      sdkSessionManager.clearAuthentication()
      _sessionState.value = SessionState.SessionExpired(session = current.session)
    }

    return isValid
  }

  override suspend fun onLoginSuccess(
    serverId: String,
    serverUrl: String,
    accessToken: String,
    userId: String,
  ) {
    logger.info(tag = TAG, message = "Login success, updating session state")

    // SDK session manager is already configured by the auth service during login
    // Just update our session state from the database
    val session = authRepository.getActiveSession().successOrNull
    if(session != null) {
      _sessionState.value = SessionState.Authenticated(session = session)
    }
  }

  override suspend fun logout(): JellyfinResult<Unit> {
    logger.info(tag = TAG, message = "Logging out")

    val result = authRepository.logout()
    sdkSessionManager.clearSession()
    _sessionState.value = SessionState.NoSession

    return result
  }

  override suspend fun switchSession(sessionId: String): JellyfinResult<Unit> {
    logger.info(tag = TAG, message = "Switching to session: $sessionId")

    val setResult = authRepository.setActiveSession(sessionId)
    if(!setResult.isSuccess()) {
      return setResult
    }

    // Restore the newly active session
    restoreSession()
    return JellyfinResult.Success(Unit)
  }

  companion object {
    private const val TAG = "SessionManager"
  }
}
