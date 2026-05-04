package com.eygraber.jellyfin.services.database.impl

import app.cash.sqldelight.db.SqlDriver
import com.eygraber.jellyfin.services.database.DatabaseConfig
import com.eygraber.jellyfin.services.database.JellyfinDatabaseProvider
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteConfiguration
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteDatabaseType
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteDriver
import com.eygraber.sqldelight.androidx.driver.SqliteJournalMode.WAL
import com.eygraber.sqldelight.androidx.driver.opfs.OpfsMultiTabMode
import com.eygraber.sqldelight.androidx.driver.opfs.androidxSqliteOpfsDriver
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
internal class WasmJsJellyfinDatabaseProvider(
  private val config: DatabaseConfig,
) : JellyfinDatabaseProvider {
  override fun createDriver(): SqlDriver = AndroidxSqliteDriver(
    driver = androidxSqliteOpfsDriver(OpfsMultiTabMode.Shared),
    databaseType = AndroidxSqliteDatabaseType.File(config.databaseName),
    schema = JellyfinDatabase.Schema,
    configuration = AndroidxSqliteConfiguration(
      isForeignKeyConstraintsEnabled = true,
      journalMode = WAL,
    ),
  )
}
