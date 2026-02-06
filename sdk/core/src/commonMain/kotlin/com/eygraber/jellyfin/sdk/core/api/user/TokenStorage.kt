package com.eygraber.jellyfin.sdk.core.api.user

/**
 * Abstraction for securely storing authentication tokens.
 * Platform implementations should use secure storage mechanisms
 * (e.g., Android Keystore, iOS Keychain, etc.).
 */
interface TokenStorage {
  /**
   * Stores the access token for the given server.
   */
  suspend fun storeToken(serverId: String, userId: String, accessToken: String)

  /**
   * Retrieves the access token for the given server and user.
   * Returns null if no token is stored.
   */
  suspend fun getToken(serverId: String, userId: String): String?

  /**
   * Removes the access token for the given server and user.
   */
  suspend fun removeToken(serverId: String, userId: String)

  /**
   * Removes all stored tokens.
   */
  suspend fun clearAll()
}
