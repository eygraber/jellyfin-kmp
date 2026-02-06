package com.eygraber.jellyfin.services.database

/**
 * Configuration for the Jellyfin local database.
 *
 * @param databaseName The name of the database file.
 */
data class DatabaseConfig(
  val databaseName: String = "jellyfin.db",
)
