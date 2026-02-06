package com.eygraber.jellyfin.services.database.impl

import android.content.Context
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import app.cash.sqldelight.db.SqlDriver
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteDatabaseType
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteDriver
import com.eygraber.sqldelight.androidx.driver.FileProvider

/**
 * Holder for the Android application [Context] needed by the database driver.
 *
 * Must be initialized before any database access occurs, typically during
 * application startup.
 */
internal object AndroidContextHolder {
  lateinit var applicationContext: Context
}

internal actual fun createPlatformDriver(databaseName: String): SqlDriver =
  AndroidxSqliteDriver(
    driver = BundledSQLiteDriver(),
    databaseType = AndroidxSqliteDatabaseType.FileProvider(
      context = AndroidContextHolder.applicationContext,
      name = databaseName,
    ),
    schema = JellyfinDatabase.Schema,
  )
