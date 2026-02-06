package com.eygraber.jellyfin.sdk.core.api.search

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

class SearchApiTest {
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
  fun getSearchHints_returns_results() {
    val client = createApiClientWithMock(
      responseBody = """
      {
        "SearchHints": [
          {
            "ItemId": "movie-1",
            "Id": "movie-1",
            "Name": "The Matrix",
            "Type": "Movie",
            "MediaType": "Video",
            "ProductionYear": 1999
          },
          {
            "ItemId": "series-1",
            "Id": "series-1",
            "Name": "The Matrix: Resurrections",
            "Type": "Movie",
            "MediaType": "Video",
            "ProductionYear": 2021
          }
        ],
        "TotalRecordCount": 2
      }
      """.trimIndent(),
    )

    val api = SearchApi(client)
    kotlinx.coroutines.test.runTest {
      val result = api.getSearchHints(searchTerm = "Matrix")
      result.isSuccess.shouldBeTrue()
      val hints = result.getOrThrow()
      hints.totalRecordCount shouldBe 2
      hints.searchHints.size shouldBe 2
      hints.searchHints[0].name shouldBe "The Matrix"
      hints.searchHints[0].type shouldBe "Movie"
      hints.searchHints[0].productionYear shouldBe 1999
      hints.searchHints[1].name shouldBe "The Matrix: Resurrections"
    }

    client.close()
  }

  @Test
  fun getSearchHints_with_empty_results() {
    val client = createApiClientWithMock(
      responseBody = """
      {
        "SearchHints": [],
        "TotalRecordCount": 0
      }
      """.trimIndent(),
    )

    val api = SearchApi(client)
    kotlinx.coroutines.test.runTest {
      val result = api.getSearchHints(searchTerm = "nonexistent")
      result.isSuccess.shouldBeTrue()
      val hints = result.getOrThrow()
      hints.totalRecordCount shouldBe 0
      hints.searchHints.size shouldBe 0
    }

    client.close()
  }

  @Test
  fun getSearchHints_with_music_results() {
    val client = createApiClientWithMock(
      responseBody = """
      {
        "SearchHints": [
          {
            "ItemId": "song-1",
            "Name": "Bohemian Rhapsody",
            "Type": "Audio",
            "MediaType": "Audio",
            "Album": "A Night at the Opera",
            "AlbumArtist": "Queen",
            "Artists": ["Queen"]
          }
        ],
        "TotalRecordCount": 1
      }
      """.trimIndent(),
    )

    val api = SearchApi(client)
    kotlinx.coroutines.test.runTest {
      val result = api.getSearchHints(searchTerm = "Bohemian")
      result.isSuccess.shouldBeTrue()
      val hints = result.getOrThrow()
      hints.searchHints[0].album shouldBe "A Night at the Opera"
      hints.searchHints[0].albumArtist shouldBe "Queen"
      hints.searchHints[0].artists shouldBe listOf("Queen")
    }

    client.close()
  }

  @Test
  fun searchApi_extension_returns_instance() {
    val client = createApiClientWithMock(responseBody = "{}")
    val api = client.searchApi
    api.shouldBeInstanceOf<SearchApi>()
    client.close()
  }
}
