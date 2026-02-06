package com.eygraber.jellyfin.services.sdk.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.common.mapSuccessTo
import com.eygraber.jellyfin.domain.validators.ServerUrlValidator
import com.eygraber.jellyfin.domain.validators.ServerVersionValidator
import com.eygraber.jellyfin.sdk.core.model.ServerDiscoveryInfo
import com.eygraber.jellyfin.services.logging.JellyfinLogger
import com.eygraber.jellyfin.services.sdk.JellyfinServerDiscoveryService
import com.eygraber.jellyfin.services.sdk.JellyfinServerService
import com.eygraber.jellyfin.services.sdk.ServerConnectionInfo
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

/**
 * Default implementation of [JellyfinServerDiscoveryService].
 *
 * Orchestrates server discovery, URL validation, connection,
 * and version compatibility checking.
 */
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultJellyfinServerDiscoveryService(
  private val serverService: JellyfinServerService,
  private val urlValidator: ServerUrlValidator,
  private val versionValidator: ServerVersionValidator,
  private val logger: JellyfinLogger,
) : JellyfinServerDiscoveryService {
  override fun discoverServers(timeoutMs: Long): Flow<ServerDiscoveryInfo> {
    logger.info(tag = TAG, message = "Starting server discovery (timeout: ${timeoutMs}ms)")
    return serverService.discoverServers(timeoutMs = timeoutMs)
  }

  @Suppress("ReturnCount")
  override suspend fun connectToServer(
    serverUrl: String,
  ): JellyfinResult<ServerConnectionInfo> {
    // Step 1: Validate and normalize the URL
    val validationResult = urlValidator.validate(serverUrl)
    if(validationResult != ServerUrlValidator.Result.Valid) {
      val errorMessage = when(validationResult) {
        ServerUrlValidator.Result.Empty -> "Server URL is required"
        ServerUrlValidator.Result.InvalidFormat -> "Invalid server URL format"
        ServerUrlValidator.Result.InsecureProtocol -> "HTTP connections are not supported, use HTTPS"
        ServerUrlValidator.Result.Valid -> error("Unexpected valid result")
      }
      logger.warn(tag = TAG, message = "URL validation failed: $errorMessage")
      return JellyfinResult.Error(message = errorMessage, isEphemeral = false)
    }

    val normalizedUrl = urlValidator.normalize(serverUrl)
      ?: return JellyfinResult.Error(
        message = "Failed to normalize server URL",
        isEphemeral = false,
      )

    logger.info(tag = TAG, message = "Connecting to server: $normalizedUrl")

    // Step 2: Connect to the server
    val connectionResult = serverService.connectToServer(normalizedUrl)
    if(!connectionResult.isSuccess()) return connectionResult.mapSuccessTo { error("unreachable") }

    val systemInfo = connectionResult.value

    // Step 3: Check version compatibility
    val versionResult = versionValidator.validate(systemInfo.version)
    val isCompatible = versionResult is ServerVersionValidator.Result.Compatible

    if(!isCompatible && versionResult is ServerVersionValidator.Result.Incompatible) {
      logger.warn(
        tag = TAG,
        message = "Server version ${systemInfo.version.orEmpty()} is below minimum " +
          "${ServerVersionValidator.MIN_MAJOR_VERSION}.${ServerVersionValidator.MIN_MINOR_VERSION}.0",
      )
    }

    logger.info(
      tag = TAG,
      message = "Server connection established: ${systemInfo.serverName.orEmpty()} " +
        "(${systemInfo.version.orEmpty()}, compatible=$isCompatible)",
    )

    return JellyfinResult.Success(
      ServerConnectionInfo(
        serverUrl = normalizedUrl,
        systemInfo = systemInfo,
        isVersionCompatible = isCompatible,
      ),
    )
  }

  override suspend fun connectToDiscoveredServer(
    discoveryInfo: ServerDiscoveryInfo,
  ): JellyfinResult<ServerConnectionInfo> {
    logger.info(
      tag = TAG,
      message = "Connecting to discovered server: ${discoveryInfo.name.orEmpty()} at ${discoveryInfo.address}",
    )
    return connectToServer(discoveryInfo.address)
  }

  companion object {
    private const val TAG = "ServerDiscovery"
  }
}
