package com.eygraber.jellyfin.data.auth.impl

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.eygraber.jellyfin.data.auth.UserSessionEntity
import com.eygraber.jellyfin.services.database.impl.JellyfinDatabase
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Local data source for user session persistence using SQLDelight.
 *
 * Handles all direct database interactions for user session management.
 * This class is not a singleton; connection pooling is handled by the database driver.
 */
@Inject
class AuthLocalDataSource(
  private val database: JellyfinDatabase,
  private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
  fun observeActive(): Flow<UserSessionEntity?> =
    database.userSessionQueries.selectActive()
      .asFlow()
      .mapToOneOrNull(dispatcher)
      .map { it?.toEntity() }

  fun observeByServerId(serverId: String): Flow<List<UserSessionEntity>> =
    database.userSessionQueries.selectByServerId(server_id = serverId)
      .asFlow()
      .mapToList(dispatcher)
      .map { sessions -> sessions.map { it.toEntity() } }

  suspend fun getActive(): UserSessionEntity? = withContext(dispatcher) {
    database.userSessionQueries.selectActive().executeAsOneOrNull()?.toEntity()
  }

  suspend fun getById(sessionId: String): UserSessionEntity? = withContext(dispatcher) {
    database.userSessionQueries.selectById(id = sessionId).executeAsOneOrNull()?.toEntity()
  }

  suspend fun getByServerId(serverId: String): List<UserSessionEntity> = withContext(dispatcher) {
    database.userSessionQueries.selectByServerId(server_id = serverId)
      .executeAsList()
      .map { it.toEntity() }
  }

  suspend fun upsert(session: UserSessionEntity): Unit = withContext(dispatcher) {
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

  suspend fun setActive(sessionId: String, timestamp: Long): Unit = withContext(dispatcher) {
    database.transaction {
      database.userSessionQueries.clearActive()
      database.userSessionQueries.setActive(last_used_at = timestamp, id = sessionId)
    }
  }

  suspend fun clearActive(): Unit = withContext(dispatcher) {
    database.userSessionQueries.clearActive()
  }

  suspend fun delete(sessionId: String): Unit = withContext(dispatcher) {
    database.userSessionQueries.delete(id = sessionId)
  }

  suspend fun deleteByServerId(serverId: String): Unit = withContext(dispatcher) {
    database.userSessionQueries.deleteByServerId(server_id = serverId)
  }

  suspend fun deleteAll(): Unit = withContext(dispatcher) {
    database.userSessionQueries.deleteAll()
  }
}

/**
 * Maps a SQLDelight-generated `User_session` row to a [UserSessionEntity].
 */
private fun migrations.User_session.toEntity() = UserSessionEntity(
  id = id,
  serverId = server_id,
  userId = user_id,
  username = username,
  accessToken = access_token,
  isActive = is_active == 1L,
  createdAt = created_at,
  lastUsedAt = last_used_at,
)
