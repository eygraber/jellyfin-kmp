package com.eygraber.jellyfin.domain.validators

import androidx.compose.runtime.Immutable
import dev.zacsweers.metro.Inject

/**
 * Validates Jellyfin server URLs.
 *
 * Ensures the URL is well-formed and uses an appropriate protocol.
 * Automatically prepends "https://" if no scheme is provided.
 */
@Inject
class ServerUrlValidator {
  @Immutable
  enum class Result {
    Valid,
    Empty,
    InvalidFormat,
    InsecureProtocol,
  }

  /**
   * Validates the given server URL.
   *
   * @param url The server URL to validate.
   * @return The validation [Result].
   */
  fun validate(url: CharSequence): Result =
    when {
      url.isBlank() -> Result.Empty
      else -> validateFormat(url.toString().trim())
    }

  /**
   * Normalizes a server URL by ensuring it has a scheme and trimming trailing slashes.
   *
   * @param url The URL to normalize.
   * @return The normalized URL, or null if the URL is invalid.
   */
  fun normalize(url: String): String? {
    val trimmed = url.trim()
    if(trimmed.isBlank()) return null

    val withScheme = when {
      trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
      else -> "https://$trimmed"
    }

    return if(validateFormat(withScheme) != Result.InvalidFormat) {
      withScheme.trimEnd('/')
    }
    else {
      null
    }
  }

  private fun validateFormat(url: String): Result {
    val withScheme = when {
      url.startsWith("http://") -> return Result.InsecureProtocol
      url.startsWith("https://") -> url
      else -> "https://$url"
    }

    return if(UrlPattern.matches(withScheme)) {
      Result.Valid
    }
    else {
      Result.InvalidFormat
    }
  }

  companion object {
    private val UrlPattern = Regex(
      """https?://[a-zA-Z0-9\-.]+(:\d{1,5})?(/\S*)?""",
    )
  }
}
