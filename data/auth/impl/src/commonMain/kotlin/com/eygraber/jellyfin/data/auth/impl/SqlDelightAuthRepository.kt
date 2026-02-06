package com.eygraber.jellyfin.data.auth.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.common.andThen
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.common.mapSuccessTo
import com.eygraber.jellyfin.common.runResult
import com.eygraber.jellyfin.data.auth.AuthRepository
import com.eygraber.jellyfin.data.auth.QuickConnectResult
import com.eygraber.jellyfin.data.auth.QuickConnectState
import com.eygraber.jellyfin.data.auth.UserSessionEntity
import com.eygraber.jellyfin.services.logging.JellyfinLogger
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.Flow

/**
 * SQLDelight-backed implementation of [AuthRepository].
 *
 * Coordinates between [AuthRemoteDataSource] for authentication
 * and [AuthLocalDataSource] for session persistence.
 * The repository is stateless and not scoped as a singleton.
 */
@ContributesBinding(AppScope::class)
class SqlDelightAuthRepository(
  private val localDataSource: AuthLocalDataSource,
  private val remoteDataSource: AuthRemoteDataSource,
  private val logger: JellyfinLogger,
  private val clock: () -> Long = { currentTimeMillis() },
) : AuthRepository {
  override fun observeActiveSession(): Flow<UserSessionEntity?> =
    localDataSource.observeActive()

  override fun observeSessionsForServer(serverId: String): Flow<List<UserSessionEntity>> =
    localDataSource.observeByServerId(serverId = serverId)

  override suspend fun getActiveSession(): JellyfinResult<UserSessionEntity?> = runResult {
    localDataSource.getActive()
  }

  override suspend fun getSessionsForServer(
    serverId: String,
  ): JellyfinResult<List<UserSessionEntity>> = runResult {
    localDataSource.getByServerId(serverId = serverId)
  }

  @Suppress("ReturnCount")
  override suspend fun login(
    serverId: String,
    serverUrl: String,
    username: String,
    password: String,
  ): JellyfinResult<UserSessionEntity> {
    logger.debug(tag = TAG, message = "Logging in user: $username on $serverUrl")

    val authResult = remoteDataSource.authenticateByName(
      serverUrl = serverUrl,
      username = username,
      password = password,
    )

    if(!authResult.isSuccess()) {
      @Suppress("UNCHECKED_CAST")
      return authResult as JellyfinResult<UserSessionEntity>
    }

    val result = authResult.value
    val accessToken = result.accessToken
      ?: return JellyfinResult.Error(
        message = "Authentication response missing access token",
        isEphemeral = false,
      )

    val userId = result.user?.id
      ?: return JellyfinResult.Error(
        message = "Authentication response missing user ID",
        isEphemeral = false,
      )

    val now = clock()
    val session = UserSessionEntity(
      id = "${serverId}_$userId",
      serverId = serverId,
      userId = userId,
      username = result.user?.name.orEmpty().ifEmpty { username },
      accessToken = accessToken,
      isActive = true,
      createdAt = now,
      lastUsedAt = now,
    )

    return runResult {
      localDataSource.upsert(session)
      localDataSource.setActive(sessionId = session.id, timestamp = now)
      logger.info(tag = TAG, message = "Login successful for user: $username")
      session
    }
  }

  override suspend fun initiateQuickConnect(
    serverUrl: String,
  ): JellyfinResult<QuickConnectState> {
    logger.debug(tag = TAG, message = "Initiating Quick Connect on $serverUrl")

    return remoteDataSource.initiateQuickConnect(serverUrl = serverUrl)
      .mapSuccessTo {
        QuickConnectState(
          code = code.orEmpty(),
          secret = secret.orEmpty(),
        )
      }
  }

  @Suppress("ReturnCount")
  override suspend fun checkQuickConnect(
    serverId: String,
    serverUrl: String,
    secret: String,
  ): JellyfinResult<QuickConnectResult> {
    val statusResult = remoteDataSource.getQuickConnectStatus(
      serverUrl = serverUrl,
      secret = secret,
    )

    if(!statusResult.isSuccess()) {
      @Suppress("UNCHECKED_CAST")
      return statusResult as JellyfinResult<QuickConnectResult>
    }

    val status = statusResult.value
    if(!status.authenticated) {
      return JellyfinResult.Success(QuickConnectResult.Pending)
    }

    // Quick Connect approved - exchange secret for an access token
    // by authenticating with the secret as the password
    val authResult = remoteDataSource.authenticateByName(
      serverUrl = serverUrl,
      username = "",
      password = secret,
    )

    if(!authResult.isSuccess()) {
      @Suppress("UNCHECKED_CAST")
      return authResult as JellyfinResult<QuickConnectResult>
    }

    val result = authResult.value
    val accessToken = result.accessToken
      ?: return JellyfinResult.Error(
        message = "Quick Connect authentication response missing access token",
        isEphemeral = false,
      )

    val userId = result.user?.id
      ?: return JellyfinResult.Error(
        message = "Quick Connect authentication response missing user ID",
        isEphemeral = false,
      )

    val now = clock()
    val session = UserSessionEntity(
      id = "${serverId}_$userId",
      serverId = serverId,
      userId = userId,
      username = result.user?.name.orEmpty(),
      accessToken = accessToken,
      isActive = true,
      createdAt = now,
      lastUsedAt = now,
    )

    return runResult {
      localDataSource.upsert(session)
      localDataSource.setActive(sessionId = session.id, timestamp = now)
      logger.info(tag = TAG, message = "Quick Connect authentication successful")
      QuickConnectResult.Authenticated(session = session)
    }
  }

  override suspend fun setActiveSession(sessionId: String): JellyfinResult<Unit> = runResult {
    logger.debug(tag = TAG, message = "Setting active session: $sessionId")
    localDataSource.setActive(
      sessionId = sessionId,
      timestamp = clock(),
    )
  }

  override suspend fun logout(): JellyfinResult<Unit> {
    logger.info(tag = TAG, message = "Logging out active session")
    val activeSession = localDataSource.getActive()
      ?: return JellyfinResult.Error(
        message = "No active session to logout",
        isEphemeral = false,
      )

    // Notify server, then remove locally regardless of server response
    return remoteDataSource.logout()
      .andThen {
        localDataSource.delete(sessionId = activeSession.id)
      }
  }

  override suspend fun logoutSession(sessionId: String): JellyfinResult<Unit> = runResult {
    logger.info(tag = TAG, message = "Logging out session: $sessionId")
    localDataSource.delete(sessionId = sessionId)
  }

  override suspend fun logoutServer(serverId: String): JellyfinResult<Unit> = runResult {
    logger.info(tag = TAG, message = "Logging out all sessions for server: $serverId")
    localDataSource.deleteByServerId(serverId = serverId)
  }

  companion object {
    private const val TAG = "AuthRepository"
  }
}
