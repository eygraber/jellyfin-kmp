package com.eygraber.jellyfin.data.auth

import com.eygraber.jellyfin.common.JellyfinResult
import kotlinx.coroutines.flow.Flow

/**
 * Repository for authentication and user session management.
 *
 * Coordinates between remote authentication (SDK) and local session
 * persistence (SQLDelight). Manages the lifecycle of user sessions
 * including login, logout, and session restoration.
 */
interface AuthRepository {
  /**
   * Observes the currently active session, emitting null when no session is active.
   */
  fun observeActiveSession(): Flow<UserSessionEntity?>

  /**
   * Observes all sessions for a specific server.
   */
  fun observeSessionsForServer(serverId: String): Flow<List<UserSessionEntity>>

  /**
   * Gets the currently active session, or null if none exists.
   */
  suspend fun getActiveSession(): JellyfinResult<UserSessionEntity?>

  /**
   * Gets all sessions for a specific server.
   */
  suspend fun getSessionsForServer(serverId: String): JellyfinResult<List<UserSessionEntity>>

  /**
   * Authenticates a user with username and password.
   *
   * On success, creates a new session, persists it locally,
   * and sets it as the active session.
   *
   * @param serverId The server's unique ID.
   * @param serverUrl The server's URL.
   * @param username The username to authenticate with.
   * @param password The password to authenticate with.
   * @return The created [UserSessionEntity] on success.
   */
  suspend fun login(
    serverId: String,
    serverUrl: String,
    username: String,
    password: String,
  ): JellyfinResult<UserSessionEntity>

  /**
   * Initiates Quick Connect authentication.
   *
   * Returns a code to display to the user. The user must authorize this
   * code on an already-authenticated device.
   *
   * @param serverUrl The server's URL.
   * @return The Quick Connect code to display.
   */
  suspend fun initiateQuickConnect(
    serverUrl: String,
  ): JellyfinResult<QuickConnectState>

  /**
   * Checks the status of a Quick Connect request and completes authentication
   * if the request has been approved.
   *
   * @param serverId The server's unique ID.
   * @param serverUrl The server's URL.
   * @param secret The secret from [initiateQuickConnect].
   * @return The created [UserSessionEntity] if approved, or updated [QuickConnectState] if still pending.
   */
  suspend fun checkQuickConnect(
    serverId: String,
    serverUrl: String,
    secret: String,
  ): JellyfinResult<QuickConnectResult>

  /**
   * Sets a session as the active session, deactivating any currently active session.
   */
  suspend fun setActiveSession(sessionId: String): JellyfinResult<Unit>

  /**
   * Logs out the active session.
   *
   * Notifies the server and removes the session from local storage.
   */
  suspend fun logout(): JellyfinResult<Unit>

  /**
   * Logs out a specific session by ID.
   *
   * Removes the session from local storage.
   */
  suspend fun logoutSession(sessionId: String): JellyfinResult<Unit>

  /**
   * Removes all sessions for a specific server.
   */
  suspend fun logoutServer(serverId: String): JellyfinResult<Unit>
}
