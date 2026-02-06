package com.eygraber.jellyfin.services.logging.impl

import com.eygraber.jellyfin.services.logging.LogSanitizer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

/**
 * Default [LogSanitizer] that redacts common sensitive data patterns.
 *
 * Detects and replaces:
 * - Authorization headers (e.g., `Authorization: Bearer <token>`)
 * - Access tokens (e.g., `token=...`, `access_token=...`)
 * - X-Emby-Authorization headers
 * - Passwords (e.g., `password=...`, `pw=...`)
 * - API keys (e.g., `api_key=...`, `apikey=...`)
 * - Email addresses
 */
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultLogSanitizer : LogSanitizer {
  override fun sanitize(message: String): String =
    sensitivePatterns.fold(message) { current, (pattern, replacement) ->
      pattern.replace(input = current, replacement = replacement)
    }

  companion object {
    private val sensitivePatterns = listOf(
      // Authorization headers: "Authorization: Bearer <token>" or "Authorization: <token>"
      Regex(
        pattern = """(?i)(authorization|x-emby-authorization)\s*:\s*\S+(?:\s+\S+)?""",
      ) to "[REDACTED_TOKEN]",

      // Simple key=value token patterns
      Regex(
        pattern = """(?i)(access_token|token)\s*[=:]\s*\S+""",
      ) to "[REDACTED_TOKEN]",

      Regex(
        pattern = """(?i)(password|pw|passwd)\s*[=:]\s*\S+""",
      ) to "[REDACTED_PASSWORD]",

      Regex(
        pattern = """(?i)(api_key|apikey|api-key)\s*[=:]\s*\S+""",
      ) to "[REDACTED_API_KEY]",

      Regex(
        pattern = """[\w.+\-]+@[\w.\-]+\.\w{2,}""",
      ) to "[REDACTED_EMAIL]",
    )
  }
}
