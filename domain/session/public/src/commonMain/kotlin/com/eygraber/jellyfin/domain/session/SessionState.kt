package com.eygraber.jellyfin.domain.session

import com.eygraber.jellyfin.data.auth.UserSessionEntity

/**
 * Represents the current session state of the application.
 */
sealed interface SessionState {
  /**
   * The session state is being determined (e.g. during app launch).
   */
  data object Loading : SessionState

  /**
   * No active session exists. The user needs to authenticate.
   */
  data object NoSession : SessionState

  /**
   * An active session exists and has been validated.
   */
  data class Authenticated(
    val session: UserSessionEntity,
  ) : SessionState

  /**
   * A session exists but the token is expired or invalid.
   * The user needs to re-authenticate.
   */
  data class SessionExpired(
    val session: UserSessionEntity,
  ) : SessionState
}
