package com.eygraber.jellyfin.data.auth.impl

import com.eygraber.jellyfin.data.auth.UserSessionEntity
import com.eygraber.jellyfin.services.database.impl.JellyfinDatabase
import com.eygraber.jellyfin.services.database.impl.asFlow
import com.eygraber.jellyfin.services.database.impl.awaitAsList
import com.eygraber.jellyfin.services.database.impl.awaitAsOneOrNull
import com.eygraber.jellyfin.services.database.impl.mapToList
import com.eygraber.jellyfin.services.database.impl.mapToOneOrNull
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import migrations.User_session

/**
 * Local data source for user session persistence using SQLDelight.
 *
 * Handles all direct database interactions for user session management.
 * This class is not a singleton; connection pooling is handled by the database driver.
 */
@Inject
class AuthLocalDataSource(
  private val database: JellyfinDatabase,
) {
  fun observeActive(): Flow<UserSessionEntity?> =
    database.userSessionQueries.selectActive()
      .asFlow()
      .mapToOneOrNull()
      .map { it?.toEntity() }

  fun observeByServerId(serverId: String): Flow<List<UserSessionEntity>> =
    database.userSessionQueries.selectByServerId(server_id = serverId)
      .asFlow()
      .mapToList()
      .map { sessions -> sessions.map { it.toEntity() } }

  suspend fun getActive(): UserSessionEntity? =
    database.userSessionQueries.selectActive().awaitAsOneOrNull()?.toEntity()

  suspend fun getById(sessionId: String): UserSessionEntity? =
    database.userSessionQueries.selectById(id = sessionId).awaitAsOneOrNull()?.toEntity()

  suspend fun getByServerId(serverId: String): List<UserSessionEntity> =
    database
      .userSessionQueries
      .selectByServerId(server_id = serverId)
      .awaitAsList()
      .map { it.toEntity() }

  suspend fun upsert(session: UserSessionEntity) {
    database.transaction {
      database.userSessionQueries.insert(
        id = session.id,
        server_id = session.serverId,
        user_id = session.userId,
        username = session.username,
        access_token = session.accessToken,
        is_active = if(session.isActive) 1L else 0L,
        created_at = session.createdAt,
        last_used_at = session.lastUsedAt,
      )
    }
  }

  suspend fun setActive(sessionId: String, timestamp: Long) {
    database.transaction {
      database.userSessionQueries.clearActive()
      database.userSessionQueries.setActive(last_used_at = timestamp, id = sessionId)
    }
  }

  suspend fun clearActive() {
    database.transaction {
      database.userSessionQueries.clearActive()
    }
  }

  suspend fun delete(sessionId: String) {
    database.transaction {
      database.userSessionQueries.delete(id = sessionId)
    }
  }

  suspend fun deleteByServerId(serverId: String) {
    database.transaction {
      database.userSessionQueries.deleteByServerId(server_id = serverId)
    }
  }

  suspend fun deleteAll() {
    database.transaction {
      database.userSessionQueries.deleteAll()
    }
  }
}

/**
 * Maps a SQLDelight-generated `User_session` row to a [UserSessionEntity].
 */
private fun User_session.toEntity() = UserSessionEntity(
  id = id,
  serverId = server_id,
  userId = user_id,
  username = username,
  accessToken = access_token,
  isActive = is_active == 1L,
  createdAt = created_at,
  lastUsedAt = last_used_at,
)
