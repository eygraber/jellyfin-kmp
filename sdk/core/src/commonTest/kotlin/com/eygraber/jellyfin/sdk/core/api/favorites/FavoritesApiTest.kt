package com.eygraber.jellyfin.sdk.core.api.favorites

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

class FavoritesApiTest {
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
  fun addFavorite_returns_user_item_data() {
    val client = createApiClientWithMock(
      responseBody = """
      {
        "PlaybackPositionTicks": 0,
        "PlayCount": 3,
        "IsFavorite": true,
        "Played": true
      }
      """.trimIndent(),
    )

    val api = FavoritesApi(client)
    kotlinx.coroutines.test.runTest {
      val result = api.addFavorite(userId = "user-1", itemId = "item-1")
      result.isSuccess.shouldBeTrue()
      val data = result.getOrThrow()
      data.isFavorite shouldBe true
    }

    client.close()
  }

  @Test
  fun removeFavorite_returns_user_item_data() {
    val client = createApiClientWithMock(
      responseBody = """
      {
        "PlaybackPositionTicks": 0,
        "PlayCount": 3,
        "IsFavorite": false,
        "Played": true
      }
      """.trimIndent(),
    )

    val api = FavoritesApi(client)
    kotlinx.coroutines.test.runTest {
      val result = api.removeFavorite(userId = "user-1", itemId = "item-1")
      result.isSuccess.shouldBeTrue()
      val data = result.getOrThrow()
      data.isFavorite shouldBe false
    }

    client.close()
  }

  @Test
  fun favoritesApi_extension_returns_instance() {
    val client = createApiClientWithMock(responseBody = "{}")
    val api = client.favoritesApi
    api.shouldBeInstanceOf<FavoritesApi>()
    client.close()
  }
}
