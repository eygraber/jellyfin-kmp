package com.eygraber.jellyfin.services.sdk.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.sdk.core.JellyfinSdk
import com.eygraber.jellyfin.sdk.core.ServerInfo
import com.eygraber.jellyfin.sdk.core.api.system.systemApi
import com.eygraber.jellyfin.sdk.core.model.PublicSystemInfo
import com.eygraber.jellyfin.sdk.core.model.ServerDiscoveryInfo
import com.eygraber.jellyfin.sdk.core.model.SystemInfo
import com.eygraber.jellyfin.services.logging.JellyfinLogger
import com.eygraber.jellyfin.services.sdk.JellyfinServerService
import com.eygraber.jellyfin.services.sdk.JellyfinSessionManager
import com.eygraber.jellyfin.services.sdk.toJellyfinResult
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * Default implementation of [JellyfinServerService].
 *
 * Uses the [JellyfinSdk] to communicate with Jellyfin servers.
 * Server discovery is a placeholder that will be implemented with
 * platform-specific UDP broadcast in a future epic.
 */
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultJellyfinServerService(
  private val sdk: JellyfinSdk,
  private val sessionManager: JellyfinSessionManager,
  private val logger: JellyfinLogger,
) : JellyfinServerService {
  override fun discoverServers(timeoutMs: Long): Flow<ServerDiscoveryInfo> {
    logger.debug(tag = TAG, message = "Server discovery not yet implemented")
    // Server discovery requires platform-specific UDP broadcast implementation.
    // This will be added in a future epic with ServerDiscovery interface implementations.
    return emptyFlow()
  }

  override suspend fun connectToServer(serverUrl: String): JellyfinResult<PublicSystemInfo> {
    logger.info(tag = TAG, message = "Connecting to server: $serverUrl")

    val apiClient = sdk.createApiClient(
      serverInfo = ServerInfo(baseUrl = serverUrl),
    )

    return try {
      apiClient.systemApi.getPublicSystemInfo()
        .toJellyfinResult()
        .also { result ->
          if(result is JellyfinResult.Success) {
            val info = result.value
            sessionManager.setServer(
              serverUrl = serverUrl,
              serverId = info.id,
              serverName = info.serverName,
            )
            logger.info(
              tag = TAG,
              message = "Connected to server: ${info.serverName.orEmpty()} (${info.version.orEmpty()})",
            )
          }
        }
    }
    finally {
      apiClient.close()
    }
  }

  override suspend fun getSystemInfo(): JellyfinResult<SystemInfo> {
    val serverInfo = sessionManager.currentServer.value
      ?: return JellyfinResult.Error(
        message = "Not connected to a server",
        isEphemeral = false,
      )

    val apiClient = sdk.createApiClient(serverInfo = serverInfo)
    return try {
      apiClient.systemApi.getSystemInfo().toJellyfinResult()
    }
    finally {
      apiClient.close()
    }
  }

  override suspend fun ping(): JellyfinResult<String> {
    val serverInfo = sessionManager.currentServer.value
      ?: return JellyfinResult.Error(
        message = "Not connected to a server",
        isEphemeral = false,
      )

    val apiClient = sdk.createApiClient(serverInfo = serverInfo)
    return try {
      apiClient.systemApi.ping().toJellyfinResult()
    }
    finally {
      apiClient.close()
    }
  }

  companion object {
    private const val TAG = "JellyfinServerService"
  }
}
