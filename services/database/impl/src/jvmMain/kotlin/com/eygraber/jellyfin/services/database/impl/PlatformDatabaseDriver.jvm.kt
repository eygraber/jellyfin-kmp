package com.eygraber.jellyfin.services.database.impl

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import app.cash.sqldelight.db.SqlDriver
import com.eygraber.jellyfin.services.database.DatabaseConfig
import com.eygraber.jellyfin.services.database.JellyfinDatabaseProvider
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteConfiguration
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteDatabaseType
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteDriver
import com.eygraber.sqldelight.androidx.driver.File
import com.eygraber.sqldelight.androidx.driver.SqliteJournalMode
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import java.io.File

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
internal class JvmJellyfinDatabaseProvider(
  private val config: DatabaseConfig,
) : JellyfinDatabaseProvider {
  override fun createDriver(): SqlDriver {
    val appDataDir = File(System.getProperty("user.home"), ".jellyfin-kmp")
    appDataDir.mkdirs()

    return AndroidxSqliteDriver(
      driver = BundledSQLiteDriver(),
      databaseType = AndroidxSqliteDatabaseType.File(
        file = File(appDataDir, config.databaseName),
      ),
      schema = JellyfinDatabase.Schema,
      configuration = AndroidxSqliteConfiguration(
        isForeignKeyConstraintsEnabled = true,
        journalMode = SqliteJournalMode.WAL,
      ),
    )
  }
}
