package com.eygraber.jellyfin.services.database.impl

import app.cash.sqldelight.db.SqlDriver

/**
 * Creates a platform-specific [SqlDriver] for the Jellyfin database.
 *
 * Each platform provides its own implementation:
 * - Android: AndroidX SQLite Driver via [Context] file provider
 * - iOS: AndroidX SQLite Driver with file-based storage
 * - Desktop/JVM: AndroidX SQLite Driver with file-based storage
 * - Web (WasmJs): SQLDelight web worker driver
 *
 * @param databaseName The name of the database file.
 */
internal expect fun createPlatformDriver(databaseName: String): SqlDriver
