package com.eygraber.jellyfin.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlin.coroutines.cancellation.CancellationException

/**
 * Transforms each emitted [JellyfinResult.Success] value using the given [mapper].
 *
 * Error results pass through unchanged.
 */
fun <T, R> Flow<JellyfinResult<T>>.mapSuccess(
  mapper: (T) -> R,
): Flow<JellyfinResult<R>> = map { result ->
  result.mapSuccessTo { mapper(this) }
}

/**
 * Transforms each emitted [JellyfinResult.Success] value using the given [mapper]
 * that itself returns a [JellyfinResult].
 *
 * Error results pass through unchanged.
 */
fun <T, R> Flow<JellyfinResult<T>>.flatMapSuccess(
  mapper: (T) -> JellyfinResult<R>,
): Flow<JellyfinResult<R>> = map { result ->
  result.flatMapSuccessTo { mapper(this) }
}

/**
 * Filters the flow to only emit successful result values,
 * discarding errors.
 */
fun <T> Flow<JellyfinResult<T>>.filterSuccess(): Flow<T> =
  mapNotNull { result -> result.successOrNull }

/**
 * Filters the flow to only emit error results, discarding successes.
 */
fun <T> Flow<JellyfinResult<T>>.filterErrors(): Flow<JellyfinResult.Error> =
  mapNotNull { result -> result as? JellyfinResult.Error }

/**
 * Invokes [action] for each error emitted by this flow.
 *
 * The flow continues to emit all values unchanged.
 */
fun <T> Flow<JellyfinResult<T>>.onEachError(
  action: (JellyfinResult.Error) -> Unit,
): Flow<JellyfinResult<T>> = onEach { result ->
  if(result is JellyfinResult.Error) {
    action(result)
  }
}

/**
 * Invokes [action] for each success value emitted by this flow.
 *
 * The flow continues to emit all values unchanged.
 */
fun <T> Flow<JellyfinResult<T>>.onEachSuccess(
  action: (T) -> Unit,
): Flow<JellyfinResult<T>> = onEach { result ->
  if(result is JellyfinResult.Success) {
    action(result.value)
  }
}

/**
 * Catches exceptions in the upstream flow and converts them to [JellyfinResult.Error].
 *
 * [CancellationException] is always rethrown and never caught.
 */
fun <T> Flow<JellyfinResult<T>>.catchAsResult(): Flow<JellyfinResult<T>> =
  catch { error ->
    if(error is CancellationException) throw error
    emit(
      JellyfinResult.Error.Detailed(
        details = error,
        message = error.message,
        isEphemeral = true,
      ),
    )
  }

/**
 * Wraps each element of a plain [Flow] into a [JellyfinResult.Success],
 * catching any exceptions and converting them to [JellyfinResult.Error].
 */
fun <T> Flow<T>.asResultFlow(): Flow<JellyfinResult<T>> =
  map<T, JellyfinResult<T>> { JellyfinResult.Success(it) }
    .catchAsResult()
