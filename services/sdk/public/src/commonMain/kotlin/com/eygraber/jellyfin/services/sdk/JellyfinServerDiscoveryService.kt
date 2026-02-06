package com.eygraber.jellyfin.services.sdk

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.sdk.core.model.ServerDiscoveryInfo
import kotlinx.coroutines.flow.Flow

/**
 * High-level service for discovering and connecting to Jellyfin servers.
 *
 * Orchestrates server discovery, URL validation, server connection,
 * and version compatibility checking into a unified flow.
 */
interface JellyfinServerDiscoveryService {
  /**
   * Discovers Jellyfin servers on the local network.
   *
   * @param timeoutMs How long to listen for server responses.
   * @return A [Flow] of discovered servers.
   */
  fun discoverServers(
    timeoutMs: Long = DEFAULT_DISCOVERY_TIMEOUT_MS,
  ): Flow<ServerDiscoveryInfo>

  /**
   * Connects to a server by URL, validating the URL and checking version compatibility.
   *
   * This method:
   * 1. Validates and normalizes the URL
   * 2. Connects to the server and retrieves public system info
   * 3. Checks server version compatibility
   * 4. Returns a [ServerConnectionInfo] with all details
   *
   * @param serverUrl The server URL (may or may not include scheme).
   * @return A [JellyfinResult] containing the [ServerConnectionInfo].
   */
  suspend fun connectToServer(serverUrl: String): JellyfinResult<ServerConnectionInfo>

  /**
   * Connects to a previously discovered server.
   *
   * @param discoveryInfo The server info from network discovery.
   * @return A [JellyfinResult] containing the [ServerConnectionInfo].
   */
  suspend fun connectToDiscoveredServer(
    discoveryInfo: ServerDiscoveryInfo,
  ): JellyfinResult<ServerConnectionInfo>

  companion object {
    const val DEFAULT_DISCOVERY_TIMEOUT_MS = 3_000L
  }
}
