package com.eygraber.jellyfin.domain.server.impl

import app.cash.turbine.test
import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.common.isError
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.data.auth.AuthRepository
import com.eygraber.jellyfin.data.auth.QuickConnectResult
import com.eygraber.jellyfin.data.auth.QuickConnectState
import com.eygraber.jellyfin.data.auth.UserSessionEntity
import com.eygraber.jellyfin.data.server.ServerEntity
import com.eygraber.jellyfin.data.server.ServerRepository
import com.eygraber.jellyfin.domain.server.ServerConnectionStatus
import com.eygraber.jellyfin.sdk.core.ServerInfo
import com.eygraber.jellyfin.sdk.core.model.PublicSystemInfo
import com.eygraber.jellyfin.sdk.core.model.ServerDiscoveryInfo
import com.eygraber.jellyfin.sdk.core.model.SystemInfo
import com.eygraber.jellyfin.services.logging.JellyfinLogger
import com.eygraber.jellyfin.services.sdk.JellyfinServerService
import com.eygraber.jellyfin.services.sdk.JellyfinSessionManager
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class DefaultServerManagerTest {
  private lateinit var serverManager: DefaultServerManager
  private lateinit var fakeServerRepository: FakeServerRepository
  private lateinit var fakeAuthRepository: FakeAuthRepository
  private lateinit var fakeServerService: FakeJellyfinServerService
  private lateinit var fakeSdkSessionManager: FakeJellyfinSessionManager

  private val testServer1 = ServerEntity(
    id = "server-1",
    name = "Test Server 1",
    url = "https://jellyfin1.example.com",
    version = "10.9.0",
    createdAt = 1000L,
    lastUsedAt = 2000L,
  )

  private val testServer2 = ServerEntity(
    id = "server-2",
    name = "Test Server 2",
    url = "https://jellyfin2.example.com",
    version = "10.9.0",
    createdAt = 1500L,
    lastUsedAt = 2500L,
  )

  private val testSession = UserSessionEntity(
    id = "server-1_user-1",
    serverId = "server-1",
    userId = "user-1",
    username = "testuser",
    accessToken = "token-123",
    isActive = true,
    createdAt = 1000L,
    lastUsedAt = 2000L,
  )

  private val noopLogger = object : JellyfinLogger {
    override fun verbose(tag: String, message: String) {}
    override fun debug(tag: String, message: String) {}
    override fun info(tag: String, message: String) {}
    override fun warn(tag: String, message: String, throwable: Throwable?) {}
    override fun error(tag: String, message: String, throwable: Throwable?) {}
  }

  @BeforeTest
  fun setUp() {
    fakeServerRepository = FakeServerRepository()
    fakeAuthRepository = FakeAuthRepository()
    fakeServerService = FakeJellyfinServerService()
    fakeSdkSessionManager = FakeJellyfinSessionManager()

    serverManager = DefaultServerManager(
      serverRepository = fakeServerRepository,
      authRepository = fakeAuthRepository,
      serverService = fakeServerService,
      sdkSessionManager = fakeSdkSessionManager,
      logger = noopLogger,
    )
  }

  @Test
  fun add_server_connects_and_persists() {
    runTest {
      fakeServerService.connectResult = JellyfinResult.Success(
        PublicSystemInfo(
          id = "server-1",
          serverName = "Test Server",
          version = "10.9.0",
        ),
      )

      val result = serverManager.addServer("https://jellyfin.example.com")

      val success = result.shouldBeInstanceOf<JellyfinResult.Success<ServerEntity>>()
      success.value.id shouldBe "server-1"
      success.value.name shouldBe "Test Server"
      success.value.url shouldBe "https://jellyfin.example.com"
      success.value.version shouldBe "10.9.0"
      fakeServerRepository.servers["server-1"]?.url shouldBe "https://jellyfin.example.com"
    }
  }

  @Test
  fun add_server_fails_when_connection_fails() {
    runTest {
      fakeServerService.connectResult = JellyfinResult.Error(
        message = "Connection refused",
        isEphemeral = true,
      )

      val result = serverManager.addServer("https://bad-server.example.com")

      result.isError() shouldBe true
    }
  }

  @Test
  fun add_server_fails_when_server_returns_no_id() {
    runTest {
      fakeServerService.connectResult = JellyfinResult.Success(
        PublicSystemInfo(id = null, serverName = "No ID Server"),
      )

      val result = serverManager.addServer("https://no-id-server.example.com")

      result.isError() shouldBe true
    }
  }

  @Test
  fun add_server_uses_url_as_name_when_server_name_is_null() {
    runTest {
      fakeServerService.connectResult = JellyfinResult.Success(
        PublicSystemInfo(id = "server-1", serverName = null),
      )

      val result = serverManager.addServer("https://jellyfin.example.com")

      val success = result.shouldBeInstanceOf<JellyfinResult.Success<ServerEntity>>()
      success.value.name shouldBe "https://jellyfin.example.com"
    }
  }

  @Test
  fun remove_server_deletes_sessions_and_server() {
    runTest {
      fakeServerRepository.servers["server-1"] = testServer1
      fakeAuthRepository.sessionsPerServer["server-1"] = listOf(testSession)

      val result = serverManager.removeServer("server-1")

      result.isSuccess() shouldBe true
      fakeAuthRepository.wasServerLoggedOut shouldBe true
      fakeServerRepository.servers.containsKey("server-1") shouldBe false
    }
  }

  @Test
  fun remove_active_server_clears_sdk_session() {
    runTest {
      fakeServerRepository.servers["server-1"] = testServer1
      fakeAuthRepository.activeSession = testSession

      val result = serverManager.removeServer("server-1")

      result.isSuccess() shouldBe true
      fakeSdkSessionManager.wasSessionCleared shouldBe true
    }
  }

  @Test
  fun remove_inactive_server_does_not_clear_sdk_session() {
    runTest {
      fakeServerRepository.servers["server-2"] = testServer2
      fakeAuthRepository.activeSession = testSession // active on server-1

      val result = serverManager.removeServer("server-2")

      result.isSuccess() shouldBe true
      fakeSdkSessionManager.wasSessionCleared shouldBe false
    }
  }

  @Test
  fun check_server_connection_returns_online_when_reachable() {
    runTest {
      fakeServerRepository.servers["server-1"] = testServer1
      fakeServerService.connectResult = JellyfinResult.Success(
        PublicSystemInfo(id = "server-1"),
      )

      val status = serverManager.checkServerConnection("server-1")

      status shouldBe ServerConnectionStatus.Online
    }
  }

  @Test
  fun check_server_connection_returns_offline_when_unreachable() {
    runTest {
      fakeServerRepository.servers["server-1"] = testServer1
      fakeServerService.connectResult = JellyfinResult.Error(
        message = "Connection refused",
        isEphemeral = true,
      )

      val status = serverManager.checkServerConnection("server-1")

      status shouldBe ServerConnectionStatus.Offline
    }
  }

  @Test
  fun check_server_connection_returns_unknown_for_missing_server() {
    runTest {
      val status = serverManager.checkServerConnection("nonexistent")

      status shouldBe ServerConnectionStatus.Unknown
    }
  }

  @Test
  fun observe_servers_emits_server_list_with_status() {
    runTest {
      val serversFlow = MutableStateFlow(listOf(testServer1))
      fakeServerRepository.serversFlow = serversFlow
      fakeAuthRepository.activeSession = testSession
      fakeAuthRepository.sessionsPerServer["server-1"] = listOf(testSession)

      serverManager.observeServers().test {
        val servers = awaitItem()
        servers.size shouldBe 1
        servers[0].server shouldBe testServer1
        servers[0].isActive shouldBe true
        servers[0].userCount shouldBe 1
        servers[0].connectionStatus shouldBe ServerConnectionStatus.Unknown

        cancelAndIgnoreRemainingEvents()
      }
    }
  }

  @Test
  fun observe_servers_marks_correct_server_as_active() {
    runTest {
      val serversFlow = MutableStateFlow(listOf(testServer1, testServer2))
      fakeServerRepository.serversFlow = serversFlow
      fakeAuthRepository.activeSession = testSession // active on server-1
      fakeAuthRepository.sessionsPerServer["server-1"] = listOf(testSession)

      serverManager.observeServers().test {
        val servers = awaitItem()
        servers.size shouldBe 2
        servers[0].isActive shouldBe true
        servers[1].isActive shouldBe false

        cancelAndIgnoreRemainingEvents()
      }
    }
  }

  @Test
  fun refresh_connection_statuses_checks_all_servers() {
    runTest {
      fakeServerRepository.servers["server-1"] = testServer1
      fakeServerRepository.servers["server-2"] = testServer2
      fakeServerService.connectResult = JellyfinResult.Success(
        PublicSystemInfo(id = "server-1"),
      )

      serverManager.refreshConnectionStatuses()

      // Both servers should have been checked
      val status1 = serverManager.checkServerConnection("server-1")
      val status2 = serverManager.checkServerConnection("server-2")
      status1 shouldBe ServerConnectionStatus.Online
      status2 shouldBe ServerConnectionStatus.Online
    }
  }
}

