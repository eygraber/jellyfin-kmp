package template.common

import kotlin.apply
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.coroutines.cancellation.CancellationException
import kotlin.getOrElse
import kotlin.let

sealed interface TemplateResult<out T> {
  data class Success<T>(val value: T) : TemplateResult<T> {
    companion object {
      @PublishedApi
      internal val Empty: TemplateResult<Unit> = Success(Unit)

      @Suppress("NOTHING_TO_INLINE")
      inline operator fun invoke(): TemplateResult<Unit> = Empty
    }
  }

  sealed interface Error : TemplateResult<Nothing> {
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
      internal val Empty: TemplateResult<Nothing> = Generic(message = null, isEphemeral = false)

      @Suppress("NOTHING_TO_INLINE")
      inline operator fun <T> invoke(): TemplateResult<T> = Empty

      operator fun <T> invoke(
        message: String?,
        isEphemeral: Boolean,
      ): TemplateResult<T> = Generic(message = message, isEphemeral = isEphemeral)
    }
  }
}

/**
 * Returns `true` if this result is a [TemplateResult.Success], `false` otherwise.
 *
 * Uses Kotlin contracts to smart-cast this result to [TemplateResult.Success] when the return value is `true`.
 */
@OptIn(ExperimentalContracts::class)
fun <T> TemplateResult<T>.isSuccess(): Boolean {
  contract {
    returns(true) implies (this@isSuccess is TemplateResult.Success<T>)
  }
  return this is TemplateResult.Success<T>
}

/**
 * Returns `true` if this result is a [TemplateResult.Error], `false` otherwise.
 *
 * Uses Kotlin contracts to smart-cast this result to [TemplateResult.Error] when the return value is `true`.
 */
@OptIn(ExperimentalContracts::class)
fun TemplateResult<*>.isError(): Boolean {
  contract {
    returns(true) implies (this@isError is TemplateResult.Error)
  }
  return this is TemplateResult.Error
}

/**
 * Returns the success [value][TemplateResult.Success.value] if this is a [TemplateResult.Success],
 * or `null` if this is an error.
 */
val <T> TemplateResult<T>.successOrNull
  get() = when(this) {
    is TemplateResult.Error -> null
    is TemplateResult.Success -> value
  }

/**
 * Returns the [Throwable] [details][TemplateResult.Error.Detailed.details] if this is a
 * [TemplateResult.Error.Detailed], and `details` is a `Throwable`, or `null` otherwise.
 */
val <T> TemplateResult<T>.throwableOrNull
  get() = when(this) {
    is TemplateResult.Error.Detailed<*> -> details as? Throwable
    is TemplateResult.Error.Generic -> null
    is TemplateResult.Success -> null
  }

/**
 * Returns the error [message][TemplateResult.Error.message] if this is a [TemplateResult.Error],
 * or `null` otherwise.
 */
val TemplateResult<*>.errorMessageOrNull
  get() = when(this) {
    is TemplateResult.Error -> message
    is TemplateResult.Success -> null
  }

/**
 * Returns the error [details][TemplateResult.Error.Detailed.details] if this is a [TemplateResult.Error.Detailed],
 * or `null` otherwise.
 */
val TemplateResult<*>.errorDetailOrNull
  get() = when(this) {
    is TemplateResult.Error.Detailed<*> -> details
    is TemplateResult.Error.Generic -> null
    is TemplateResult.Success -> null
  }

/**
 * Maps a successful result to [Unit], discarding the original success value.
 *
 * Errors are passed through unchanged.
 */
fun TemplateResult<*>.mapToUnit() = when(this) {
  is TemplateResult.Error.Detailed<*> -> this
  is TemplateResult.Error.Generic -> this
  is TemplateResult.Success -> TemplateResult.Success()
}

fun TemplateResult<TemplateResult<*>>.unwrapToUnit() = when(this) {
  is TemplateResult.Error -> this
  is TemplateResult.Success -> value.mapToUnit()
}

fun <T> TemplateResult<TemplateResult<T>>.unwrap() = when(this) {
  is TemplateResult.Error -> this
  is TemplateResult.Success -> value
}

/**
 * Transforms this result using the given [mapper] function, which receives this result as a receiver
 * and returns a new [TemplateResult].
 *
 * Unlike [flatMapSuccessTo], the mapper is always invoked regardless of success or error state.
 */
inline fun <T, R> TemplateResult<T>.flatMap(mapper: TemplateResult<T>.() -> TemplateResult<R>) = mapper()

/**
 * Transforms the success value using the given [mapper] function,
 * wrapping the result in a new [TemplateResult.Success].
 *
 * If this is an error, it is passed through unchanged.
 */
inline fun <T, R> TemplateResult<T>.mapSuccessTo(mapper: T.() -> R) = when(this) {
  is TemplateResult.Error.Detailed<*> -> this
  is TemplateResult.Error.Generic -> this
  is TemplateResult.Success -> TemplateResult.Success(value.mapper())
}

/**
 * Transforms the success value using the given [mapper] function, which returns a new [TemplateResult].
 *
 * Use this for chaining operations that may themselves fail. If this is an error, it is passed through unchanged.
 */
inline fun <T, R> TemplateResult<T>.flatMapSuccessTo(mapper: T.() -> TemplateResult<R>) = when(this) {
  is TemplateResult.Error.Detailed<*> -> this
  is TemplateResult.Error.Generic -> this
  is TemplateResult.Success -> value.mapper()
}

/**
 * Executes the given [block] with the success value if this is a success, then returns this result.
 *
 * If [block] throws an exception, it is caught and converted to a [TemplateResult.Error].
 * Errors are passed through unchanged without executing [block].
 *
 * @see doOnSuccess for a version that doesn't catch exceptions from [block]
 */
inline fun <T> TemplateResult<T>.andThen(block: (T) -> Unit): TemplateResult<T> = when(this) {
  is TemplateResult.Error.Detailed<*> -> this
  is TemplateResult.Error.Generic -> this
  is TemplateResult.Success -> flatMapSuccessTo {
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
inline fun <T> TemplateResult<T>.doOnSuccess(block: (T) -> Unit) = apply {
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
inline fun <T> TemplateResult<T>.doOnError(block: (TemplateResult.Error) -> Unit) = apply {
  if(this is TemplateResult.Error) {
    val cause = throwableOrNull

    val suppressed = runResult { block(this) }.throwableOrNull

    if(cause != null && suppressed != null) {
      cause.addSuppressed(suppressed)
    }
  }
}

/**
 * Executes [block] and wraps the result in a [TemplateResult.Success], or catches any exception
 * (except [CancellationException]) and wraps it in a [TemplateResult.Error.Detailed].
 *
 * @param ephemeralPredicate determines whether the error should be marked as ephemeral (defaults to `true` for all errors)
 * @param block the operation to execute
 * @return [TemplateResult.Success] with the block's result, or [TemplateResult.Error.Detailed] with the caught exception
 */
inline fun <T> runResult(
  ephemeralPredicate: (Throwable) -> Boolean = ::defaultEphemeralPredicate,
  block: () -> T,
) = runCatching {
  TemplateResult.Success(block())
}.getOrElse { error ->
  if(error is CancellationException) throw error

  TemplateResult.Error.Detailed(
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
