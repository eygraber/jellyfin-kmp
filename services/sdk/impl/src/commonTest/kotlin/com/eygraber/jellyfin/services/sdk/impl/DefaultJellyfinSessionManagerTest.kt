package com.eygraber.jellyfin.services.sdk.impl

import com.eygraber.jellyfin.services.logging.JellyfinLogger
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.BeforeTest
import kotlin.test.Test

class DefaultJellyfinSessionManagerTest {
  private val logger = object : JellyfinLogger {
    override fun verbose(tag: String, message: String) {}
    override fun debug(tag: String, message: String) {}
    override fun info(tag: String, message: String) {}
    override fun warn(tag: String, message: String, throwable: Throwable?) {}
    override fun error(tag: String, message: String, throwable: Throwable?) {}
  }

  private lateinit var sessionManager: DefaultJellyfinSessionManager

  @BeforeTest
  fun setUp() {
    sessionManager = DefaultJellyfinSessionManager(logger = logger)
  }

  @Test
  fun initial_state_has_no_server() {
    sessionManager.currentServer.value.shouldBeNull()
    sessionManager.authenticated.value shouldBe false
  }

  @Test
  fun set_server_updates_current_server() {
    sessionManager.setServer(
      serverUrl = "https://jellyfin.example.com",
      serverId = "server-123",
      serverName = "My Server",
    )

    val server = sessionManager.currentServer.value
    server.shouldNotBeNull()
    server.baseUrl shouldBe "https://jellyfin.example.com"
  }

  @Test
  fun set_server_clears_authentication() {
    sessionManager.setServer(serverUrl = "https://jellyfin.example.com")
    sessionManager.setAuthentication(accessToken = "token123", userId = "user1")
    sessionManager.authenticated.value shouldBe true

    sessionManager.setServer(serverUrl = "https://other.example.com")
    sessionManager.authenticated.value shouldBe false
  }

  @Test
  fun set_authentication_updates_server_info() {
    sessionManager.setServer(serverUrl = "https://jellyfin.example.com")
    sessionManager.setAuthentication(
      accessToken = "token-abc",
      userId = "user-123",
    )

    val server = sessionManager.currentServer.value
    server.shouldNotBeNull()
    server.accessToken shouldBe "token-abc"
    server.userId shouldBe "user-123"
    sessionManager.authenticated.value shouldBe true
  }

  @Test
  fun set_authentication_without_server_is_ignored() {
    sessionManager.setAuthentication(
      accessToken = "token-abc",
      userId = "user-123",
    )

    sessionManager.currentServer.value.shouldBeNull()
    sessionManager.authenticated.value shouldBe false
  }

  @Test
  fun clear_authentication_removes_token_and_user() {
    sessionManager.setServer(serverUrl = "https://jellyfin.example.com")
    sessionManager.setAuthentication(accessToken = "token-abc", userId = "user-123")

    sessionManager.clearAuthentication()

    val server = sessionManager.currentServer.value
    server.shouldNotBeNull()
    server.baseUrl shouldBe "https://jellyfin.example.com"
    server.accessToken.shouldBeNull()
    server.userId.shouldBeNull()
    sessionManager.authenticated.value shouldBe false
  }

  @Test
  fun clear_session_removes_everything() {
    sessionManager.setServer(serverUrl = "https://jellyfin.example.com")
    sessionManager.setAuthentication(accessToken = "token-abc", userId = "user-123")

    sessionManager.clearSession()

    sessionManager.currentServer.value.shouldBeNull()
    sessionManager.authenticated.value shouldBe false
  }

  @Test
  fun clear_authentication_without_server_is_safe() {
    sessionManager.clearAuthentication()
    sessionManager.currentServer.value.shouldBeNull()
    sessionManager.authenticated.value shouldBe false
  }
}
