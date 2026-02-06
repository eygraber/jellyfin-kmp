package com.eygraber.jellyfin.common

import kotlinx.coroutines.delay
import kotlin.math.min
import kotlin.math.pow

/**
 * Configuration for retry behavior with exponential backoff.
 *
 * @param maxRetries The maximum number of retry attempts (0 means no retries).
 * @param initialDelayMs The delay before the first retry, in milliseconds.
 * @param maxDelayMs The maximum delay between retries, in milliseconds.
 * @param multiplier The multiplier applied to the delay after each retry.
 * @param shouldRetry A predicate that determines whether a given error should be retried.
 */
data class RetryConfig(
  val maxRetries: Int = DEFAULT_MAX_RETRIES,
  val initialDelayMs: Long = DEFAULT_INITIAL_DELAY_MS,
  val maxDelayMs: Long = DEFAULT_MAX_DELAY_MS,
  val multiplier: Double = DEFAULT_MULTIPLIER,
  val shouldRetry: (JellyfinResult.Error) -> Boolean = { true },
) {
  companion object {
    const val DEFAULT_MAX_RETRIES = 3
    const val DEFAULT_INITIAL_DELAY_MS = 1_000L
    const val DEFAULT_MAX_DELAY_MS = 30_000L
    const val DEFAULT_MULTIPLIER = 2.0
  }
}

/**
 * Executes [block] with retry logic using exponential backoff.
 *
 * If [block] returns a [JellyfinResult.Error] and [RetryConfig.shouldRetry] returns `true`,
 * the operation is retried up to [RetryConfig.maxRetries] times with increasing delays.
 *
 * @param config The retry configuration.
 * @param block The operation to execute and potentially retry.
 * @return The result of the last attempt.
 */
@Suppress("ReturnCount")
suspend fun <T> retryWithBackoff(
  config: RetryConfig = RetryConfig(),
  block: suspend () -> JellyfinResult<T>,
): JellyfinResult<T> {
  var lastResult: JellyfinResult<T> = block()

  repeat(config.maxRetries) { attempt ->
    val current = lastResult
    when {
      current.isSuccess() -> return current

      current is JellyfinResult.Error && !config.shouldRetry(current) ->
        return current

      else -> {
        val delayMs = calculateDelay(
          attempt = attempt,
          initialDelayMs = config.initialDelayMs,
          maxDelayMs = config.maxDelayMs,
          multiplier = config.multiplier,
        )
        delay(delayMs)
        lastResult = block()
      }
    }
  }

  return lastResult
}

/**
 * Calculates the delay for a given retry attempt using exponential backoff.
 */
internal fun calculateDelay(
  attempt: Int,
  initialDelayMs: Long,
  maxDelayMs: Long,
  multiplier: Double,
): Long {
  val exponentialDelay = initialDelayMs * multiplier.pow(attempt.toDouble())
  return min(a = exponentialDelay.toLong(), b = maxDelayMs)
}
