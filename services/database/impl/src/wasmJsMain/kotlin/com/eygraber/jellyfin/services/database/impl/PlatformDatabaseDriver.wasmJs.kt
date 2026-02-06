package com.eygraber.jellyfin.services.database.impl

import app.cash.sqldelight.db.SqlDriver

/**
 * WasmJs database support is not yet implemented.
 *
 * SQLDelight's web worker driver requires `generateAsync = true` which
 * changes the generated API for all platforms. Full WasmJs support
 * will be added in a future issue using the async web worker driver.
 *
 * @throws UnsupportedOperationException Always, since WasmJs database is not yet supported.
 */
internal actual fun createPlatformDriver(databaseName: String): SqlDriver {
  error("Database is not yet supported on WasmJs. See PlatformDatabaseDriver.wasmJs.kt for details.")
}
