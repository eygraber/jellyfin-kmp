package com.eygraber.jellyfin.services.sdk

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.sdk.core.model.PublicSystemInfo
import com.eygraber.jellyfin.sdk.core.model.ServerDiscoveryInfo
import com.eygraber.jellyfin.sdk.core.model.SystemInfo
import kotlinx.coroutines.flow.Flow

/**
 * Service for server discovery and connectivity.
 *
 * Provides operations to discover Jellyfin servers on the local network,
 * connect to a server by URL, and verify server health.
 */
interface JellyfinServerService {
  /**
   * Discovers Jellyfin servers on the local network via UDP broadcast.
   *
   * @param timeoutMs How long to listen for server responses.
   * @return A [Flow] emitting [ServerDiscoveryInfo] for each server found.
   */
  fun discoverServers(timeoutMs: Long = DEFAULT_DISCOVERY_TIMEOUT_MS): Flow<ServerDiscoveryInfo>

  /**
   * Connects to a Jellyfin server by URL and retrieves its public information.
   *
   * This does not require authentication and can be used to verify
   * a server is reachable and get basic server details.
   *
   * @param serverUrl The URL of the Jellyfin server.
   * @return A [JellyfinResult] containing the [PublicSystemInfo].
   */
  suspend fun connectToServer(serverUrl: String): JellyfinResult<PublicSystemInfo>

  /**
   * Gets detailed system information from the currently connected server.
   * Requires authentication.
   *
   * @return A [JellyfinResult] containing the [SystemInfo].
   */
  suspend fun getSystemInfo(): JellyfinResult<SystemInfo>

  /**
   * Pings the currently connected server to check connectivity.
   *
   * @return A [JellyfinResult] containing the ping response string.
   */
  suspend fun ping(): JellyfinResult<String>

  companion object {
    const val DEFAULT_DISCOVERY_TIMEOUT_MS = 3_000L
  }
}
