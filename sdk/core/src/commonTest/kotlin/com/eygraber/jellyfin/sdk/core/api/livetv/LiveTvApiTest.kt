package com.eygraber.jellyfin.sdk.core.api.livetv

import com.eygraber.jellyfin.sdk.core.ClientInfo
import com.eygraber.jellyfin.sdk.core.DeviceInfo
import com.eygraber.jellyfin.sdk.core.ServerInfo
import com.eygraber.jellyfin.sdk.core.api.JellyfinApiClient
import com.eygraber.jellyfin.sdk.core.model.LiveTvTimerInfoDto
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
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
    runTest {
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
    runTest {
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
    runTest {
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
    runTest {
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
  fun getChannel_returns_single_channel() {
    val client = createApiClientWithMock(
      responseBody = """
      {
        "Id": "channel-1",
        "Name": "BBC One",
        "Type": "TvChannel",
        "ChannelNumber": "1"
      }
      """.trimIndent(),
    )

    val api = LiveTvApi(client)
    runTest {
      val result = api.getChannel(channelId = "channel-1", userId = "user-1")
      result.isSuccess.shouldBeTrue()
      val channel = result.getOrThrow()
      channel.id shouldBe "channel-1"
      channel.name shouldBe "BBC One"
      channel.channelNumber shouldBe "1"
    }

    client.close()
  }

  @Test
  fun getProgram_returns_single_program() {
    val client = createApiClientWithMock(
      responseBody = """
      {
        "Id": "program-1",
        "Name": "Evening News",
        "ChannelId": "channel-1",
        "StartDate": "2024-01-01T18:00:00Z",
        "EndDate": "2024-01-01T19:00:00Z"
      }
      """.trimIndent(),
    )

    val api = LiveTvApi(client)
    runTest {
      val result = api.getProgram(programId = "program-1", userId = "user-1")
      result.isSuccess.shouldBeTrue()
      val program = result.getOrThrow()
      program.id shouldBe "program-1"
      program.channelId shouldBe "channel-1"
      program.startDate shouldBe "2024-01-01T18:00:00Z"
      program.endDate shouldBe "2024-01-01T19:00:00Z"
    }

    client.close()
  }

  @Test
  fun getRecording_returns_single_recording() {
    val client = createApiClientWithMock(
      responseBody = """
      {
        "Id": "rec-1",
        "Name": "Recorded Show",
        "Status": "Completed"
      }
      """.trimIndent(),
    )

    val api = LiveTvApi(client)
    runTest {
      val result = api.getRecording(recordingId = "rec-1", userId = "user-1")
      result.isSuccess.shouldBeTrue()
      val recording = result.getOrThrow()
      recording.id shouldBe "rec-1"
      recording.status shouldBe "Completed"
    }

    client.close()
  }

  @Test
  fun deleteRecording_succeeds_on_no_content() {
    val client = createApiClientWithMock(
      responseBody = "",
      statusCode = HttpStatusCode.NoContent,
    )

    val api = LiveTvApi(client)
    runTest {
      val result = api.deleteRecording(recordingId = "rec-1")
      result.isSuccess.shouldBeTrue()
    }

    client.close()
  }

  @Test
  fun createTimer_succeeds_on_no_content() {
    val client = createApiClientWithMock(
      responseBody = "",
      statusCode = HttpStatusCode.NoContent,
    )

    val api = LiveTvApi(client)
    runTest {
      val result = api.createTimer(
        body = LiveTvTimerInfoDto(
          programId = "prog-1",
        ),
      )
      result.isSuccess.shouldBeTrue()
    }

    client.close()
  }

  @Test
  fun getSeriesTimers_returns_results() {
    val client = createApiClientWithMock(
      responseBody = """
      {
        "Items": [
          {
            "Id": "series-1",
            "Name": "Doctor Who",
            "RecordAnyChannel": true
          }
        ],
        "TotalRecordCount": 1
      }
      """.trimIndent(),
    )

    val api = LiveTvApi(client)
    runTest {
      val result = api.getSeriesTimers()
      result.isSuccess.shouldBeTrue()
      val timers = result.getOrThrow()
      timers.totalRecordCount shouldBe 1
      timers.items[0].id shouldBe "series-1"
      timers.items[0].recordAnyChannel.shouldBeTrue()
    }

    client.close()
  }

  @Test
  fun getGuideInfo_returns_date_range() {
    val client = createApiClientWithMock(
      responseBody = """
      {
        "StartDate": "2024-01-01T00:00:00Z",
        "EndDate": "2024-01-15T00:00:00Z"
      }
      """.trimIndent(),
    )

    val api = LiveTvApi(client)
    runTest {
      val result = api.getGuideInfo()
      result.isSuccess.shouldBeTrue()
      val info = result.getOrThrow()
      info.startDate shouldBe "2024-01-01T00:00:00Z"
      info.endDate shouldBe "2024-01-15T00:00:00Z"
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
