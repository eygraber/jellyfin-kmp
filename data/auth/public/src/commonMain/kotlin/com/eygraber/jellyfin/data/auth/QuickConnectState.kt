package com.eygraber.jellyfin.data.auth

/**
 * State of a Quick Connect authentication request.
 *
 * @property code The code to display to the user for authorization.
 * @property secret The secret used to poll for approval status.
 */
data class QuickConnectState(
  val code: String,
  val secret: String,
)

/**
 * Result of checking a Quick Connect request.
 */
sealed interface QuickConnectResult {
  /**
   * The Quick Connect request has been approved and the session is created.
   */
  data class Authenticated(val session: UserSessionEntity) : QuickConnectResult

  /**
   * The Quick Connect request is still waiting for approval.
   */
  data object Pending : QuickConnectResult
}
