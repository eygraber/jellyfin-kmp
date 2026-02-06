package com.eygraber.jellyfin.services.sdk

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.sdk.core.JellyfinSdkError
import com.eygraber.jellyfin.sdk.core.SdkResult

/**
 * Converts a [SdkResult] to a [JellyfinResult].
 *
 * [SdkResult.Success] maps to [JellyfinResult.Success].
 * [SdkResult.Failure] maps to [JellyfinResult.Error.Detailed] with the [JellyfinSdkError]
 * preserved as the error details.
 */
fun <T> SdkResult<T>.toJellyfinResult(): JellyfinResult<T> = when(this) {
  is SdkResult.Success -> JellyfinResult.Success(value)
  is SdkResult.Failure -> JellyfinResult.Error.Detailed(
    details = error,
    message = error.message,
    isEphemeral = error.isEphemeral,
  )
}

/**
 * Converts a [SdkResult] to a [JellyfinResult], transforming the success value.
 *
 * @param mapper A function to transform the success value.
 */
inline fun <T, R> SdkResult<T>.toJellyfinResult(
  mapper: (T) -> R,
): JellyfinResult<R> = when(this) {
  is SdkResult.Success -> JellyfinResult.Success(mapper(value))
  is SdkResult.Failure -> JellyfinResult.Error.Detailed(
    details = error,
    message = error.message,
    isEphemeral = error.isEphemeral,
  )
}

/**
 * Determines whether a [JellyfinSdkError] should be considered ephemeral.
 *
 * Network and serialization errors are ephemeral (transient, retryable).
 * Authentication errors are not ephemeral (require user action).
 * HTTP errors are ephemeral for server errors (5xx), not for client errors (4xx).
 */
@PublishedApi
internal val JellyfinSdkError.isEphemeral: Boolean
  get() = when(this) {
    is JellyfinSdkError.Network -> true
    is JellyfinSdkError.Serialization -> true
    is JellyfinSdkError.Authentication -> false
    is JellyfinSdkError.Http -> statusCode in 500..599
    is JellyfinSdkError.Unknown -> true
  }
