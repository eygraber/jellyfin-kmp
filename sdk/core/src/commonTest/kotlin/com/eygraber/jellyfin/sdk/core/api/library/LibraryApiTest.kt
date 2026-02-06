package com.eygraber.jellyfin.sdk.core.api.library

import com.eygraber.jellyfin.sdk.core.ClientInfo
import com.eygraber.jellyfin.sdk.core.DeviceInfo
import com.eygraber.jellyfin.sdk.core.ServerInfo
import com.eygraber.jellyfin.sdk.core.api.JellyfinApiClient
import com.eygraber.jellyfin.sdk.core.model.ImageType
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

class LibraryApiTest {
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
  fun getItems_returns_items_result() {
    val client = createApiClientWithMock(
      responseBody = """
      {
        "Items": [
          {
            "Name": "Test Movie",
            "Id": "movie-1",
            "Type": "Movie",
            "ProductionYear": 2024,
            "CommunityRating": 8.5
          }
        ],
        "TotalRecordCount": 1,
        "StartIndex": 0
      }
      """.trimIndent(),
    )

    val api = LibraryApi(client)
    kotlinx.coroutines.test.runTest {
      val result = api.getItems(
        userId = "user-1",
        includeItemTypes = listOf("Movie"),
        sortBy = listOf("SortName"),
        limit = 10,
      )
      result.isSuccess.shouldBeTrue()
      val items = result.getOrThrow()
      items.totalRecordCount shouldBe 1
      items.items.size shouldBe 1
      items.items[0].name shouldBe "Test Movie"
      items.items[0].type shouldBe "Movie"
      items.items[0].productionYear shouldBe 2024
    }

    client.close()
  }

  @Test
  fun getUserViews_returns_library_views() {
    val client = createApiClientWithMock(
      responseBody = """
      {
        "Items": [
          {"Name": "Movies", "Id": "lib-1", "CollectionType": "movies"},
          {"Name": "TV Shows", "Id": "lib-2", "CollectionType": "tvshows"},
          {"Name": "Music", "Id": "lib-3", "CollectionType": "music"}
        ],
        "TotalRecordCount": 3
      }
      """.trimIndent(),
    )

    val api = LibraryApi(client)
    kotlinx.coroutines.test.runTest {
      val result = api.getUserViews(userId = "user-1")
      result.isSuccess.shouldBeTrue()
      val views = result.getOrThrow()
      views.items.size shouldBe 3
      views.items[0].collectionType shouldBe "movies"
      views.items[1].collectionType shouldBe "tvshows"
    }

    client.close()
  }

  @Test
  fun getLatestItems_returns_list() {
    val client = createApiClientWithMock(
      responseBody = """
      [
        {"Name": "New Movie", "Id": "new-1", "Type": "Movie"},
        {"Name": "New Episode", "Id": "new-2", "Type": "Episode"}
      ]
      """.trimIndent(),
    )

    val api = LibraryApi(client)
    kotlinx.coroutines.test.runTest {
      val result = api.getLatestItems(userId = "user-1", limit = 10)
      result.isSuccess.shouldBeTrue()
      val items = result.getOrThrow()
      items.size shouldBe 2
      items[0].name shouldBe "New Movie"
    }

    client.close()
  }

  @Test
  fun getResumeItems_returns_in_progress() {
    val client = createApiClientWithMock(
      responseBody = """
      {
        "Items": [
          {
            "Name": "Half-Watched Movie",
            "Id": "resume-1",
            "UserData": {
              "PlaybackPositionTicks": 50000000,
              "PlayedPercentage": 50.0,
              "Played": false
            }
          }
        ],
        "TotalRecordCount": 1
      }
      """.trimIndent(),
    )

    val api = LibraryApi(client)
    kotlinx.coroutines.test.runTest {
      val result = api.getResumeItems(userId = "user-1")
      result.isSuccess.shouldBeTrue()
      val items = result.getOrThrow()
      items.items[0].userData?.playedPercentage shouldBe 50.0
    }

    client.close()
  }

  @Test
  fun getImageUrl_generates_correct_url() {
    val client = createApiClientWithMock(responseBody = "{}")
    val api = LibraryApi(client)

    val url = api.getImageUrl(
      itemId = "item-123",
      imageType = ImageType.Primary,
      maxWidth = 300,
      maxHeight = 450,
      tag = "abc123",
    )

    url shouldContain "jellyfin.example.com"
    url shouldContain "Items/item-123/Images/Primary"
    url shouldContain "maxWidth=300"
    url shouldContain "maxHeight=450"
    url shouldContain "tag=abc123"

    client.close()
  }

  @Test
  fun getImageUrl_with_backdrop_index() {
    val client = createApiClientWithMock(responseBody = "{}")
    val api = LibraryApi(client)

    val url = api.getImageUrl(
      itemId = "item-123",
      imageType = ImageType.Backdrop,
      imageIndex = 2,
    )

    url shouldContain "Images/Backdrop/2"

    client.close()
  }

  @Test
  fun libraryApi_extension_returns_instance() {
    val client = createApiClientWithMock(responseBody = "{}")
    val api = client.libraryApi
    api.shouldBeInstanceOf<LibraryApi>()
    client.close()
  }
}
