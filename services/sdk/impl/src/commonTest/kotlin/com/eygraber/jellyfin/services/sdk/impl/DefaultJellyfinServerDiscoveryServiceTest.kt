package com.eygraber.jellyfin.services.sdk.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.common.isError
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.sdk.core.model.PublicSystemInfo
import com.eygraber.jellyfin.sdk.core.model.ServerDiscoveryInfo
import com.eygraber.jellyfin.sdk.core.model.SystemInfo
import com.eygraber.jellyfin.services.logging.JellyfinLogger
import com.eygraber.jellyfin.services.sdk.JellyfinServerService
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DefaultJellyfinServerDiscoveryServiceTest {
  private val noopLogger = object : JellyfinLogger {
    override fun verbose(tag: String, message: String) {}
    override fun debug(tag: String, message: String) {}
    override fun info(tag: String, message: String) {}
    override fun warn(tag: String, message: String, throwable: Throwable?) {}
    override fun error(tag: String, message: String, throwable: Throwable?) {}
  }

  private fun createFakeServerService(
    connectResult: JellyfinResult<PublicSystemInfo> = JellyfinResult.Success(
      PublicSystemInfo(
        serverName = "Test Server",
        version = "10.9.0",
        id = "server-id-123",
      ),
    ),
  ): JellyfinServerService = object : JellyfinServerService {
    override fun discoverServers(timeoutMs: Long): Flow<ServerDiscoveryInfo> = emptyFlow()

    override suspend fun connectToServer(serverUrl: String): JellyfinResult<PublicSystemInfo> =
      connectResult

    override suspend fun getSystemInfo(): JellyfinResult<SystemInfo> =
      JellyfinResult.Error(message = "Not implemented", isEphemeral = false)

    override suspend fun ping(): JellyfinResult<String> =
      JellyfinResult.Error(message = "Not implemented", isEphemeral = false)
  }

  private fun createService(
    serverService: JellyfinServerService = createFakeServerService(),
  ): DefaultJellyfinServerDiscoveryService {
    val urlValidator = com.eygraber.jellyfin.domain.validators.ServerUrlValidator()
    val versionValidator = com.eygraber.jellyfin.domain.validators.ServerVersionValidator()
    return DefaultJellyfinServerDiscoveryService(
      serverService = serverService,
      urlValidator = urlValidator,
      versionValidator = versionValidator,
      logger = noopLogger,
    )
  }

  @Test
  fun connect_with_valid_url_returns_connection_info() {
    runTest {
      val service = createService()
      val result = service.connectToServer("https://jellyfin.example.com")

      result.isSuccess() shouldBe true
      val info = (result as JellyfinResult.Success).value
      info.serverUrl shouldBe "https://jellyfin.example.com"
      info.systemInfo.serverName shouldBe "Test Server"
      info.isVersionCompatible shouldBe true
    }
  }

  @Test
  fun connect_with_url_without_scheme_normalizes() {
    runTest {
      val service = createService()
      val result = service.connectToServer("jellyfin.example.com")

      result.isSuccess() shouldBe true
      val info = (result as JellyfinResult.Success).value
      info.serverUrl shouldBe "https://jellyfin.example.com"
    }
  }

  @Test
  fun connect_with_empty_url_returns_error() {
    runTest {
      val service = createService()
      val result = service.connectToServer("")

      result.isError() shouldBe true
      val error = result.shouldBeInstanceOf<JellyfinResult.Error.Generic>()
      error.message shouldBe "Server URL is required"
    }
  }

  @Test
  fun connect_with_http_url_returns_error() {
    runTest {
      val service = createService()
      val result = service.connectToServer("http://jellyfin.example.com")

      result.isError() shouldBe true
      val error = result.shouldBeInstanceOf<JellyfinResult.Error.Generic>()
      error.message shouldBe "HTTP connections are not supported, use HTTPS"
    }
  }

  @Test
  fun connect_with_incompatible_version_marks_incompatible() {
    runTest {
      val serverService = createFakeServerService(
        connectResult = JellyfinResult.Success(
          PublicSystemInfo(
            serverName = "Old Server",
            version = "10.7.0",
            id = "old-server",
          ),
        ),
      )
      val service = createService(serverService = serverService)
      val result = service.connectToServer("https://old.example.com")

      result.isSuccess() shouldBe true
      val info = (result as JellyfinResult.Success).value
      info.isVersionCompatible shouldBe false
    }
  }

  @Test
  fun connect_with_server_error_propagates_error() {
    runTest {
      val serverService = createFakeServerService(
        connectResult = JellyfinResult.Error(
          message = "Connection refused",
          isEphemeral = true,
        ),
      )
      val service = createService(serverService = serverService)
      val result = service.connectToServer("https://unreachable.example.com")

      result.isError() shouldBe true
    }
  }

  @Test
  fun connect_to_discovered_server_delegates_to_connect() {
    runTest {
      val service = createService()
      val discoveryInfo = ServerDiscoveryInfo(
        address = "https://discovered.example.com",
        id = "disc-server",
        name = "Discovered Server",
      )
      val result = service.connectToDiscoveredServer(discoveryInfo)

      result.isSuccess() shouldBe true
      val info = (result as JellyfinResult.Success).value
      info.serverUrl shouldBe "https://discovered.example.com"
    }
  }

  @Test
  fun connect_with_trailing_slash_normalizes() {
    runTest {
      val service = createService()
      val result = service.connectToServer("https://jellyfin.example.com/")

      result.isSuccess() shouldBe true
      val info = (result as JellyfinResult.Success).value
      info.serverUrl shouldBe "https://jellyfin.example.com"
    }
  }

  @Test
  fun connect_with_unknown_version_marks_compatible() {
    runTest {
      val serverService = createFakeServerService(
        connectResult = JellyfinResult.Success(
          PublicSystemInfo(
            serverName = "Mystery Server",
            version = null,
            id = "mystery",
          ),
        ),
      )
      val service = createService(serverService = serverService)
      val result = service.connectToServer("https://mystery.example.com")

      result.isSuccess() shouldBe true
      // Unknown version should NOT be marked as compatible
      val info = (result as JellyfinResult.Success).value
      info.isVersionCompatible shouldBe false
    }
  }
}
