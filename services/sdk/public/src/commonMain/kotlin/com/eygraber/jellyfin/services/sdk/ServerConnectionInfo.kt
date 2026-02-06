package com.eygraber.jellyfin.services.sdk

import com.eygraber.jellyfin.sdk.core.model.PublicSystemInfo

/**
 * Information about a successfully connected Jellyfin server.
 *
 * Contains the server's public info, normalized URL, and compatibility status.
 *
 * @param serverUrl The normalized URL used to connect to the server.
 * @param systemInfo The server's public system information.
 * @param isVersionCompatible Whether the server version meets minimum requirements.
 */
data class ServerConnectionInfo(
  val serverUrl: String,
  val systemInfo: PublicSystemInfo,
  val isVersionCompatible: Boolean,
)
