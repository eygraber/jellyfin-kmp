package com.eygraber.jellyfin.sdk.core.api.livetv

import com.eygraber.jellyfin.sdk.core.ClientInfo
import com.eygraber.jellyfin.sdk.core.DeviceInfo
import com.eygraber.jellyfin.sdk.core.ServerInfo
import com.eygraber.jellyfin.sdk.core.api.JellyfinApiClient
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

class LiveTvApiTest {
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
  fun getChannels_returns_channels() {
    val client = createApiClientWithMock(
      responseBody = """
      {
        "Items": [
          {
            "Name": "BBC One",
            "Id": "channel-1",
            "Type": "TvChannel"
          },
          {
            "Name": "CNN",
            "Id": "channel-2",
            "Type": "TvChannel"
          }
        ],
        "TotalRecordCount": 2
      }
      """.trimIndent(),
    )

    val api = LiveTvApi(client)
    kotlinx.coroutines.test.runTest {
      val result = api.getChannels(userId = "user-1")
      result.isSuccess.shouldBeTrue()
      val channels = result.getOrThrow()
      channels.totalRecordCount shouldBe 2
      channels.items.size shouldBe 2
      channels.items[0].name shouldBe "BBC One"
      channels.items[1].name shouldBe "CNN"
    }

    client.close()
  }

  @Test
  fun getPrograms_returns_programs() {
    val client = createApiClientWithMock(
      responseBody = """
      {
        "Items": [
          {
            "Name": "Evening News",
            "Id": "program-1",
            "Type": "Program"
          }
        ],
        "TotalRecordCount": 1
      }
      """.trimIndent(),
    )

    val api = LiveTvApi(client)
    kotlinx.coroutines.test.runTest {
      val result = api.getPrograms(userId = "user-1", isAiring = true)
      result.isSuccess.shouldBeTrue()
      val programs = result.getOrThrow()
      programs.totalRecordCount shouldBe 1
      programs.items[0].name shouldBe "Evening News"
    }

    client.close()
  }

  @Test
  fun getRecordings_returns_recordings() {
    val client = createApiClientWithMock(
      responseBody = """
      {
        "Items": [
          {
            "Name": "Recorded Show",
            "Id": "recording-1",
            "Type": "Recording"
          }
        ],
        "TotalRecordCount": 1
      }
      """.trimIndent(),
    )

    val api = LiveTvApi(client)
    kotlinx.coroutines.test.runTest {
      val result = api.getRecordings(userId = "user-1")
      result.isSuccess.shouldBeTrue()
      val recordings = result.getOrThrow()
      recordings.totalRecordCount shouldBe 1
      recordings.items[0].name shouldBe "Recorded Show"
    }

    client.close()
  }

  @Test
  fun getTimers_returns_timers() {
    val client = createApiClientWithMock(
      responseBody = """
      [
        {
          "Id": "timer-1",
          "ChannelId": "channel-1",
          "ChannelName": "BBC One",
          "ProgramId": "program-1",
          "Name": "Evening News",
          "Status": "InProgress"
        }
      ]
      """.trimIndent(),
    )

    val api = LiveTvApi(client)
    kotlinx.coroutines.test.runTest {
      val result = api.getTimers()
      result.isSuccess.shouldBeTrue()
      val timers = result.getOrThrow()
      timers.size shouldBe 1
      timers[0].id shouldBe "timer-1"
      timers[0].channelName shouldBe "BBC One"
      timers[0].status shouldBe "InProgress"
    }

    client.close()
  }

  @Test
  fun liveTvApi_extension_returns_instance() {
    val client = createApiClientWithMock(responseBody = "{}")
    val api = client.liveTvApi
    api.shouldBeInstanceOf<LiveTvApi>()
    client.close()
  }
}
