package com.eygraber.jellyfin.sdk.core.api.system

import com.eygraber.jellyfin.sdk.core.ClientInfo
import com.eygraber.jellyfin.sdk.core.DeviceInfo
import com.eygraber.jellyfin.sdk.core.ServerInfo
import com.eygraber.jellyfin.sdk.core.api.JellyfinApiClient
import com.eygraber.jellyfin.sdk.core.model.ServerConfigurationDto
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class SystemApiAdminTest {
  private val clientInfo = ClientInfo(name = "TestClient", version = "1.0.0")
  private val deviceInfo = DeviceInfo(name = "TestDevice", id = "test-device-id")
  private val serverInfo = ServerInfo(baseUrl = "https://jellyfin.example.com")

  private fun createApiClient(
    responseBody: String = "{}",
    statusCode: HttpStatusCode = HttpStatusCode.OK,
    onRequest: suspend (HttpRequestData) -> Unit = {},
  ): JellyfinApiClient {
    val engine = MockEngine { request ->
      onRequest(request)
      respond(
        content = responseBody,
        status = statusCode,
        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
      )
    }
    return JellyfinApiClient(
      clientInfo = clientInfo,
      deviceInfo = deviceInfo,
      serverInfo = serverInfo,
      httpClientEngine = engine,
    )
  }

  @Test
  fun getConfiguration_returns_dto() {
    val client = createApiClient(
      responseBody = """
      {
        "ServerName": "Home",
        "PreferredMetadataLanguage": "en",
        "MetadataCountryCode": "US",
        "MinResumePct": 5,
        "QuickConnectAvailable": true
      }
      """.trimIndent(),
    )

    val api = SystemApi(client)
    runTest {
      val result = api.getConfiguration()
      result.isSuccess.shouldBeTrue()
      val config = result.getOrThrow()
      config.serverName shouldBe "Home"
      config.preferredMetadataLanguage shouldBe "en"
      config.minResumePct shouldBe 5
      config.quickConnectAvailable shouldBe true
    }

    client.close()
  }

  @Test
  fun updateConfiguration_posts_dto() {
    var capturedMethod: HttpMethod? = null
    var capturedUrl: String? = null
    val client = createApiClient(
      responseBody = "",
      statusCode = HttpStatusCode.NoContent,
      onRequest = { req ->
        capturedMethod = req.method
        capturedUrl = req.url.toString()
      },
    )

    val api = SystemApi(client)
    runTest {
      val result = api.updateConfiguration(
        configuration = ServerConfigurationDto(serverName = "Updated"),
      )
      result.isSuccess.shouldBeTrue()
    }

    capturedMethod shouldBe HttpMethod.Post
    capturedUrl?.shouldContain("System/Configuration")
    client.close()
  }

  @Test
  fun restartServer_posts_to_restart_path() {
    var capturedUrl: String? = null
    val client = createApiClient(
      responseBody = "",
      statusCode = HttpStatusCode.NoContent,
      onRequest = { req -> capturedUrl = req.url.toString() },
    )

    val api = SystemApi(client)
    runTest {
      val result = api.restartServer()
      result.isSuccess.shouldBeTrue()
    }

    capturedUrl?.shouldContain("System/Restart")
    client.close()
  }

  @Test
  fun shutdownServer_posts_to_shutdown_path() {
    var capturedUrl: String? = null
    val client = createApiClient(
      responseBody = "",
      statusCode = HttpStatusCode.NoContent,
      onRequest = { req -> capturedUrl = req.url.toString() },
    )

    val api = SystemApi(client)
    runTest {
      val result = api.shutdownServer()
      result.isSuccess.shouldBeTrue()
    }

    capturedUrl?.shouldContain("System/Shutdown")
    client.close()
  }
}
