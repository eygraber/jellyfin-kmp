package com.eygraber.jellyfin.data.auth.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.sdk.core.model.AuthenticationResult
import com.eygraber.jellyfin.sdk.core.model.QuickConnectResult
import com.eygraber.jellyfin.sdk.core.model.UserDto
import com.eygraber.jellyfin.services.sdk.JellyfinAuthService
import dev.zacsweers.metro.Inject

/**
 * Remote data source for authentication operations.
 *
 * Delegates to [JellyfinAuthService] for all network interactions.
 * This class is stateless and not a singleton.
 */
@Inject
class AuthRemoteDataSource(
  private val authService: JellyfinAuthService,
) {
  suspend fun authenticateByName(
    serverUrl: String,
    username: String,
    password: String,
  ): JellyfinResult<AuthenticationResult> =
    authService.authenticateByName(
      serverUrl = serverUrl,
      username = username,
      password = password,
    )

  suspend fun getPublicUsers(serverUrl: String): JellyfinResult<List<UserDto>> =
    authService.getPublicUsers(serverUrl = serverUrl)

  suspend fun initiateQuickConnect(
    serverUrl: String,
  ): JellyfinResult<QuickConnectResult> =
    authService.initiateQuickConnect(serverUrl = serverUrl)

  suspend fun getQuickConnectStatus(
    serverUrl: String,
    secret: String,
  ): JellyfinResult<QuickConnectResult> =
    authService.getQuickConnectStatus(serverUrl = serverUrl, secret = secret)

  suspend fun logout(): JellyfinResult<Unit> =
    authService.logout()
}
