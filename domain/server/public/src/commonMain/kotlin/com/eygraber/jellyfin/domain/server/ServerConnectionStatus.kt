package com.eygraber.jellyfin.domain.server

/**
 * Represents the connection status of a saved server.
 */
enum class ServerConnectionStatus {
  /**
   * The server's connection status has not been checked yet.
   */
  Unknown,

  /**
   * The server is reachable and responding.
   */
  Online,

  /**
   * The server is not reachable.
   */
  Offline,

  /**
   * A connection check is currently in progress.
   */
  Checking,
}
