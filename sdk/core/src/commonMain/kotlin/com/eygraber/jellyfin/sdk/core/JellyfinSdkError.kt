package com.eygraber.jellyfin.sdk.core

import kotlinx.serialization.Serializable

sealed class JellyfinSdkError : Exception() {
  data class Http(
    val statusCode: Int,
    override val message: String,
  ) : JellyfinSdkError()

  data class Network(
    override val cause: Throwable,
  ) : JellyfinSdkError() {
    override val message: String get() = cause.message ?: "Network error"
  }

  data class Serialization(
    override val cause: Throwable,
  ) : JellyfinSdkError() {
    override val message: String get() = cause.message ?: "Serialization error"
  }

  data class Authentication(
    override val message: String = "Authentication required",
  ) : JellyfinSdkError()

  data class Unknown(
    override val cause: Throwable,
  ) : JellyfinSdkError() {
    override val message: String get() = cause.message ?: "Unknown error"
  }
}

@Serializable
data class JellyfinErrorBody(
  val message: String? = null,
)
