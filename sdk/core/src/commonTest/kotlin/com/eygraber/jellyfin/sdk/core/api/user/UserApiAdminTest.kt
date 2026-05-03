package com.eygraber.jellyfin.sdk.core.api.user

import com.eygraber.jellyfin.sdk.core.ClientInfo
import com.eygraber.jellyfin.sdk.core.DeviceInfo
import com.eygraber.jellyfin.sdk.core.ServerInfo
import com.eygraber.jellyfin.sdk.core.api.JellyfinApiClient
import com.eygraber.jellyfin.sdk.core.model.UserPolicy
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.request.HttpRequestData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class UserApiAdminTest {
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
  fun getUsers_returns_list() {
    val client = createApiClient(
      responseBody = """
      [
        {"Name": "alice", "Id": "user-1", "HasPassword": true, "Policy": {"IsAdministrator": true}},
        {"Name": "bob", "Id": "user-2", "HasPassword": false}
      ]
      """.trimIndent(),
    )

    val api = UserApi(client)
    runTest {
      val result = api.getUsers(isHidden = false, isDisabled = false)
      result.isSuccess.shouldBeTrue()
      val users = result.getOrThrow()
      users shouldHaveSize 2
      users[0].name shouldBe "alice"
      users[0].policy?.isAdministrator shouldBe true
    }

    client.close()
  }

  @Test
  fun createUser_posts_request_body() {
    var capturedMethod: HttpMethod? = null
    var capturedUrl: String? = null
    val client = createApiClient(
      responseBody = """{"Name": "carol", "Id": "user-3"}""",
      onRequest = { req ->
        capturedMethod = req.method
        capturedUrl = req.url.toString()
      },
    )

    val api = UserApi(client)
    runTest {
      val result = api.createUser(name = "carol", password = "pw")
      result.isSuccess.shouldBeTrue()
      result.getOrThrow().id shouldBe "user-3"
    }

    capturedMethod shouldBe HttpMethod.Post
    capturedUrl?.shouldContain("Users/New")
    client.close()
  }

  @Test
  fun updateUserPolicy_posts_to_policy_path() {
    var capturedUrl: String? = null
    var capturedMethod: HttpMethod? = null
    val client = createApiClient(
      responseBody = "",
      statusCode = HttpStatusCode.NoContent,
      onRequest = { req ->
        capturedUrl = req.url.toString()
        capturedMethod = req.method
      },
    )

    val api = UserApi(client)
    runTest {
      val result = api.updateUserPolicy(
        userId = "user-1",
        policy = UserPolicy(isAdministrator = true),
      )
      result.isSuccess.shouldBeTrue()
    }

    capturedUrl?.shouldContain("Users/user-1/Policy")
    capturedMethod shouldBe HttpMethod.Post
    client.close()
  }

  @Test
  fun updateUserPassword_sends_reset_field() {
    var capturedBody: String? = null
    val client = createApiClient(
      responseBody = "",
      statusCode = HttpStatusCode.NoContent,
      onRequest = { req ->
        capturedBody = req.body.toByteArray().decodeToString()
      },
    )

    val api = UserApi(client)
    runTest {
      val result = api.updateUserPassword(
        userId = "user-1",
        newPassword = "",
        currentPassword = null,
        resetPassword = true,
      )
      result.isSuccess.shouldBeTrue()
    }

    val body = capturedBody.orEmpty()
    body.shouldContain("\"ResetPassword\":true")
    body.shouldContain("\"NewPw\":\"\"")
    client.close()
  }

  @Test
  fun deleteUser_uses_delete_method() {
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

    val api = UserApi(client)
    runTest {
      val result = api.deleteUser(userId = "user-1")
      result.isSuccess.shouldBeTrue()
    }

    capturedMethod shouldBe HttpMethod.Delete
    capturedUrl?.shouldContain("Users/user-1")
    client.close()
  }
}
