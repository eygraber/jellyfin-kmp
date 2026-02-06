package com.eygraber.jellyfin.common

/**
 * Categorization of errors that can occur during Jellyfin operations.
 *
 * Used to determine appropriate error messages, retry behavior, and
 * UI feedback for different failure modes.
 */
enum class JellyfinErrorCategory {
  /**
   * Network connectivity issues (timeout, DNS, no internet).
   * Typically retryable.
   */
  Network,

  /**
   * Authentication or authorization failures (401, 403).
   * Usually requires re-authentication, not retryable.
   */
  Auth,

  /**
   * Server-side errors (5xx status codes).
   * May be retryable depending on the specific error.
   */
  Server,

  /**
   * Client-side errors (4xx status codes other than 401/403).
   * Typically not retryable without modifying the request.
   */
  Client,

  /**
   * Errors that don't fit other categories.
   * May or may not be retryable.
   */
  Unknown,
  ;

  companion object {
    /**
     * Determines the [JellyfinErrorCategory] for a given HTTP status code.
     *
     * @param statusCode The HTTP status code, or null for non-HTTP errors.
     * @return The corresponding error category.
     */
    fun fromStatusCode(statusCode: Int?): JellyfinErrorCategory =
      when {
        statusCode == null -> Unknown
        statusCode == 401 || statusCode == 403 -> Auth
        statusCode in 400..499 -> Client
        statusCode in 500..599 -> Server
        else -> Unknown
      }

    /**
     * Determines the [JellyfinErrorCategory] for a given [Throwable].
     *
     * Maps common exception types to their most likely error category.
     */
    fun fromThrowable(throwable: Throwable): JellyfinErrorCategory =
      when {
        throwable.isNetworkError() -> Network
        else -> Unknown
      }
  }
}

/**
 * Returns `true` if this error category is typically retryable.
 */
val JellyfinErrorCategory.isRetryable: Boolean
  get() = when(this) {
    JellyfinErrorCategory.Network -> true
    JellyfinErrorCategory.Server -> true
    JellyfinErrorCategory.Auth -> false
    JellyfinErrorCategory.Client -> false
    JellyfinErrorCategory.Unknown -> false
  }

/**
 * Heuristic check for whether a [Throwable] represents a network connectivity issue.
 *
 * Checks the exception class name hierarchy since Ktor and platform-specific
 * network exceptions aren't available in commonMain.
 */
private fun Throwable.isNetworkError(): Boolean {
  val name = this::class.simpleName.orEmpty()
  return name.contains("Timeout", ignoreCase = true) ||
    name.contains("Connect", ignoreCase = true) ||
    name.contains("Socket", ignoreCase = true) ||
    name.contains("UnknownHost", ignoreCase = true) ||
    name.contains("NoRouteToHost", ignoreCase = true) ||
    name.contains("Network", ignoreCase = true) ||
    cause?.isNetworkError() == true
}
