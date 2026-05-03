package com.eygraber.jellyfin.sdk.core.api.library

import com.eygraber.jellyfin.sdk.core.ClientInfo
import com.eygraber.jellyfin.sdk.core.DeviceInfo
import com.eygraber.jellyfin.sdk.core.ServerInfo
import com.eygraber.jellyfin.sdk.core.api.JellyfinApiClient
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
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

class LibraryApiAdminTest {
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
  fun getVirtualFolders_returns_list() {
    val client = createApiClient(
      responseBody = """
      [
        {
          "Name": "Movies",
          "ItemId": "lib-1",
          "CollectionType": "movies",
          "Locations": ["/media/movies"]
        }
      ]
      """.trimIndent(),
    )

    val api = LibraryApi(client)
    runTest {
      val result = api.getVirtualFolders()
      result.isSuccess.shouldBeTrue()
      val folders = result.getOrThrow()
      folders shouldHaveSize 1
      folders[0].name shouldBe "Movies"
      folders[0].itemId shouldBe "lib-1"
      folders[0].collectionType shouldBe "movies"
      folders[0].locations shouldBe listOf("/media/movies")
    }

    client.close()
  }

  @Test
  fun refreshLibrary_posts_to_refresh_path() {
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

    val api = LibraryApi(client)
    runTest {
      val result = api.refreshLibrary()
      result.isSuccess.shouldBeTrue()
    }

    capturedMethod shouldBe HttpMethod.Post
    capturedUrl?.shouldContain("Library/Refresh")
    client.close()
  }
}
