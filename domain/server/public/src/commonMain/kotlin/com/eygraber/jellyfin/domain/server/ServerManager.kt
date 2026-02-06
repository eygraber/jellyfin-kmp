package com.eygraber.jellyfin.domain.server

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.data.server.ServerEntity
import kotlinx.coroutines.flow.Flow

/**
 * Domain-level manager for multi-server operations.
 *
 * Coordinates between the server repository, auth repository,
 * and session manager to provide server lifecycle management
 * including adding, removing, and switching between servers.
 */
interface ServerManager {
  /**
   * Observes all saved servers with their current connection status.
   *
   * The returned list includes whether each server is the active server
   * and the number of saved user sessions per server.
   */
  fun observeServers(): Flow<List<ServerWithStatus>>

  /**
   * Adds a new server by URL. Validates the URL, connects to the server
   * to retrieve its info, and persists it to the database.
   *
   * If a server with the same ID already exists, its URL and info are updated.
   *
   * @param serverUrl The URL of the server to add.
   * @return The saved [ServerEntity] on success.
   */
  suspend fun addServer(serverUrl: String): JellyfinResult<ServerEntity>

  /**
   * Removes a server and all associated user sessions.
   *
   * If the removed server was the active server, the session state
   * will be cleared.
   *
   * @param serverId The server's unique identifier.
   */
  suspend fun removeServer(serverId: String): JellyfinResult<Unit>

  /**
   * Checks the connection status of a specific server.
   *
   * @param serverId The server's unique identifier.
   * @return The connection status of the server.
   */
  suspend fun checkServerConnection(serverId: String): ServerConnectionStatus

  /**
   * Checks the connection status of all saved servers.
   * Updates are emitted via [observeServers].
   */
  suspend fun refreshConnectionStatuses()
}
