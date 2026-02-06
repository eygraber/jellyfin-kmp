package com.eygraber.jellyfin.services.sdk.impl

import com.eygraber.jellyfin.sdk.core.ServerInfo
import com.eygraber.jellyfin.services.logging.JellyfinLogger
import com.eygraber.jellyfin.services.sdk.JellyfinSessionManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Default implementation of [JellyfinSessionManager].
 *
 * Maintains the current server connection and authentication state
 * as observable [StateFlow]s for reactive UI updates.
 */
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultJellyfinSessionManager(
  private val logger: JellyfinLogger,
) : JellyfinSessionManager {
  private val _currentServer = MutableStateFlow<ServerInfo?>(null)
  override val currentServer: StateFlow<ServerInfo?> = _currentServer.asStateFlow()

  private val _authenticated = MutableStateFlow(false)
  override val authenticated: StateFlow<Boolean> = _authenticated.asStateFlow()

  override fun setServer(
    serverUrl: String,
    serverId: String?,
    serverName: String?,
  ) {
    logger.info(tag = TAG, message = "Setting server: $serverUrl")
    _currentServer.value = ServerInfo(baseUrl = serverUrl)
    _authenticated.value = false
  }

  override fun setAuthentication(
    accessToken: String,
    userId: String,
  ) {
    val current = _currentServer.value
    if(current == null) {
      logger.warn(tag = TAG, message = "Cannot set authentication without a server connection")
      return
    }

    logger.info(tag = TAG, message = "Setting authentication for user: $userId")
    _currentServer.value = current.copy(
      accessToken = accessToken,
      userId = userId,
    )
    _authenticated.value = true
  }

  override fun clearAuthentication() {
    logger.info(tag = TAG, message = "Clearing authentication")
    val current = _currentServer.value
    if(current != null) {
      _currentServer.value = current.copy(
        accessToken = null,
        userId = null,
      )
    }
    _authenticated.value = false
  }

  override fun clearSession() {
    logger.info(tag = TAG, message = "Clearing session")
    _currentServer.value = null
    _authenticated.value = false
  }

  companion object {
    private const val TAG = "JellyfinSessionManager"
  }
}
