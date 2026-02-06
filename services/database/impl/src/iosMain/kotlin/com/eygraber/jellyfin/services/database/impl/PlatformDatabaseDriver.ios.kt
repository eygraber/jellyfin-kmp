package com.eygraber.jellyfin.services.database.impl

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import app.cash.sqldelight.db.SqlDriver
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteDatabaseType
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteDriver
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

@Suppress("UNCHECKED_CAST")
internal actual fun createPlatformDriver(databaseName: String): SqlDriver {
  val documentDirectory = NSSearchPathForDirectoriesInDomains(
    directory = NSDocumentDirectory,
    domainMask = NSUserDomainMask,
    expandTilde = true,
  ).first() as String

  return AndroidxSqliteDriver(
    driver = BundledSQLiteDriver(),
    databaseType = AndroidxSqliteDatabaseType.File(
      databaseFilePath = "$documentDirectory/$databaseName",
    ),
    schema = JellyfinDatabase.Schema,
  )
}
