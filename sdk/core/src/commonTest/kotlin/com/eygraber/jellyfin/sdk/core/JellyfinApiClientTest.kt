package com.eygraber.jellyfin.sdk.core

import com.eygraber.jellyfin.sdk.core.api.JellyfinApiClient
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlin.test.Test

class JellyfinApiClientTest {
  private val clientInfo = ClientInfo(name = "TestClient", version = "1.0.0")
  private val deviceInfo = DeviceInfo(name = "TestDevice", id = "test-device-id")
  private val serverInfo = ServerInfo(baseUrl = "https://jellyfin.example.com")

  @Test
  fun authorization_header_without_token() {
    val client = JellyfinApiClient(
      clientInfo = clientInfo,
      deviceInfo = deviceInfo,
      serverInfo = serverInfo,
    )

    val header = client.buildAuthorizationHeader()
    header shouldContain "Client=\"TestClient\""
    header shouldContain "Device=\"TestDevice\""
    header shouldContain "DeviceId=\"test-device-id\""
    header shouldContain "Version=\"1.0.0\""
    header shouldBe "MediaBrowser Client=\"TestClient\", Device=\"TestDevice\", " +
      "DeviceId=\"test-device-id\", Version=\"1.0.0\""

    client.close()
  }

  @Test
  fun authorization_header_with_token() {
    val client = JellyfinApiClient(
      clientInfo = clientInfo,
      deviceInfo = deviceInfo,
      serverInfo = serverInfo.copy(accessToken = "my-token"),
    )

    val header = client.buildAuthorizationHeader()
    header shouldContain "Token=\"my-token\""

    client.close()
  }

  @Test
  fun update_access_token() {
    val client = JellyfinApiClient(
      clientInfo = clientInfo,
      deviceInfo = deviceInfo,
      serverInfo = serverInfo,
    )

    client.serverInfo.accessToken shouldBe null

    client.updateAccessToken("new-token")
    client.serverInfo.accessToken shouldBe "new-token"

    client.updateAccessToken(null)
    client.serverInfo.accessToken shouldBe null

    client.close()
  }

  @Test
  fun update_user_id() {
    val client = JellyfinApiClient(
      clientInfo = clientInfo,
      deviceInfo = deviceInfo,
      serverInfo = serverInfo,
    )

    client.serverInfo.userId shouldBe null

    client.updateUserId("user-123")
    client.serverInfo.userId shouldBe "user-123"

    client.close()
  }

  @Test
  fun default_json_ignores_unknown_keys() {
    val json = JellyfinApiClient.defaultJson
    val result = json.decodeFromString<ServerTestModel>(
      """{"known":"value","unknown":"ignored"}""",
    )
    result.known shouldBe "value"
  }
}

@kotlinx.serialization.Serializable
private data class ServerTestModel(
  val known: String,
)
