package com.eygraber.jellyfin.data.server.impl

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.eygraber.jellyfin.data.server.ServerEntity
import com.eygraber.jellyfin.services.database.impl.JellyfinDatabase
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Local data source for server persistence using SQLDelight.
 *
 * Handles all direct database interactions for server configurations.
 * This class is not a singleton; connection pooling is handled by the database driver.
 */
@Inject
class ServerLocalDataSource(
  private val database: JellyfinDatabase,
  private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
  fun observeAll(): Flow<List<ServerEntity>> =
    database.serverQueries.selectAll()
      .asFlow()
      .mapToList(dispatcher)
      .map { servers -> servers.map { it.toEntity() } }

  suspend fun getAll(): List<ServerEntity> = withContext(dispatcher) {
    database.serverQueries.selectAll().executeAsList().map { it.toEntity() }
  }

  suspend fun getById(serverId: String): ServerEntity? = withContext(dispatcher) {
    database.serverQueries.selectById(id = serverId).executeAsOneOrNull()?.toEntity()
  }

  suspend fun getByUrl(url: String): ServerEntity? = withContext(dispatcher) {
    database.serverQueries.selectByUrl(url = url).executeAsOneOrNull()?.toEntity()
  }

  suspend fun upsert(server: ServerEntity): Unit = withContext(dispatcher) {
    database.serverQueries.insert(
      id = server.id,
      name = server.name,
      url = server.url,
      version = server.version,
      created_at = server.createdAt,
      last_used_at = server.lastUsedAt,
    )
  }

  suspend fun updateLastUsed(
    serverId: String,
    timestamp: Long,
  ): Unit = withContext(dispatcher) {
    database.serverQueries.updateLastUsed(
      last_used_at = timestamp,
      id = serverId,
    )
  }

  suspend fun delete(serverId: String): Unit = withContext(dispatcher) {
    database.serverQueries.delete(id = serverId)
  }

  suspend fun deleteAll(): Unit = withContext(dispatcher) {
    database.serverQueries.deleteAll()
  }
}

/**
 * Maps a SQLDelight-generated `Server` row to a [ServerEntity].
 */
private fun migrations.Server.toEntity() = ServerEntity(
  id = id,
  name = name,
  url = url,
  version = version,
  createdAt = created_at,
  lastUsedAt = last_used_at,
)
