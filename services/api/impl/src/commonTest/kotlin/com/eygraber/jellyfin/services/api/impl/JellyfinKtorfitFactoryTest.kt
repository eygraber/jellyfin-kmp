package com.eygraber.jellyfin.services.api.impl

import com.eygraber.jellyfin.services.api.ApiConfig
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

class JellyfinKtorfitFactoryTest {
  private val testConfig = ApiConfig(
    baseUrl = "https://jellyfin.example.com",
    clientName = "TestClient",
    clientVersion = "1.0.0",
    deviceName = "TestDevice",
    deviceId = "test-device-123",
    connectTimeoutMs = 10_000L,
    requestTimeoutMs = 20_000L,
    socketTimeoutMs = 20_000L,
  )

  @Test
  fun factory_creates_ktorfit_instance() {
    val factory = JellyfinKtorfitFactory(apiConfig = testConfig)
    val ktorfit = factory.create()
    ktorfit shouldNotBe null
  }

  @Test
  fun factory_creates_httpClient() {
    val factory = JellyfinKtorfitFactory(apiConfig = testConfig)
    val client = factory.createHttpClient()
    client shouldNotBe null
    client.close()
  }

  @Test
  fun defaultJson_ignores_unknown_keys() {
    val json = JellyfinKtorfitFactory.defaultJson
    json.configuration.ignoreUnknownKeys shouldBe true
  }

  @Test
  fun defaultJson_is_lenient() {
    val json = JellyfinKtorfitFactory.defaultJson
    json.configuration.isLenient shouldBe true
  }

  @Test
  fun defaultJson_encodes_defaults() {
    val json = JellyfinKtorfitFactory.defaultJson
    json.configuration.encodeDefaults shouldBe true
  }

  @Test
  fun defaultJson_coerces_input_values() {
    val json = JellyfinKtorfitFactory.defaultJson
    json.configuration.coerceInputValues shouldBe true
  }

  @Test
  fun apiConfig_has_default_timeouts() {
    val config = ApiConfig(
      baseUrl = "https://example.com",
      clientName = "Test",
      clientVersion = "1.0",
      deviceName = "Device",
      deviceId = "id",
    )

    config.connectTimeoutMs shouldBe 15_000L
    config.requestTimeoutMs shouldBe 30_000L
    config.socketTimeoutMs shouldBe 30_000L
  }
}
