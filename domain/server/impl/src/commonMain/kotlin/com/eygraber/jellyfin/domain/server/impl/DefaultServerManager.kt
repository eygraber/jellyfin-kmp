package com.eygraber.jellyfin.domain.server.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.common.doOnSuccess
import com.eygraber.jellyfin.common.flatMapSuccessTo
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.common.mapSuccessTo
import com.eygraber.jellyfin.data.auth.AuthRepository
import com.eygraber.jellyfin.data.server.ServerEntity
import com.eygraber.jellyfin.data.server.ServerRepository
import com.eygraber.jellyfin.domain.server.ServerConnectionStatus
import com.eygraber.jellyfin.domain.server.ServerManager
import com.eygraber.jellyfin.domain.server.ServerWithStatus
import com.eygraber.jellyfin.services.logging.JellyfinLogger
import com.eygraber.jellyfin.services.sdk.JellyfinServerService
import com.eygraber.jellyfin.services.sdk.JellyfinSessionManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

/**
 * Default implementation of [ServerManager].
 *
 * Coordinates between the server repository (persistence), auth repository
 * (session counts), server service (connectivity), and SDK session manager
 * (active server tracking) to manage multi-server operations.
 *
 * This is scoped as a singleton because it maintains connection status
 * state that should be consistent across the application.
 */
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultServerManager(
  private val serverRepository: ServerRepository,
  private val authRepository: AuthRepository,
  private val serverService: JellyfinServerService,
  private val sdkSessionManager: JellyfinSessionManager,
  private val logger: JellyfinLogger,
) : ServerManager {
  /**
   * In-memory cache of connection statuses per server ID.
   */
  private val connectionStatuses = MutableStateFlow<Map<String, ServerConnectionStatus>>(emptyMap())

  override fun observeServers(): Flow<List<ServerWithStatus>> =
    combine(
      serverRepository.observeServers(),
      connectionStatuses,
      authRepository.observeActiveSession(),
    ) { servers, statuses, activeSession ->
      val activeServerId = activeSession?.serverId
      servers.map { server ->
        ServerWithStatus(
          server = server,
          connectionStatus = statuses[server.id] ?: ServerConnectionStatus.Unknown,
          isActive = server.id == activeServerId,
          userCount = authRepository.getSessionsForServer(server.id)
            .let { result ->
              if(result.isSuccess()) result.value.size else 0
            },
        )
      }
    }

  @Suppress("ReturnCount")
  override suspend fun addServer(serverUrl: String): JellyfinResult<ServerEntity> {
    logger.info(tag = TAG, message = "Adding server: $serverUrl")

    // Connect to the server to validate and get its info
    val connectResult = serverService.connectToServer(serverUrl)
    if(!connectResult.isSuccess()) {
      logger.warn(tag = TAG, message = "Failed to connect to server: $serverUrl")
      return connectResult.mapSuccessTo { error("Unreachable") }
    }

    val systemInfo = connectResult.value
    val serverId = systemInfo.id
    if(serverId.isNullOrBlank()) {
      logger.warn(tag = TAG, message = "Server did not return an ID: $serverUrl")
      return JellyfinResult.Error(
        message = "Server did not return a valid ID",
        isEphemeral = false,
      )
    }

    val now = currentTimeMillis()
    val server = ServerEntity(
      id = serverId,
      name = systemInfo.serverName ?: serverUrl,
      url = serverUrl,
      version = systemInfo.version,
      createdAt = now,
      lastUsedAt = now,
    )

    return serverRepository.saveServer(server).mapSuccessTo { server }
  }

  override suspend fun removeServer(serverId: String): JellyfinResult<Unit> {
    logger.info(tag = TAG, message = "Removing server: $serverId")

    // Check if this is the active server before removing
    val activeSession = authRepository.getActiveSession().let { result ->
      if(result.isSuccess()) result.value else null
    }
    val isActiveServer = activeSession?.serverId == serverId

    // Remove all sessions for this server first, then delete the server
    return authRepository.logoutServer(serverId)
      .flatMapSuccessTo { serverRepository.deleteServer(serverId) }
      .doOnSuccess {
        // Clear connection status cache for this server
        connectionStatuses.value = connectionStatuses.value - serverId

        // If the removed server was the active server, clear the SDK session
        if(isActiveServer) {
          sdkSessionManager.clearSession()
        }
      }
  }

  override suspend fun checkServerConnection(serverId: String): ServerConnectionStatus {
    val serverResult = serverRepository.getServerById(serverId)
    if(!serverResult.isSuccess()) {
      return ServerConnectionStatus.Unknown
    }

    updateConnectionStatus(serverId, ServerConnectionStatus.Checking)

    val status = checkConnection(serverResult.value.url)
    updateConnectionStatus(serverId, status)

    return status
  }

  override suspend fun refreshConnectionStatuses() {
    logger.debug(tag = TAG, message = "Refreshing connection statuses for all servers")

    val serversResult = serverRepository.getServers()
    if(!serversResult.isSuccess()) return

    for(server in serversResult.value) {
      updateConnectionStatus(server.id, ServerConnectionStatus.Checking)
    }

    for(server in serversResult.value) {
      val status = checkConnection(server.url)
      updateConnectionStatus(server.id, status)
    }
  }

  private suspend fun checkConnection(serverUrl: String): ServerConnectionStatus =
    if(serverService.connectToServer(serverUrl).isSuccess()) {
      ServerConnectionStatus.Online
    }
    else {
      ServerConnectionStatus.Offline
    }

  private fun updateConnectionStatus(serverId: String, status: ServerConnectionStatus) {
    connectionStatuses.value = connectionStatuses.value + (serverId to status)
  }

  companion object {
    private const val TAG = "ServerManager"
  }
}
