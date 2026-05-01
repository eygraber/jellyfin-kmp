package com.eygraber.jellyfin.services.database.impl

import app.cash.sqldelight.db.SqlDriver
import com.eygraber.jellyfin.services.database.JellyfinDatabaseProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

/**
 * WasmJs database support is not yet implemented.
 *
 * SQLDelight's web worker driver requires `generateAsync = true` which changes the
 * generated API for all platforms. Full WasmJs support will be added in a future issue
 * using the async web worker driver.
 */
@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
internal class WasmJsJellyfinDatabaseProvider : JellyfinDatabaseProvider {
  override fun createDriver(): SqlDriver =
    error("Database is not yet supported on WasmJs.")
}
