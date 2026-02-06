package com.eygraber.jellyfin.domain.validators

import androidx.compose.runtime.Immutable
import dev.zacsweers.metro.Inject

/**
 * Validates Jellyfin server version compatibility.
 *
 * Ensures the server meets the minimum version requirements
 * for this client to function properly.
 */
@Inject
class ServerVersionValidator {
  @Immutable
  sealed interface Result {
    data object Compatible : Result
    data object Incompatible : Result
    data object Unknown : Result
  }

  /**
   * Checks if the given server version is compatible with this client.
   *
   * @param version The server version string (e.g., "10.9.0").
   * @return The compatibility [Result].
   */
  fun validate(version: String?): Result {
    if(version == null) return Result.Unknown

    val parts = version.split('.').mapNotNull { it.toIntOrNull() }
    if(parts.size < REQUIRED_VERSION_PARTS) return Result.Unknown

    val major = parts[0]
    val minor = parts[1]

    return when {
      major > MIN_MAJOR_VERSION -> Result.Compatible
      major == MIN_MAJOR_VERSION && minor >= MIN_MINOR_VERSION -> Result.Compatible
      else -> Result.Incompatible
    }
  }

  companion object {
    /**
     * Minimum supported Jellyfin server version.
     * Jellyfin 10.8.0 introduced several API changes this client depends on.
     */
    const val MIN_MAJOR_VERSION = 10
    const val MIN_MINOR_VERSION = 8

    private const val REQUIRED_VERSION_PARTS = 2
  }
}
