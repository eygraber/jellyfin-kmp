package com.eygraber.jellyfin.sdk.core.api.user

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

class UserApiTest {
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
  fun authenticateByName_returns_auth_result() {
    val client = createApiClientWithMock(
      responseBody = """
      {
        "User": {
          "Name": "testuser",
          "Id": "user-123",
          "HasPassword": true
        },
        "AccessToken": "access-token-abc",
        "ServerId": "server-456"
      }
      """.trimIndent(),
    )

    val api = UserApi(client)
    kotlinx.coroutines.test.runTest {
      val result = api.authenticateByName(username = "testuser", password = "password")
      result.isSuccess.shouldBeTrue()
      val auth = result.getOrThrow()
      auth.accessToken shouldBe "access-token-abc"
      auth.serverId shouldBe "server-456"
      auth.user?.name shouldBe "testuser"
      auth.user?.id shouldBe "user-123"
    }

    client.close()
  }

  @Test
  fun getCurrentUser_returns_user() {
    val client = createApiClientWithMock(
      responseBody = """
      {
        "Name": "testuser",
        "Id": "user-123",
        "HasPassword": true,
        "Policy": {
          "IsAdministrator": true
        }
      }
      """.trimIndent(),
    )

    val api = UserApi(client)
    kotlinx.coroutines.test.runTest {
      val result = api.getCurrentUser()
      result.isSuccess.shouldBeTrue()
      val user = result.getOrThrow()
      user.name shouldBe "testuser"
      user.id shouldBe "user-123"
      user.policy?.isAdministrator shouldBe true
    }

    client.close()
  }

  @Test
  fun getPublicUsers_returns_list() {
    val client = createApiClientWithMock(
      responseBody = """
      [
        {"Name": "user1", "Id": "id-1", "HasPassword": true},
        {"Name": "user2", "Id": "id-2", "HasPassword": false}
      ]
      """.trimIndent(),
    )

    val api = UserApi(client)
    kotlinx.coroutines.test.runTest {
      val result = api.getPublicUsers()
      result.isSuccess.shouldBeTrue()
      val users = result.getOrThrow()
      users.size shouldBe 2
      users[0].name shouldBe "user1"
      users[1].hasPassword.shouldBeFalse()
    }

    client.close()
  }

  @Test
  fun authenticateByName_handles_unauthorized() {
    val client = createApiClientWithMock(
      responseBody = """{"message": "Invalid username or password"}""",
      statusCode = HttpStatusCode.Unauthorized,
    )

    val api = UserApi(client)
    kotlinx.coroutines.test.runTest {
      val result = api.authenticateByName(username = "bad", password = "wrong")
      result.isFailure.shouldBeTrue()
      val error = result.errorOrNull()
      error.shouldBeInstanceOf<com.eygraber.jellyfin.sdk.core.JellyfinSdkError.Http>()
        .statusCode shouldBe 401
    }

    client.close()
  }

  @Test
  fun initiateQuickConnect_returns_result() {
    val client = createApiClientWithMock(
      responseBody = """
      {
        "Authenticated": false,
        "Secret": "quick-secret-123",
        "Code": "1234"
      }
      """.trimIndent(),
    )

    val api = UserApi(client)
    kotlinx.coroutines.test.runTest {
      val result = api.initiateQuickConnect()
      result.isSuccess.shouldBeTrue()
      val quickConnect = result.getOrThrow()
      quickConnect.authenticated.shouldBeFalse()
      quickConnect.secret shouldBe "quick-secret-123"
      quickConnect.code shouldBe "1234"
    }

    client.close()
  }

  @Test
  fun userApi_extension_returns_instance() {
    val client = createApiClientWithMock(responseBody = "{}")
    val api = client.userApi
    api.shouldBeInstanceOf<UserApi>()
    client.close()
  }
}
