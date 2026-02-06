package com.eygraber.jellyfin.services.logging.impl

import com.eygraber.jellyfin.services.logging.JellyfinLogger
import com.eygraber.jellyfin.services.logging.LogSanitizer
import com.juul.khronicle.Log
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

/**
 * [JellyfinLogger] implementation backed by Khronicle.
 *
 * All messages are sanitized via the provided [LogSanitizer] before being logged.
 * This ensures sensitive data (tokens, passwords, emails) never appears in logs.
 */
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class KhronicleLogger(
  private val sanitizer: LogSanitizer,
) : JellyfinLogger {
  override fun verbose(tag: String, message: String) {
    Log.verbose(tag = tag) { sanitizer.sanitize(message) }
  }

  override fun debug(tag: String, message: String) {
    Log.debug(tag = tag) { sanitizer.sanitize(message) }
  }

  override fun info(tag: String, message: String) {
    Log.info(tag = tag) { sanitizer.sanitize(message) }
  }

  override fun warn(tag: String, message: String, throwable: Throwable?) {
    Log.warn(tag = tag, throwable = throwable) { sanitizer.sanitize(message) }
  }

  override fun error(tag: String, message: String, throwable: Throwable?) {
    Log.error(tag = tag, throwable = throwable) { sanitizer.sanitize(message) }
  }
}
