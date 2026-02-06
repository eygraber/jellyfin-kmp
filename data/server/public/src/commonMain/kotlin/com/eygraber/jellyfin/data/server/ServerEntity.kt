package com.eygraber.jellyfin.data.server

/**
 * Represents a saved Jellyfin server in the local database.
 *
 * @param id The unique server identifier (from the Jellyfin server's own ID).
 * @param name The display name of the server.
 * @param url The URL used to connect to the server.
 * @param version The server's Jellyfin version string, if known.
 * @param createdAt Timestamp (epoch millis) when this server was first added.
 * @param lastUsedAt Timestamp (epoch millis) when this server was last connected to.
 */
data class ServerEntity(
  val id: String,
  val name: String,
  val url: String,
  val version: String?,
  val createdAt: Long,
  val lastUsedAt: Long,
)
