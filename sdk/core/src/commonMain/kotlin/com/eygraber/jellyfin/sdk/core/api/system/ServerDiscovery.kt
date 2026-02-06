package com.eygraber.jellyfin.sdk.core.api.system

import com.eygraber.jellyfin.sdk.core.model.ServerDiscoveryInfo
import kotlinx.coroutines.flow.Flow

/**
 * Discovers Jellyfin servers on the local network.
 *
 * Server discovery uses UDP broadcast to find servers.
 * Platform-specific implementations handle the actual network operations.
 */
interface ServerDiscovery {
  /**
   * Discovers Jellyfin servers by sending a UDP broadcast on port 7359.
   * Returns a flow of discovered servers as they respond.
   *
   * @param timeoutMs How long to wait for responses in milliseconds.
   */
  fun discoverServers(timeoutMs: Long = DEFAULT_DISCOVERY_TIMEOUT_MS): Flow<ServerDiscoveryInfo>

  companion object {
    const val DISCOVERY_PORT = 7359
    const val DISCOVERY_MESSAGE = "who is JellyfinServer?"
    const val DEFAULT_DISCOVERY_TIMEOUT_MS = 3_000L
  }
}
