package com.eygraber.jellyfin.common

import kotlin.apply
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.coroutines.cancellation.CancellationException
import kotlin.getOrElse
import kotlin.let

sealed interface JellyfinResult<out T> {
  data class Success<T>(val value: T) : JellyfinResult<T> {
    companion object {
      @PublishedApi
      internal val Empty: JellyfinResult<Unit> = Success(Unit)

      @Suppress("NOTHING_TO_INLINE")
      inline operator fun invoke(): JellyfinResult<Unit> = Empty
    }
  }

  sealed interface Error : JellyfinResult<Nothing> {
    val message: String?
    val isEphemeral: Boolean

    data class Detailed<E>(
      val details: E,
      override val message: String?,
      override val isEphemeral: Boolean,
    ) : Error

    data class Generic(
      override val message: String?,
      override val isEphemeral: Boolean,
    ) : Error

    companion object {
      @PublishedApi
      internal val Empty: JellyfinResult<Nothing> = Generic(message = null, isEphemeral = false)

      @Suppress("NOTHING_TO_INLINE")
      inline operator fun <T> invoke(): JellyfinResult<T> = Empty

      operator fun <T> invoke(
        message: String?,
        isEphemeral: Boolean,
      ): JellyfinResult<T> = Generic(message = message, isEphemeral = isEphemeral)
    }
  }
}

/**
 * Returns `true` if this result is a [JellyfinResult.Success], `false` otherwise.
 *
 * Uses Kotlin contracts to smart-cast this result to [JellyfinResult.Success] when the return value is `true`.
 */
@OptIn(ExperimentalContracts::class)
fun <T> JellyfinResult<T>.isSuccess(): Boolean {
  contract {
    returns(true) implies (this@isSuccess is JellyfinResult.Success<T>)
  }
  return this is JellyfinResult.Success<T>
}

/**
 * Returns `true` if this result is a [JellyfinResult.Error], `false` otherwise.
 *
 * Uses Kotlin contracts to smart-cast this result to [JellyfinResult.Error] when the return value is `true`.
 */
@OptIn(ExperimentalContracts::class)
fun JellyfinResult<*>.isError(): Boolean {
  contract {
    returns(true) implies (this@isError is JellyfinResult.Error)
  }
  return this is JellyfinResult.Error
}

/**
 * Returns the success [value][JellyfinResult.Success.value] if this is a [JellyfinResult.Success],
 * or `null` if this is an error.
 */
val <T> JellyfinResult<T>.successOrNull
  get() = when(this) {
    is JellyfinResult.Error -> null
    is JellyfinResult.Success -> value
  }

/**
 * Returns the [Throwable] [details][JellyfinResult.Error.Detailed.details] if this is a
 * [JellyfinResult.Error.Detailed], and `details` is a `Throwable`, or `null` otherwise.
 */
val <T> JellyfinResult<T>.throwableOrNull
  get() = when(this) {
    is JellyfinResult.Error.Detailed<*> -> details as? Throwable
    is JellyfinResult.Error.Generic -> null
    is JellyfinResult.Success -> null
  }

/**
 * Returns the error [message][JellyfinResult.Error.message] if this is a [JellyfinResult.Error],
 * or `null` otherwise.
 */
val JellyfinResult<*>.errorMessageOrNull
  get() = when(this) {
    is JellyfinResult.Error -> message
    is JellyfinResult.Success -> null
  }

/**
 * Returns the error [details][JellyfinResult.Error.Detailed.details] if this is a [JellyfinResult.Error.Detailed],
 * or `null` otherwise.
 */
val JellyfinResult<*>.errorDetailOrNull
  get() = when(this) {
    is JellyfinResult.Error.Detailed<*> -> details
    is JellyfinResult.Error.Generic -> null
    is JellyfinResult.Success -> null
  }

/**
 * Maps a successful result to [Unit], discarding the original success value.
 *
 * Errors are passed through unchanged.
 */
fun JellyfinResult<*>.mapToUnit() = when(this) {
  is JellyfinResult.Error.Detailed<*> -> this
  is JellyfinResult.Error.Generic -> this
  is JellyfinResult.Success -> JellyfinResult.Success()
}

fun JellyfinResult<JellyfinResult<*>>.unwrapToUnit() = when(this) {
  is JellyfinResult.Error -> this
  is JellyfinResult.Success -> value.mapToUnit()
}

