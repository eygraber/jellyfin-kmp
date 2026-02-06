package com.eygraber.jellyfin.data.server

import com.eygraber.jellyfin.common.JellyfinResult
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing saved server configurations.
 *
 * Provides CRUD operations and reactive queries for server persistence.
 * All write operations return [JellyfinResult] to handle potential database errors.
 */
interface ServerRepository {
  /**
   * Observes all saved servers, sorted by most recently used.
   */
  fun observeServers(): Flow<List<ServerEntity>>

  /**
   * Gets all saved servers, sorted by most recently used.
   */
  suspend fun getServers(): JellyfinResult<List<ServerEntity>>

  /**
   * Gets a server by its unique ID.
   *
   * @param serverId The server's unique identifier.
   * @return The server entity, or an error if not found.
   */
  suspend fun getServerById(serverId: String): JellyfinResult<ServerEntity>

  /**
   * Gets a server by its URL.
   *
   * @param url The server URL.
   * @return The server entity, or an error if not found.
   */
  suspend fun getServerByUrl(url: String): JellyfinResult<ServerEntity>

  /**
   * Saves a server configuration. If a server with the same ID exists,
   * it will be updated (upsert behavior).
   *
   * @param server The server entity to save.
   */
  suspend fun saveServer(server: ServerEntity): JellyfinResult<Unit>

  /**
   * Updates the last used timestamp for a server.
   *
   * @param serverId The server's unique identifier.
   * @param timestamp The new last-used timestamp (epoch millis).
   */
  suspend fun updateLastUsed(
    serverId: String,
    timestamp: Long,
  ): JellyfinResult<Unit>

  /**
   * Deletes a server and all associated user sessions.
   *
   * @param serverId The server's unique identifier.
   */
  suspend fun deleteServer(serverId: String): JellyfinResult<Unit>

  /**
   * Deletes all saved servers and their associated sessions.
   */
  suspend fun deleteAllServers(): JellyfinResult<Unit>
}
