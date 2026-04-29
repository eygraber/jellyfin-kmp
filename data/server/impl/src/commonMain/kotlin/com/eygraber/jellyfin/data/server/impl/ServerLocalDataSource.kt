package com.eygraber.jellyfin.data.server.impl

import com.eygraber.jellyfin.data.server.ServerEntity
import com.eygraber.jellyfin.services.database.impl.JellyfinDatabase
import com.eygraber.jellyfin.services.database.impl.asFlow
import com.eygraber.jellyfin.services.database.impl.awaitAsList
import com.eygraber.jellyfin.services.database.impl.awaitAsOneOrNull
import com.eygraber.jellyfin.services.database.impl.mapToList
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import migrations.Server

/**
 * Local data source for server persistence using SQLDelight.
 *
 * Handles all direct database interactions for server configurations.
 * This class is not a singleton; connection pooling is handled by the database driver.
 */
@Inject
class ServerLocalDataSource(
  private val database: JellyfinDatabase,
) {
  fun observeAll(): Flow<List<ServerEntity>> =
    database
      .serverQueries
      .selectAll()
      .asFlow()
      .mapToList()
      .map { servers -> servers.map { it.toEntity() } }

  suspend fun getAll(): List<ServerEntity> =
    database.serverQueries.selectAll().awaitAsList().map { it.toEntity() }

  suspend fun getById(serverId: String): ServerEntity? =
    database.serverQueries.selectById(id = serverId).awaitAsOneOrNull()?.toEntity()

  suspend fun getByUrl(url: String): ServerEntity? =
    database.serverQueries.selectByUrl(url = url).awaitAsOneOrNull()?.toEntity()

  suspend fun upsert(server: ServerEntity) {
    database.transaction {
      database.serverQueries.insert(
        id = server.id,
        name = server.name,
        url = server.url,
        version = server.version,
        created_at = server.createdAt,
        last_used_at = server.lastUsedAt,
      )
    }
  }

  suspend fun updateLastUsed(
    serverId: String,
    timestamp: Long,
  ) {
    database.transaction {
      database.serverQueries.updateLastUsed(
        last_used_at = timestamp,
        id = serverId,
      )
    }
  }

  suspend fun delete(serverId: String) {
    database.transaction {
      database.serverQueries.delete(id = serverId)
    }
  }

  suspend fun deleteAll() {
    database.transaction {
      database.serverQueries.deleteAll()
    }
  }
}

/**
 * Maps a SQLDelight-generated `Server` row to a [ServerEntity].
 */
private fun Server.toEntity() = ServerEntity(
  id = id,
  name = name,
  url = url,
  version = version,
  createdAt = created_at,
  lastUsedAt = last_used_at,
)
