package com.eygraber.jellyfin.services.sdk.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.sdk.core.JellyfinSdk
import com.eygraber.jellyfin.sdk.core.ServerInfo
import com.eygraber.jellyfin.sdk.core.api.user.userApi
import com.eygraber.jellyfin.sdk.core.model.AuthenticationResult
import com.eygraber.jellyfin.sdk.core.model.QuickConnectResult
import com.eygraber.jellyfin.sdk.core.model.UserDto
import com.eygraber.jellyfin.services.logging.JellyfinLogger
import com.eygraber.jellyfin.services.sdk.JellyfinAuthService
import com.eygraber.jellyfin.services.sdk.JellyfinSessionManager
import com.eygraber.jellyfin.services.sdk.toJellyfinResult
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

/**
 * Default implementation of [JellyfinAuthService].
 *
 * Uses the [JellyfinSdk] to authenticate users and manage sessions.
 * On successful authentication, stores the access token and user ID
 * in the [JellyfinSessionManager] for subsequent authenticated requests.
 */
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultJellyfinAuthService(
  private val sdk: JellyfinSdk,
  private val sessionManager: JellyfinSessionManager,
  private val logger: JellyfinLogger,
) : JellyfinAuthService {
  override suspend fun authenticateByName(
    serverUrl: String,
    username: String,
    password: String,
  ): JellyfinResult<AuthenticationResult> {
    logger.info(tag = TAG, message = "Authenticating user: $username on $serverUrl")

    val apiClient = sdk.createApiClient(
      serverInfo = ServerInfo(baseUrl = serverUrl),
    )

    return try {
      val result = apiClient.userApi
        .authenticateByName(username = username, password = password)
        .toJellyfinResult()

      if(result.isSuccess()) {
        val authResult = result.value
        val accessToken = authResult.accessToken
        val userId = authResult.user?.id

        if(accessToken != null && userId != null) {
          sessionManager.setServer(serverUrl = serverUrl)
          sessionManager.setAuthentication(
            accessToken = accessToken,
            userId = userId,
          )
          logger.info(tag = TAG, message = "Authentication successful for user: $username")
        }
        else {
          logger.warn(
            tag = TAG,
            message = "Authentication response missing token or userId",
          )
        }
      }

      result
    }
    finally {
      apiClient.close()
    }
  }

  override suspend fun getCurrentUser(): JellyfinResult<UserDto> {
    val serverInfo = sessionManager.currentServer.value
      ?: return JellyfinResult.Error(
        message = "Not connected to a server",
        isEphemeral = false,
      )

    val apiClient = sdk.createApiClient(serverInfo = serverInfo)
    return try {
      apiClient.userApi.getCurrentUser().toJellyfinResult()
    }
    finally {
      apiClient.close()
    }
  }

  override suspend fun getPublicUsers(serverUrl: String): JellyfinResult<List<UserDto>> {
    val apiClient = sdk.createApiClient(
      serverInfo = ServerInfo(baseUrl = serverUrl),
    )

    return try {
      apiClient.userApi.getPublicUsers().toJellyfinResult()
    }
    finally {
      apiClient.close()
    }
  }

  override suspend fun initiateQuickConnect(
    serverUrl: String,
  ): JellyfinResult<QuickConnectResult> {
    logger.info(tag = TAG, message = "Initiating Quick Connect on $serverUrl")

    val apiClient = sdk.createApiClient(
      serverInfo = ServerInfo(baseUrl = serverUrl),
    )

    return try {
      apiClient.userApi.initiateQuickConnect().toJellyfinResult()
    }
    finally {
      apiClient.close()
    }
  }

  override suspend fun getQuickConnectStatus(
    serverUrl: String,
    secret: String,
  ): JellyfinResult<QuickConnectResult> {
    val apiClient = sdk.createApiClient(
      serverInfo = ServerInfo(baseUrl = serverUrl),
    )

    return try {
      apiClient.userApi.getQuickConnectStatus(secret = secret).toJellyfinResult()
    }
    finally {
      apiClient.close()
    }
  }

  override suspend fun logout(): JellyfinResult<Unit> {
    val serverInfo = sessionManager.currentServer.value
      ?: return JellyfinResult.Error(
        message = "Not connected to a server",
        isEphemeral = false,
      )

    logger.info(tag = TAG, message = "Logging out")

    val apiClient = sdk.createApiClient(serverInfo = serverInfo)
    return try {
      val result = apiClient.userApi.logout().toJellyfinResult()

      // Clear auth regardless of server response
      sessionManager.clearAuthentication()
      logger.info(tag = TAG, message = "Logout complete")

      result
    }
    finally {
      apiClient.close()
    }
  }

  companion object {
    private const val TAG = "JellyfinAuthService"
  }
}
