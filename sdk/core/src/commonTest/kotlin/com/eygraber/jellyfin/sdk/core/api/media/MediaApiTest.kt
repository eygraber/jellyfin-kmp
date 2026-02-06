package com.eygraber.jellyfin.sdk.core.api.media

import com.eygraber.jellyfin.sdk.core.ClientInfo
import com.eygraber.jellyfin.sdk.core.DeviceInfo
import com.eygraber.jellyfin.sdk.core.ServerInfo
import com.eygraber.jellyfin.sdk.core.api.JellyfinApiClient
import com.eygraber.jellyfin.sdk.core.model.PlaybackStartInfo
import com.eygraber.jellyfin.sdk.core.model.PlaybackStopInfo
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlin.test.Test

class MediaApiTest {
  private val clientInfo = ClientInfo(name = "TestClient", version = "1.0.0")
  private val deviceInfo = DeviceInfo(name = "TestDevice", id = "test-device-id")
  private val serverInfo = ServerInfo(
    baseUrl = "https://jellyfin.example.com",
    accessToken = "test-token",
    userId = "user-1",
  )

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
  fun getPlaybackInfo_returns_playback_info() {
    val client = createApiClientWithMock(
      responseBody = """
      {
        "MediaSources": [
          {
            "Id": "source-1",
            "Name": "1080p",
            "Container": "mkv",
            "Size": 5000000000,
            "Bitrate": 8000000,
            "SupportsDirectStream": true,
            "SupportsDirectPlay": true,
            "MediaStreams": [
              {
                "Codec": "h264",
                "Type": "Video",
                "Index": 0,
                "Width": 1920,
                "Height": 1080,
                "IsDefault": true
              },
              {
                "Codec": "aac",
                "Type": "Audio",
                "Index": 1,
                "Language": "eng",
                "IsDefault": true
              }
            ]
          }
        ],
        "PlaySessionId": "session-abc"
      }
      """.trimIndent(),
    )

    val api = MediaApi(client)
    kotlinx.coroutines.test.runTest {
      val result = api.getPlaybackInfo(itemId = "item-1", userId = "user-1")
      result.isSuccess.shouldBeTrue()
      val info = result.getOrThrow()
      info.playSessionId shouldBe "session-abc"
      info.mediaSources.size shouldBe 1
      info.mediaSources[0].id shouldBe "source-1"
      info.mediaSources[0].container shouldBe "mkv"
      info.mediaSources[0].supportsDirectPlay shouldBe true
      info.mediaSources[0].mediaStreams.size shouldBe 2
      info.mediaSources[0].mediaStreams[0].codec shouldBe "h264"
      info.mediaSources[0].mediaStreams[0].type shouldBe "Video"
      info.mediaSources[0].mediaStreams[1].codec shouldBe "aac"
      info.mediaSources[0].mediaStreams[1].language shouldBe "eng"
    }

    client.close()
  }

  @Test
  fun reportPlaybackStart_returns_success() {
    val client = createApiClientWithMock(responseBody = "")

    val api = MediaApi(client)
    kotlinx.coroutines.test.runTest {
      val result = api.reportPlaybackStart(
        info = PlaybackStartInfo(
          itemId = "item-1",
          mediaSourceId = "source-1",
          playSessionId = "session-abc",
          canSeek = true,
          playMethod = "DirectPlay",
        ),
      )
      result.isSuccess.shouldBeTrue()
    }

    client.close()
  }

  @Test
  fun reportPlaybackStopped_returns_success() {
    val client = createApiClientWithMock(responseBody = "")

    val api = MediaApi(client)
    kotlinx.coroutines.test.runTest {
      val result = api.reportPlaybackStopped(
        info = PlaybackStopInfo(
          itemId = "item-1",
          playSessionId = "session-abc",
          positionTicks = 50_000_000L,
        ),
      )
      result.isSuccess.shouldBeTrue()
    }

    client.close()
  }

  @Test
  fun markPlayed_returns_user_item_data() {
    val client = createApiClientWithMock(
      responseBody = """
      {
        "PlaybackPositionTicks": 0,
        "PlayCount": 1,
        "IsFavorite": false,
        "Played": true,
        "PlayedPercentage": 100.0
      }
      """.trimIndent(),
    )

    val api = MediaApi(client)
    kotlinx.coroutines.test.runTest {
      val result = api.markPlayed(userId = "user-1", itemId = "item-1")
      result.isSuccess.shouldBeTrue()
      val data = result.getOrThrow()
      data.played shouldBe true
      data.playedPercentage shouldBe 100.0
    }

    client.close()
  }

  @Test
  fun markUnplayed_returns_user_item_data() {
    val client = createApiClientWithMock(
      responseBody = """
      {
        "PlaybackPositionTicks": 0,
        "PlayCount": 0,
        "IsFavorite": false,
        "Played": false
      }
      """.trimIndent(),
    )

    val api = MediaApi(client)
    kotlinx.coroutines.test.runTest {
      val result = api.markUnplayed(userId = "user-1", itemId = "item-1")
      result.isSuccess.shouldBeTrue()
      val data = result.getOrThrow()
      data.played shouldBe false
    }

    client.close()
  }

  @Test
  fun getStreamUrl_generates_correct_url() {
    val client = createApiClientWithMock(responseBody = "{}")
    val api = MediaApi(client)

    val url = api.getStreamUrl(
      itemId = "item-123",
      mediaSourceId = "source-1",
      container = "mp4",
      videoCodec = "h264",
      audioCodec = "aac",
      maxWidth = 1920,
      maxHeight = 1080,
    )

    url shouldContain "jellyfin.example.com"
    url shouldContain "Videos/item-123/stream.mp4"
    url shouldContain "static=true"
    url shouldContain "mediaSourceId=source-1"
    url shouldContain "videoCodec=h264"
    url shouldContain "audioCodec=aac"
    url shouldContain "maxWidth=1920"
    url shouldContain "maxHeight=1080"
    url shouldContain "api_key=test-token"

    client.close()
  }

  @Test
  fun getStreamUrl_without_container() {
    val client = createApiClientWithMock(responseBody = "{}")
    val api = MediaApi(client)

    val url = api.getStreamUrl(itemId = "item-123")

    url shouldContain "Videos/item-123/stream?"
    url shouldContain "static=true"

    client.close()
  }

  @Test
  fun getAudioStreamUrl_generates_correct_url() {
    val client = createApiClientWithMock(responseBody = "{}")
    val api = MediaApi(client)

    val url = api.getAudioStreamUrl(
      itemId = "audio-1",
      mediaSourceId = "source-1",
      container = "mp3",
      maxBitrate = 320_000,
    )

    url shouldContain "jellyfin.example.com"
    url shouldContain "Audio/audio-1/universal"
    url shouldContain "mediaSourceId=source-1"
    url shouldContain "container=mp3"
    url shouldContain "maxStreamingBitrate=320000"
    url shouldContain "api_key=test-token"
    url shouldContain "userId=user-1"

    client.close()
  }

  @Test
  fun mediaApi_extension_returns_instance() {
    val client = createApiClientWithMock(responseBody = "{}")
    val api = client.mediaApi
    api.shouldBeInstanceOf<MediaApi>()
    client.close()
  }
}
