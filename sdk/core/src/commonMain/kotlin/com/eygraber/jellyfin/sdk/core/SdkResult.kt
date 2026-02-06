package com.eygraber.jellyfin.sdk.core

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

sealed class SdkResult<out T> {
  data class Success<T>(val value: T) : SdkResult<T>()
  data class Failure(val error: JellyfinSdkError) : SdkResult<Nothing>()

  val isSuccess: Boolean get() = this is Success
  val isFailure: Boolean get() = this is Failure

  fun getOrNull(): T? = when(this) {
    is Success -> value
    is Failure -> null
  }

  fun errorOrNull(): JellyfinSdkError? = when(this) {
    is Success -> null
    is Failure -> error
  }

  fun getOrThrow(): T = when(this) {
    is Success -> value
    is Failure -> throw error
  }

  @OptIn(ExperimentalContracts::class)
  inline fun <R> map(transform: (T) -> R): SdkResult<R> {
    contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
    return when(this) {
      is Success -> Success(transform(value))
      is Failure -> this
    }
  }

  @OptIn(ExperimentalContracts::class)
  inline fun <R> flatMap(transform: (T) -> SdkResult<R>): SdkResult<R> {
    contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
    return when(this) {
      is Success -> transform(value)
      is Failure -> this
    }
  }

  @OptIn(ExperimentalContracts::class)
  inline fun onSuccess(action: (T) -> Unit): SdkResult<T> {
    contract { callsInPlace(action, InvocationKind.AT_MOST_ONCE) }
    if(this is Success) action(value)
    return this
  }

  @OptIn(ExperimentalContracts::class)
  inline fun onFailure(action: (JellyfinSdkError) -> Unit): SdkResult<T> {
    contract { callsInPlace(action, InvocationKind.AT_MOST_ONCE) }
    if(this is Failure) action(error)
    return this
  }
}