private class FakeServerRepository : ServerRepository {
  val servers = mutableMapOf<String, ServerEntity>()
  var serversFlow: MutableStateFlow<List<ServerEntity>> = MutableStateFlow(emptyList())

  override fun observeServers(): Flow<List<ServerEntity>> = serversFlow

  override suspend fun getServers(): JellyfinResult<List<ServerEntity>> =
    JellyfinResult.Success(servers.values.toList())

  override suspend fun getServerById(serverId: String): JellyfinResult<ServerEntity> {
    val server = servers[serverId]
      ?: return JellyfinResult.Error(message = "Not found", isEphemeral = false)
    return JellyfinResult.Success(server)
  }

  override suspend fun getServerByUrl(url: String): JellyfinResult<ServerEntity> =
    JellyfinResult.Error(message = "Not implemented", isEphemeral = false)

  override suspend fun saveServer(server: ServerEntity): JellyfinResult<Unit> {
    servers[server.id] = server
    return JellyfinResult.Success(Unit)
  }

  override suspend fun updateLastUsed(serverId: String, timestamp: Long): JellyfinResult<Unit> =
    JellyfinResult.Success(Unit)

  override suspend fun deleteServer(serverId: String): JellyfinResult<Unit> {
    servers.remove(serverId)
    return JellyfinResult.Success(Unit)
  }

