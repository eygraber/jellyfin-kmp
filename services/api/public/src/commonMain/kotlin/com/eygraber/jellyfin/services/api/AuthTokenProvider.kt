package com.eygraber.jellyfin.services.api

/**
 * Provides authentication tokens for API requests.
 *
 * Implementations should return the current access token, or null
 * if the user is not authenticated.
 *
 * This is the auth interceptor placeholder -- actual auth implementation
 * will come in a later epic.
 */
interface AuthTokenProvider {
  /**
   * Returns the current access token, or null if not authenticated.
   */
  suspend fun getAccessToken(): String?
}
