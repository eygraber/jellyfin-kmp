package com.eygraber.jellyfin.services.database.impl

import android.content.Context
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import app.cash.sqldelight.db.SqlDriver
import com.eygraber.jellyfin.di.qualifiers.AppContext
import com.eygraber.jellyfin.services.database.DatabaseConfig
import com.eygraber.jellyfin.services.database.JellyfinDatabaseProvider
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteConfiguration
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteDatabaseType
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteDriver
import com.eygraber.sqldelight.androidx.driver.FileProvider
import com.eygraber.sqldelight.androidx.driver.SqliteJournalMode
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
internal class AndroidJellyfinDatabaseProvider(
  @param:AppContext private val context: Context,
  private val config: DatabaseConfig,
) : JellyfinDatabaseProvider {
  override fun createDriver(): SqlDriver = AndroidxSqliteDriver(
    driver = BundledSQLiteDriver(),
    databaseType = AndroidxSqliteDatabaseType.FileProvider(
      context = context,
      name = config.databaseName,
    ),
    schema = JellyfinDatabase.Schema,
    configuration = AndroidxSqliteConfiguration(
      isForeignKeyConstraintsEnabled = true,
      journalMode = SqliteJournalMode.WAL,
    ),
  )
}
