package com.eygraber.jellyfin.data.server.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.common.runResult
import com.eygraber.jellyfin.data.server.ServerEntity
import com.eygraber.jellyfin.data.server.ServerRepository
import com.eygraber.jellyfin.services.logging.JellyfinLogger
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.Flow

/**
 * SQLDelight-backed implementation of [ServerRepository].
 *
 * Delegates to [ServerLocalDataSource] for all database interactions.
 * The repository is stateless and not scoped as a singleton.
 */
@ContributesBinding(AppScope::class)
class SqlDelightServerRepository(
  private val localDataSource: ServerLocalDataSource,
  private val logger: JellyfinLogger,
) : ServerRepository {
  override fun observeServers(): Flow<List<ServerEntity>> =
    localDataSource.observeAll()

  override suspend fun getServers(): JellyfinResult<List<ServerEntity>> = runResult {
    localDataSource.getAll()
  }

  override suspend fun getServerById(serverId: String): JellyfinResult<ServerEntity> = runResult {
    localDataSource.getById(serverId)
      ?: error("Server not found: $serverId")
  }

  override suspend fun getServerByUrl(url: String): JellyfinResult<ServerEntity> = runResult {
    localDataSource.getByUrl(url)
      ?: error("Server not found for URL: $url")
  }

  override suspend fun saveServer(server: ServerEntity): JellyfinResult<Unit> = runResult {
    logger.debug(tag = TAG, message = "Saving server: ${server.name} (${server.url})")
    localDataSource.upsert(server)
  }

  override suspend fun updateLastUsed(
    serverId: String,
    timestamp: Long,
  ): JellyfinResult<Unit> = runResult {
    logger.debug(tag = TAG, message = "Updating last used for server: $serverId")
    localDataSource.updateLastUsed(serverId = serverId, timestamp = timestamp)
  }

  override suspend fun deleteServer(serverId: String): JellyfinResult<Unit> = runResult {
    logger.info(tag = TAG, message = "Deleting server: $serverId")
    localDataSource.delete(serverId)
  }

  override suspend fun deleteAllServers(): JellyfinResult<Unit> = runResult {
    logger.info(tag = TAG, message = "Deleting all servers")
    localDataSource.deleteAll()
  }

  companion object {
    private const val TAG = "ServerRepository"
  }
}
