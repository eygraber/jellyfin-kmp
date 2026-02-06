package com.eygraber.jellyfin.services.logging

/**
 * Sanitizes log messages to prevent sensitive data from being logged.
 *
 * Implementations should redact tokens, passwords, API keys, and other
 * personally identifiable information (PII) before messages are written to logs.
 */
interface LogSanitizer {
  /**
   * Sanitizes the given [message], replacing any sensitive data with redacted placeholders.
   */
  fun sanitize(message: String): String
}