  override suspend fun deleteAllServers(): JellyfinResult<Unit> {
    servers.clear()
    return JellyfinResult.Success(Unit)
  }
}

private class FakeAuthRepository : AuthRepository {
  var activeSession: UserSessionEntity? = null
  val sessionsPerServer: MutableMap<String, List<UserSessionEntity>> = mutableMapOf()
  var wasServerLoggedOut = false
  private val activeSessionFlow = MutableStateFlow<UserSessionEntity?>(null)

  override fun observeActiveSession(): Flow<UserSessionEntity?> {
    activeSessionFlow.value = activeSession
    return activeSessionFlow
  }

  override fun observeSessionsForServer(serverId: String): Flow<List<UserSessionEntity>> = emptyFlow()

  override suspend fun getActiveSession(): JellyfinResult<UserSessionEntity?> =
    JellyfinResult.Success(activeSession)

  override suspend fun getSessionsForServer(serverId: String): JellyfinResult<List<UserSessionEntity>> =
    JellyfinResult.Success(sessionsPerServer[serverId].orEmpty())

  override suspend fun login(
    serverId: String,
    serverUrl: String,
    username: String,
    password: String,
  ): JellyfinResult<UserSessionEntity> =
    JellyfinResult.Error(message = "Not implemented", isEphemeral = false)

  override suspend fun initiateQuickConnect(
    serverUrl: String,
  ): JellyfinResult<QuickConnectState> =
    JellyfinResult.Error(message = "Not implemented", isEphemeral = false)

  override suspend fun checkQuickConnect(
    serverId: String,
    serverUrl: String,
    secret: String,
  ): JellyfinResult<QuickConnectResult> =
    JellyfinResult.Error(message = "Not implemented", isEphemeral = false)

  override suspend fun setActiveSession(sessionId: String): JellyfinResult<Unit> =
    JellyfinResult.Success(Unit)

  override suspend fun logout(): JellyfinResult<Unit> {
    activeSession = null
    return JellyfinResult.Success(Unit)
  }

  override suspend fun logoutSession(sessionId: String): JellyfinResult<Unit> =
    JellyfinResult.Success(Unit)

  override suspend fun logoutServer(serverId: String): JellyfinResult<Unit> {
    wasServerLoggedOut = true
    sessionsPerServer.remove(serverId)
    return JellyfinResult.Success(Unit)
  }
}

private class FakeJellyfinServerService : JellyfinServerService {
  var connectResult: JellyfinResult<PublicSystemInfo> =
    JellyfinResult.Error(message = "Not configured", isEphemeral = false)

  override fun discoverServers(timeoutMs: Long): Flow<ServerDiscoveryInfo> = emptyFlow()

  override suspend fun connectToServer(serverUrl: String): JellyfinResult<PublicSystemInfo> =
    connectResult

  override suspend fun getSystemInfo(): JellyfinResult<SystemInfo> =
    JellyfinResult.Error(message = "Not implemented", isEphemeral = false)

  override suspend fun ping(): JellyfinResult<String> =
    JellyfinResult.Error(message = "Not implemented", isEphemeral = false)
}

private class FakeJellyfinSessionManager : JellyfinSessionManager {
  var wasSessionCleared = false

  override val currentServer: StateFlow<ServerInfo?> = MutableStateFlow(null)
  override val authenticated: StateFlow<Boolean> = MutableStateFlow(false)

  override fun setServer(serverUrl: String, serverId: String?, serverName: String?) {}

  override fun setAuthentication(accessToken: String, userId: String) {}

  override fun clearAuthentication() {}

  override fun clearSession() {
    wasSessionCleared = true
  }
}
