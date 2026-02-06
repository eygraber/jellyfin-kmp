package com.eygraber.jellyfin.domain.session

import com.eygraber.jellyfin.common.JellyfinResult
import kotlinx.coroutines.flow.StateFlow

/**
 * Domain-level session manager that coordinates session lifecycle.
 *
 * Handles session restoration on app launch, token validation,
 * and session state transitions. This is the single source of truth
 * for whether the user is authenticated.
 */
interface SessionManager {
  /**
   * The current session state, observable by the UI layer.
   */
  val sessionState: StateFlow<SessionState>

  /**
   * Restores the session from persistent storage.
   *
   * Should be called on app launch. Validates the stored session
   * against the server and updates [sessionState] accordingly.
   *
   * @return The resulting [SessionState] after restoration attempt.
   */
  suspend fun restoreSession(): SessionState

  /**
   * Validates the current session by checking the token against the server.
   *
   * If validation fails, transitions to [SessionState.SessionExpired].
   *
   * @return `true` if the session is still valid, `false` otherwise.
   */
  suspend fun validateSession(): Boolean

  /**
   * Called after a successful login to update the session state.
   *
   * @param serverId The server's unique ID.
   * @param serverUrl The server's URL.
   * @param accessToken The access token from authentication.
   * @param userId The authenticated user's ID.
   */
  suspend fun onLoginSuccess(
    serverId: String,
    serverUrl: String,
    accessToken: String,
    userId: String,
  )

  /**
   * Logs out the current session, clearing all stored credentials.
   *
   * Transitions to [SessionState.NoSession].
   */
  suspend fun logout(): JellyfinResult<Unit>

  /**
   * Switches to a different session by its ID.
   *
   * @param sessionId The session to switch to.
   */
  suspend fun switchSession(sessionId: String): JellyfinResult<Unit>
}
