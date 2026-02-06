package com.eygraber.jellyfin.domain.server

import com.eygraber.jellyfin.data.server.ServerEntity

/**
 * A saved server along with its current connection status.
 *
 * @param server The server entity from the database.
 * @param connectionStatus The current connection status of the server.
 * @param isActive Whether this is the currently active server.
 * @param userCount The number of saved user sessions for this server.
 */
data class ServerWithStatus(
  val server: ServerEntity,
  val connectionStatus: ServerConnectionStatus,
  val isActive: Boolean,
  val userCount: Int,
)
