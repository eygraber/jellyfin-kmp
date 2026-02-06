package com.eygraber.jellyfin.services.logging

/**
 * Logging abstraction for the Jellyfin app.
 *
 * Provides structured logging with severity levels and optional tags.
 * Implementations handle platform-specific log output, crash reporting,
 * and sensitive data sanitization.
 */
interface JellyfinLogger {
  fun verbose(tag: String, message: String)
  fun debug(tag: String, message: String)
  fun info(tag: String, message: String)
  fun warn(tag: String, message: String, throwable: Throwable? = null)
  fun error(tag: String, message: String, throwable: Throwable? = null)
}
