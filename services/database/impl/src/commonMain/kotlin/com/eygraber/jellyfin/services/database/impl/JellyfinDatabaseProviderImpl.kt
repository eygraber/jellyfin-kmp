package com.eygraber.jellyfin.services.database.impl

import app.cash.sqldelight.db.SqlDriver
import com.eygraber.jellyfin.services.database.DatabaseConfig
import com.eygraber.jellyfin.services.database.JellyfinDatabaseProvider

/**
 * Implementation of [JellyfinDatabaseProvider] that creates platform-specific
 * SQLite drivers and initializes the [JellyfinDatabase].
 *
 * The database is created lazily on first access and reused for subsequent calls.
 */
class JellyfinDatabaseProviderImpl(
  private val config: DatabaseConfig = DatabaseConfig(),
) : JellyfinDatabaseProvider {
  override fun createDriver(): SqlDriver = createPlatformDriver(
    databaseName = config.databaseName,
  )

  /**
   * Creates a [JellyfinDatabase] instance using the platform-specific driver.
   */
  fun createDatabase(): JellyfinDatabase = JellyfinDatabase(
    driver = createDriver(),
  )
}
