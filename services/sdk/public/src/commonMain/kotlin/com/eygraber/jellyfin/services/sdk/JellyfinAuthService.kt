package com.eygraber.jellyfin.services.sdk

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.sdk.core.model.AuthenticationResult
import com.eygraber.jellyfin.sdk.core.model.QuickConnectResult
import com.eygraber.jellyfin.sdk.core.model.UserDto

/**
 * Service for authentication and user session management.
 *
 * Provides operations to authenticate users, manage sessions,
 * and interact with user data on the Jellyfin server.
 */
interface JellyfinAuthService {
  /**
   * Authenticates a user by username and password.
   *
   * On success, the access token is stored for subsequent authenticated requests.
   *
   * @param serverUrl The URL of the Jellyfin server.
   * @param username The username.
   * @param password The password.
   * @return A [JellyfinResult] containing the [AuthenticationResult].
   */
  suspend fun authenticateByName(
    serverUrl: String,
    username: String,
    password: String,
  ): JellyfinResult<AuthenticationResult>

  /**
   * Gets the currently authenticated user.
   * Requires an active authenticated session.
   *
   * @return A [JellyfinResult] containing the [UserDto].
   */
  suspend fun getCurrentUser(): JellyfinResult<UserDto>

  /**
   * Gets all public users visible on the server.
   * Does not require authentication.
   *
   * @param serverUrl The URL of the Jellyfin server.
   * @return A [JellyfinResult] containing a list of [UserDto].
   */
  suspend fun getPublicUsers(serverUrl: String): JellyfinResult<List<UserDto>>

  /**
   * Initiates Quick Connect on the specified server.
   *
   * Returns a [QuickConnectResult] containing a code to display to the user
   * and a secret for polling approval status.
   *
   * @param serverUrl The URL of the Jellyfin server.
   * @return A [JellyfinResult] containing the [QuickConnectResult].
   */
  suspend fun initiateQuickConnect(
    serverUrl: String,
  ): JellyfinResult<QuickConnectResult>

  /**
   * Checks the status of a Quick Connect request.
   *
   * @param serverUrl The URL of the Jellyfin server.
   * @param secret The secret from [initiateQuickConnect].
   * @return A [JellyfinResult] containing the [QuickConnectResult] with updated status.
   */
  suspend fun getQuickConnectStatus(
    serverUrl: String,
    secret: String,
  ): JellyfinResult<QuickConnectResult>

  /**
   * Logs out the current session.
   *
   * Clears the stored access token and notifies the server.
   *
   * @return A [JellyfinResult] indicating success or failure.
   */
  suspend fun logout(): JellyfinResult<Unit>
}
