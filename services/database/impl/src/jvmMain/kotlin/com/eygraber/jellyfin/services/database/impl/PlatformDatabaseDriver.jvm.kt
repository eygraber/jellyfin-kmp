package com.eygraber.jellyfin.services.database.impl

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import app.cash.sqldelight.db.SqlDriver
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteDatabaseType
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteDriver
import com.eygraber.sqldelight.androidx.driver.File
import java.io.File

internal actual fun createPlatformDriver(databaseName: String): SqlDriver {
  val appDataDir = File(System.getProperty("user.home"), ".jellyfin-kmp")
  appDataDir.mkdirs()

  return AndroidxSqliteDriver(
    driver = BundledSQLiteDriver(),
    databaseType = AndroidxSqliteDatabaseType.File(
      file = File(appDataDir, databaseName),
    ),
    schema = JellyfinDatabase.Schema,
  )
}
