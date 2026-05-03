package com.eygraber.jellyfin.sdk.core.api.tasks

import com.eygraber.jellyfin.sdk.core.ClientInfo
import com.eygraber.jellyfin.sdk.core.DeviceInfo
import com.eygraber.jellyfin.sdk.core.ServerInfo
import com.eygraber.jellyfin.sdk.core.api.JellyfinApiClient
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
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

class ScheduledTasksApiTest {
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
  fun getTasks_returns_list() {
    val client = createApiClient(
      responseBody = """
      [
        {
          "Id": "task-1",
          "Name": "Refresh Library",
          "Category": "Library",
          "State": "Idle",
          "IsHidden": false
        },
        {
          "Id": "task-2",
          "Name": "Clean Cache",
          "State": "Running",
          "CurrentProgressPercentage": 42.5
        }
      ]
      """.trimIndent(),
    )

    val api = ScheduledTasksApi(client)
    runTest {
      val result = api.getTasks(isHidden = false, isEnabled = true)
      result.isSuccess.shouldBeTrue()
      val tasks = result.getOrThrow()
      tasks shouldHaveSize 2
      tasks[0].id shouldBe "task-1"
      tasks[0].state shouldBe "Idle"
      tasks[1].state shouldBe "Running"
      tasks[1].currentProgressPercentage shouldBe 42.5
    }

    client.close()
  }

  @Test
  fun startTask_posts_to_running_path() {
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

    val api = ScheduledTasksApi(client)
    runTest {
      val result = api.startTask(taskId = "task-1")
      result.isSuccess.shouldBeTrue()
    }

    capturedMethod shouldBe HttpMethod.Post
    capturedUrl?.shouldContain("ScheduledTasks/Running/task-1")
    client.close()
  }

  @Test
  fun stopTask_uses_delete_method() {
    var capturedMethod: HttpMethod? = null
    val client = createApiClient(
      responseBody = "",
      statusCode = HttpStatusCode.NoContent,
      onRequest = { req -> capturedMethod = req.method },
    )

    val api = ScheduledTasksApi(client)
    runTest {
      val result = api.stopTask(taskId = "task-1")
      result.isSuccess.shouldBeTrue()
    }

    capturedMethod shouldBe HttpMethod.Delete
    client.close()
  }

  @Test
  fun scheduledTasksApi_extension_returns_instance() {
    val client = createApiClient()
    client.scheduledTasksApi.shouldBeInstanceOf<ScheduledTasksApi>()
    client.close()
  }
}
