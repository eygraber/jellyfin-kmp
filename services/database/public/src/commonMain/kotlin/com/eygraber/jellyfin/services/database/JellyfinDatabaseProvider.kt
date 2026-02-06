package com.eygraber.jellyfin.services.database

import app.cash.sqldelight.db.SqlDriver

/**
 * Provides a [SqlDriver] for the Jellyfin database.
 *
 * Each platform implements this to create the appropriate driver
 * using the correct SQLite backend for that platform.
 */
interface JellyfinDatabaseProvider {
  /**
   * Creates a [SqlDriver] configured for the current platform.
   *
   * The returned driver is ready for use and has already been initialized
   * with the database schema (created or migrated as needed).
   */
  fun createDriver(): SqlDriver
}
