package com.eygraber.jellyfin.services.database.impl

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import app.cash.sqldelight.db.SqlDriver
import com.eygraber.jellyfin.services.database.DatabaseConfig
import com.eygraber.jellyfin.services.database.JellyfinDatabaseProvider
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteConfiguration
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteDatabaseType
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteDriver
import com.eygraber.sqldelight.androidx.driver.SqliteJournalMode
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
internal class IosJellyfinDatabaseProvider(
  private val config: DatabaseConfig,
) : JellyfinDatabaseProvider {
  @Suppress("UNCHECKED_CAST")
  override fun createDriver(): SqlDriver {
    val documentDirectory = NSSearchPathForDirectoriesInDomains(
      directory = NSDocumentDirectory,
      domainMask = NSUserDomainMask,
      expandTilde = true,
    ).first() as String

    return AndroidxSqliteDriver(
      driver = BundledSQLiteDriver(),
      databaseType = AndroidxSqliteDatabaseType.File(
        databaseFilePath = "$documentDirectory/${config.databaseName}",
      ),
      schema = JellyfinDatabase.Schema,
      configuration = AndroidxSqliteConfiguration(
        isForeignKeyConstraintsEnabled = true,
        journalMode = SqliteJournalMode.WAL,
      ),
    )
  }
}
