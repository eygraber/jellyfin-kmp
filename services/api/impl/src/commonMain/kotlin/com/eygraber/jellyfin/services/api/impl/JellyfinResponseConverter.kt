package com.eygraber.jellyfin.services.api.impl

import com.eygraber.jellyfin.services.api.JellyfinResponse
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import kotlin.coroutines.cancellation.CancellationException

/**
 * Converts a Ktor [HttpResponse] into a [JellyfinResponse].
 *
 * Successful responses (2xx status codes) are parsed into the target type.
 * Error responses are wrapped in [JellyfinResponse.Error] with status information.
 */
@Suppress("SuspendFunWithCoroutineScopeReceiver")
suspend inline fun <reified T> HttpResponse.toJellyfinResponse(): JellyfinResponse<T> =
  try {
    val statusCode = status.value
    if(statusCode in 200..299) {
      JellyfinResponse.Success(
        body = body(),
        statusCode = statusCode,
      )
    }
    else {
      JellyfinResponse.Error(
        statusCode = statusCode,
        message = "HTTP $statusCode",
      )
    }
  }
  catch(cancellation: CancellationException) {
    throw cancellation
  }
  catch(@Suppress("TooGenericExceptionCaught") error: Exception) {
    JellyfinResponse.Error(
      message = error.message,
      cause = error,
    )
  }