fun <T> JellyfinResult<JellyfinResult<T>>.unwrap() = when(this) {
  is JellyfinResult.Error -> this
  is JellyfinResult.Success -> value
}

/**
 * Transforms this result using the given [mapper] function, which receives this result as a receiver
 * and returns a new [JellyfinResult].
 *
 * Unlike [flatMapSuccessTo], the mapper is always invoked regardless of success or error state.
 */
inline fun <T, R> JellyfinResult<T>.flatMap(mapper: JellyfinResult<T>.() -> JellyfinResult<R>) = mapper()

/**
 * Transforms the success value using the given [mapper] function,
 * wrapping the result in a new [JellyfinResult.Success].
 *
 * If this is an error, it is passed through unchanged.
 */
inline fun <T, R> JellyfinResult<T>.mapSuccessTo(mapper: T.() -> R) = when(this) {
  is JellyfinResult.Error.Detailed<*> -> this
  is JellyfinResult.Error.Generic -> this
  is JellyfinResult.Success -> JellyfinResult.Success(value.mapper())
}

/**
 * Transforms the success value using the given [mapper] function, which returns a new [JellyfinResult].
 *
 * Use this for chaining operations that may themselves fail. If this is an error, it is passed through unchanged.
 */
inline fun <T, R> JellyfinResult<T>.flatMapSuccessTo(mapper: T.() -> JellyfinResult<R>) = when(this) {
  is JellyfinResult.Error.Detailed<*> -> this
  is JellyfinResult.Error.Generic -> this
  is JellyfinResult.Success -> value.mapper()
}

/**
 * Executes the given [block] with the success value if this is a success, then returns this result.
 *
 * If [block] throws an exception, it is caught and converted to a [JellyfinResult.Error].
 * Errors are passed through unchanged without executing [block].
 *
 * @see doOnSuccess for a version that doesn't catch exceptions from [block]
 */
inline fun <T> JellyfinResult<T>.andThen(block: (T) -> Unit): JellyfinResult<T> = when(this) {
  is JellyfinResult.Error.Detailed<*> -> this
  is JellyfinResult.Error.Generic -> this
  is JellyfinResult.Success -> flatMapSuccessTo {
    val success = this

    runResult {
      block(success)
    }.mapSuccessTo { success }
  }
}

/**
 * Executes the given [block] with the success value if this is a success, then returns this result unchanged.
 *
 * Unlike [andThen], exceptions thrown by [block] are not caught.
 */
inline fun <T> JellyfinResult<T>.doOnSuccess(block: (T) -> Unit) = apply {
  successOrNull?.let(block)
}

/**
 * Executes the given [block] if this is an error, then returns this result unchanged.
 *
 * If the block throws an exception and this result has a [Throwable] as its error details,
 * the thrown exception will be added as a suppressed exception to the original throwable.
 *
 * If the block throws an exception and this result does not have a [Throwable] as its error details,
 * the exception will be swallowed.
 */
inline fun <T> JellyfinResult<T>.doOnError(block: (JellyfinResult.Error) -> Unit) = apply {
  if(this is JellyfinResult.Error) {
    val cause = throwableOrNull

    val suppressed = runResult { block(this) }.throwableOrNull

    if(cause != null && suppressed != null) {
      cause.addSuppressed(suppressed)
    }
  }
}

/**
 * Executes [block] and wraps the result in a [JellyfinResult.Success], or catches any exception
 * (except [CancellationException]) and wraps it in a [JellyfinResult.Error.Detailed].
 *
 * @param ephemeralPredicate determines whether the error should be marked as ephemeral (defaults to `true` for all errors)
 * @param block the operation to execute
 * @return [JellyfinResult.Success] with the block's result, or [JellyfinResult.Error.Detailed] with the caught exception
 */
inline fun <T> runResult(
  ephemeralPredicate: (Throwable) -> Boolean = ::defaultEphemeralPredicate,
  block: () -> T,
) = runCatching {
  JellyfinResult.Success(block())
}.getOrElse { error ->
  if(error is CancellationException) throw error

  JellyfinResult.Error.Detailed(
    details = error,
    message = error.message,
    isEphemeral = ephemeralPredicate(error),
  )
}

@PublishedApi
internal fun defaultEphemeralPredicate(error: Throwable) = when(error) {
  is IllegalArgumentException -> false
  is IllegalStateException -> false
  is RuntimeException -> false
  else -> true
}
