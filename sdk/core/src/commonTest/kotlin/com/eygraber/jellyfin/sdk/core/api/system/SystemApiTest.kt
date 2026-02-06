package com.eygraber.jellyfin.sdk.core.api.system

import com.eygraber.jellyfin.sdk.core.ClientInfo
import com.eygraber.jellyfin.sdk.core.DeviceInfo
import com.eygraber.jellyfin.sdk.core.ServerInfo
import com.eygraber.jellyfin.sdk.core.api.JellyfinApiClient
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlin.test.Test

class SystemApiTest {
  private val clientInfo = ClientInfo(name = "TestClient", version = "1.0.0")
  private val deviceInfo = DeviceInfo(name = "TestDevice", id = "test-device-id")
  private val serverInfo = ServerInfo(baseUrl = "https://jellyfin.example.com")

  private fun createApiClientWithMock(
    responseBody: String,
    statusCode: HttpStatusCode = HttpStatusCode.OK,
  ): JellyfinApiClient {
    val mockEngine = MockEngine { _ ->
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
      httpClientEngine = mockEngine,
    )
  }

  @Test
  fun getPublicSystemInfo_returns_server_info() {
    val client = createApiClientWithMock(
      responseBody = """
      {
        "LocalAddress": "http://192.168.1.100:8096",
        "ServerName": "My Jellyfin",
        "Version": "10.9.0",
        "ProductName": "Jellyfin Server",
        "Id": "server-id-123",
        "StartupWizardCompleted": true
      }
      """.trimIndent(),
    )

    val api = SystemApi(client)
    kotlinx.coroutines.test.runTest {
      val result = api.getPublicSystemInfo()
      result.isSuccess.shouldBeTrue()
      val info = result.getOrThrow()
      info.serverName shouldBe "My Jellyfin"
      info.version shouldBe "10.9.0"
      info.id shouldBe "server-id-123"
      info.startupWizardCompleted shouldBe true
    }

    client.close()
  }

  @Test
  fun getSystemInfo_returns_full_info() {
    val client = createApiClientWithMock(
      responseBody = """
      {
        "LocalAddress": "http://192.168.1.100:8096",
        "ServerName": "My Jellyfin",
        "Version": "10.9.0",
        "ProductName": "Jellyfin Server",
        "OperatingSystem": "Linux",
        "Id": "server-id-123",
        "StartupWizardCompleted": true,
        "HasPendingRestart": false,
        "IsShuttingDown": false,
        "HasUpdateAvailable": true
      }
      """.trimIndent(),
    )

    val api = SystemApi(client)
    kotlinx.coroutines.test.runTest {
      val result = api.getSystemInfo()
      result.isSuccess.shouldBeTrue()
      val info = result.getOrThrow()
      info.serverName shouldBe "My Jellyfin"
      info.operatingSystem shouldBe "Linux"
      info.hasPendingRestart.shouldBeFalse()
      info.hasUpdateAvailable.shouldBeTrue()
    }

    client.close()
  }

  @Test
  fun getBrandingConfiguration_returns_branding() {
    val client = createApiClientWithMock(
      responseBody = """
      {
        "LoginDisclaimer": "Welcome to Jellyfin",
        "CustomCss": "",
        "SplashscreenEnabled": false
      }
      """.trimIndent(),
    )

    val api = SystemApi(client)
    kotlinx.coroutines.test.runTest {
      val result = api.getBrandingConfiguration()
      result.isSuccess.shouldBeTrue()
      val branding = result.getOrThrow()
      branding.loginDisclaimer shouldBe "Welcome to Jellyfin"
      branding.isSplashscreenEnabled.shouldBeFalse()
    }

    client.close()
  }

  @Test
  fun getPublicSystemInfo_handles_error() {
    val client = createApiClientWithMock(
      responseBody = """{"message": "Internal Server Error"}""",
      statusCode = HttpStatusCode.InternalServerError,
    )

    val api = SystemApi(client)
    kotlinx.coroutines.test.runTest {
      val result = api.getPublicSystemInfo()
      result.isFailure.shouldBeTrue()
      val error = result.errorOrNull()
      error.shouldBe(
        com.eygraber.jellyfin.sdk.core.JellyfinSdkError.Http(
          statusCode = 500,
          message = "Internal Server Error",
        ),
      )
    }

    client.close()
  }

  @Test
  fun ping_returns_response() {
    val mockEngine = MockEngine { _ ->
      respond(
        content = "Jellyfin Server",
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, ContentType.Text.Plain.toString()),
      )
    }
    val client = JellyfinApiClient(
      clientInfo = clientInfo,
      deviceInfo = deviceInfo,
      serverInfo = serverInfo,
      httpClientEngine = mockEngine,
    )

    val api = SystemApi(client)
    kotlinx.coroutines.test.runTest {
      val result = api.ping()
      result.isSuccess.shouldBeTrue()
      result.getOrNull() shouldBe "Jellyfin Server"
    }

    client.close()
  }

  @Test
  fun systemApi_extension_returns_instance() {
    val client = createApiClientWithMock(responseBody = "{}")
    val api = client.systemApi
    api.shouldBeInstanceOf<SystemApi>()
    client.close()
  }
}
