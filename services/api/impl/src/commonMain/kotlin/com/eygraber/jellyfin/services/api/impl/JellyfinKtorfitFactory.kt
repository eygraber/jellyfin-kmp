package com.eygraber.jellyfin.services.api.impl

import com.eygraber.jellyfin.services.api.ApiConfig
import com.eygraber.jellyfin.services.api.AuthTokenProvider
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Factory for creating configured [Ktorfit] instances.
 *
 * Configures the underlying Ktor HTTP client with:
 * - Platform-specific engine (OkHttp, Darwin, Java, Js)
 * - JSON serialization via kotlinx.serialization
 * - Timeout configuration
 * - MediaBrowser authorization header
 * - Request/response logging
 */
class JellyfinKtorfitFactory(
  private val apiConfig: ApiConfig,
  @Suppress("UnusedPrivateProperty")
  private val authTokenProvider: AuthTokenProvider? = null,
  private val json: Json = defaultJson,
) {
  /**
   * Creates a new [Ktorfit] instance configured for the Jellyfin server.
   */
  fun create(): Ktorfit = Ktorfit.Builder()
    .baseUrl(apiConfig.baseUrl.trimEnd('/') + "/")
    .httpClient(createHttpClient())
    .build()

  @Suppress("LongMethod")
  internal fun createHttpClient(): HttpClient = HttpClient(platformHttpEngineFactory()) {
    install(ContentNegotiation) {
      json(json)
    }

    install(HttpTimeout) {
      connectTimeoutMillis = apiConfig.connectTimeoutMs
      requestTimeoutMillis = apiConfig.requestTimeoutMs
      socketTimeoutMillis = apiConfig.socketTimeoutMs
    }

    install(Logging) {
      level = LogLevel.HEADERS
    }

    defaultRequest {
      contentType(ContentType.Application.Json)

      header(
        key = "X-Emby-Authorization",
        value = buildMediaBrowserHeader(),
      )
    }
  }

  private fun buildMediaBrowserHeader(): String = buildString {
    append("MediaBrowser ")
    append("Client=\"${apiConfig.clientName}\"")
    append(", Device=\"${apiConfig.deviceName}\"")
    append(", DeviceId=\"${apiConfig.deviceId}\"")
    append(", Version=\"${apiConfig.clientVersion}\"")
  }

  companion object {
    val defaultJson = Json {
      ignoreUnknownKeys = true
      isLenient = true
      encodeDefaults = true
      coerceInputValues = true
    }
  }
}
