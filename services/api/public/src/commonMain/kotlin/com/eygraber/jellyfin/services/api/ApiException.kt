package com.eygraber.jellyfin.services.api

/**
 * Exception thrown when an API call fails with an HTTP error status.
 *
 * @param statusCode The HTTP status code, or null for non-HTTP errors.
 * @param message A human-readable error message.
 */
class ApiException(
  val statusCode: Int? = null,
  override val message: String? = null,
) : Exception(message ?: "API error (status: ${statusCode?.toString() ?: "unknown"})")
