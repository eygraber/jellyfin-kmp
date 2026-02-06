package com.eygraber.jellyfin.services.api

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.common.runResult
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

/**
 * Represents a raw HTTP response from the Jellyfin server.
 *
 * This is the type returned by Ktorfit API interfaces before conversion
 * to [JellyfinResult]. It wraps the raw response body along with
 * HTTP status information.
 *
 * Ktorfit API interfaces should use [JellyfinResponse] parameterized with
 * either [JsonObject] or [JsonArray] from kotlinx.serialization.
 *
 * @param T The type of the response body, typically [JsonObject] or [JsonArray].
 */
sealed interface JellyfinResponse<out T> {
  /**
   * A successful HTTP response containing the parsed body.
   */
  data class Success<T>(
    val body: T,
    val statusCode: Int,
  ) : JellyfinResponse<T>

  /**
   * An error response from the server or a network/parsing failure.
   */
  data class Error(
    val statusCode: Int? = null,
    val message: String? = null,
    val cause: Throwable? = null,
  ) : JellyfinResponse<Nothing>
}

/**
 * Converts a [JellyfinResponse] to a [JellyfinResult].
 *
 * Successful responses are mapped using the provided [mapper] to transform the
 * raw response body into the desired domain type.
 *
 * Error responses are converted to [JellyfinResult.Error] with details preserved.
 *
 * @param mapper A function to transform the response body into the desired type.
 */
inline fun <T, R> JellyfinResponse<T>.toResult(
  mapper: (T) -> R,
): JellyfinResult<R> = when(this) {
  is JellyfinResponse.Success -> runResult { mapper(body) }
  is JellyfinResponse.Error -> JellyfinResult.Error.Detailed(
    details = cause ?: ApiException(
      statusCode = statusCode,
      message = message,
    ),
    message = message ?: "API error (status: ${statusCode?.toString() ?: "unknown"})",
    isEphemeral = true,
  )
}

/**
 * Converts a [JellyfinResponse] to a [JellyfinResult] without transforming the body.
 */
fun <T> JellyfinResponse<T>.toResult(): JellyfinResult<T> = toResult { it }
