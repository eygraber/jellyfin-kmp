package com.eygraber.jellyfin.sdk.core.api

import com.eygraber.jellyfin.sdk.core.ClientInfo
import com.eygraber.jellyfin.sdk.core.DeviceInfo
import com.eygraber.jellyfin.sdk.core.JellyfinErrorBody
import com.eygraber.jellyfin.sdk.core.JellyfinSdkError
import com.eygraber.jellyfin.sdk.core.SdkResult
import com.eygraber.jellyfin.sdk.core.ServerInfo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.coroutines.cancellation.CancellationException

class JellyfinApiClient(
  private val clientInfo: ClientInfo,
  private val deviceInfo: DeviceInfo,
  serverInfo: ServerInfo,
  json: Json = defaultJson,
  httpClientEngine: io.ktor.client.engine.HttpClientEngine? = null,
) {
  var serverInfo: ServerInfo = serverInfo
    private set

  val json: Json = json

  val httpClient: HttpClient = createHttpClient(httpClientEngine, json)

  fun updateServerInfo(serverInfo: ServerInfo) {
    this.serverInfo = serverInfo
  }

  fun updateAccessToken(accessToken: String?) {
    serverInfo = serverInfo.copy(accessToken = accessToken)
  }

  fun updateUserId(userId: String?) {
    serverInfo = serverInfo.copy(userId = userId)
  }

  suspend inline fun <reified T> request(
    crossinline block: HttpRequestBuilder.() -> Unit,
  ): SdkResult<T> = safeApiCall {
    httpClient.request {
      header(key = HttpHeaders.Authorization, value = buildAuthorizationHeader())
      block()
    }
  }

  fun buildAuthorizationHeader(): String = buildString {
    append("MediaBrowser ")
    append("Client=\"${clientInfo.name}\", ")
    append("Device=\"${deviceInfo.name}\", ")
    append("DeviceId=\"${deviceInfo.id}\", ")
    append("Version=\"${clientInfo.version}\"")
    serverInfo.accessToken?.let { token ->
      append(", Token=\"$token\"")
    }
  }

  fun close() {
    httpClient.close()
  }

  private fun createHttpClient(
    engine: io.ktor.client.engine.HttpClientEngine?,
    json: Json,
  ): HttpClient {
    val config: io.ktor.client.HttpClientConfig<*>.() -> Unit = {
      install(ContentNegotiation) {
        json(json)
      }

      install(Logging) {
        level = LogLevel.NONE
      }

      defaultRequest {
        url(serverInfo.baseUrl.trimEnd('/') + "/")
        contentType(ContentType.Application.Json)
      }

      HttpResponseValidator {
        validateResponse { response ->
          if(!response.status.isSuccess()) {
            val errorBody = try {
              response.body<JellyfinErrorBody>()
            }
            catch(cancellation: CancellationException) {
              throw cancellation
            }
            catch(@Suppress("TooGenericExceptionCaught") _: Exception) {
              null
            }

            throw JellyfinSdkError.Http(
              statusCode = response.status.value,
              message = errorBody?.message ?: response.status.description,
            )
          }
        }
      }

      expectSuccess = false
    }

    return if(engine != null) {
      HttpClient(engine, config)
    }
    else {
      HttpClient(config)
    }
  }

  companion object {
    val defaultJson: Json = Json {
      ignoreUnknownKeys = true
      isLenient = true
      encodeDefaults = true
      coerceInputValues = true
    }
  }
}

suspend inline fun <reified T> safeApiCall(
  crossinline call: suspend () -> HttpResponse,
): SdkResult<T> = try {
  val response = call()
  if(response.status.isSuccess()) {
    val body: T = response.body()
    SdkResult.Success(body)
  }
  else {
    val errorBody = try {
      response.body<JellyfinErrorBody>()
    }
    catch(cancellation: CancellationException) {
      throw cancellation
    }
    catch(@Suppress("TooGenericExceptionCaught") _: Exception) {
      null
    }
    SdkResult.Failure(
      JellyfinSdkError.Http(
        statusCode = response.status.value,
        message = errorBody?.message ?: response.bodyAsText(),
      ),
    )
  }
}
catch(error: CancellationException) {
  throw error
}
catch(error: JellyfinSdkError) {
  SdkResult.Failure(error)
}
catch(error: SerializationException) {
  SdkResult.Failure(JellyfinSdkError.Serialization(error))
}
catch(@Suppress("TooGenericExceptionCaught") error: Exception) {
  SdkResult.Failure(JellyfinSdkError.Network(error))
}
