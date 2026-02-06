package com.eygraber.jellyfin.services.sdk

import com.eygraber.jellyfin.sdk.core.ServerInfo
import kotlinx.coroutines.flow.StateFlow

/**
 * Manages the current server connection and authentication session.
 *
 * Provides observable state for the current server and authentication status,
 * and methods to update the session configuration.
 */
interface JellyfinSessionManager {
  /**
   * The current server info, or null if not connected to any server.
   */
  val currentServer: StateFlow<ServerInfo?>

  /**
   * Whether the user is currently authenticated with a valid access token.
   */
  val authenticated: StateFlow<Boolean>

  /**
   * Updates the current server connection.
   *
   * @param serverUrl The URL of the server to connect to.
   * @param serverId The server's unique ID (from [PublicSystemInfo]).
   * @param serverName The server's display name.
   */
  fun setServer(
    serverUrl: String,
    serverId: String? = null,
    serverName: String? = null,
  )

  /**
   * Sets the access token and user ID for the current session.
   * Called after successful authentication.
   *
   * @param accessToken The access token from authentication.
   * @param userId The authenticated user's ID.
   */
  fun setAuthentication(
    accessToken: String,
    userId: String,
  )

  /**
   * Clears the current authentication, removing the access token and user ID.
   */
  fun clearAuthentication()

  /**
   * Clears the entire session, including server connection and authentication.
   */
  fun clearSession()
}
