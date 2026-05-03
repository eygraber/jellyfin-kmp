package com.eygraber.jellyfin.sdk.core.api.activity

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
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ActivityLogApiTest {
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
  fun getEntries_returns_paginated_result() {
    val client = createApiClient(
      responseBody = """
      {
        "Items": [
          {
            "Id": 1,
            "Name": "Login",
            "Type": "SessionStarted",
            "Date": "2024-01-01T00:00:00Z",
            "UserId": "user-1",
            "Severity": "Information"
          },
          {
            "Id": 2,
            "Name": "Library Scan Complete",
            "Type": "TaskCompleted",
            "Date": "2024-01-01T01:00:00Z",
            "Severity": "Information"
          }
        ],
        "TotalRecordCount": 2,
        "StartIndex": 0
      }
      """.trimIndent(),
    )

    val api = ActivityLogApi(client)
    runTest {
      val result = api.getEntries(
        startIndex = 0,
        limit = 50,
        minDate = "2024-01-01T00:00:00Z",
        hasUserId = null,
      )
      result.isSuccess.shouldBeTrue()
      val page = result.getOrThrow()
      page.items shouldHaveSize 2
      page.totalRecordCount shouldBe 2
      page.items[0].id shouldBe 1L
      page.items[0].userId shouldBe "user-1"
      page.items[1].userId shouldBe null
    }

    client.close()
  }

  @Test
  fun getEntries_passes_query_params_to_url() {
    var capturedUrl: String? = null
    val client = createApiClient(
      responseBody = """{"Items": [], "TotalRecordCount": 0, "StartIndex": 0}""",
      onRequest = { req -> capturedUrl = req.url.toString() },
    )

    val api = ActivityLogApi(client)
    runTest {
      api.getEntries(
        startIndex = 100,
        limit = 25,
        minDate = "2024-01-01T00:00:00Z",
        hasUserId = true,
      )
    }

    val url = capturedUrl.orEmpty()
    url.shouldContain("startIndex=100")
    url.shouldContain("limit=25")
    url.shouldContain("hasUserId=true")
    client.close()
  }

  @Test
  fun activityLogApi_extension_returns_instance() {
    val client = createApiClient()
    client.activityLogApi.shouldBeInstanceOf<ActivityLogApi>()
    client.close()
  }
}
