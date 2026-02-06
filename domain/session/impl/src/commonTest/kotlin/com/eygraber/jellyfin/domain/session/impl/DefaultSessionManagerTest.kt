package com.eygraber.jellyfin.domain.session.impl

import app.cash.turbine.test
import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.data.auth.AuthRepository
import com.eygraber.jellyfin.data.auth.QuickConnectResult
import com.eygraber.jellyfin.data.auth.QuickConnectState
import com.eygraber.jellyfin.data.auth.UserSessionEntity
import com.eygraber.jellyfin.data.server.ServerEntity
import com.eygraber.jellyfin.data.server.ServerRepository
import com.eygraber.jellyfin.domain.session.SessionState
import com.eygraber.jellyfin.sdk.core.ServerInfo
import com.eygraber.jellyfin.sdk.core.model.AuthenticationResult
import com.eygraber.jellyfin.sdk.core.model.UserDto
import com.eygraber.jellyfin.services.logging.JellyfinLogger
import com.eygraber.jellyfin.services.sdk.JellyfinAuthService
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
import com.eygraber.jellyfin.sdk.core.model.QuickConnectResult as SdkQuickConnectResult

class DefaultSessionManagerTest {
  private lateinit var sessionManager: DefaultSessionManager
  private lateinit var fakeAuthRepository: FakeAuthRepository
  private lateinit var fakeServerRepository: FakeServerRepository
  private lateinit var fakeAuthService: FakeJellyfinAuthService
  private lateinit var fakeSdkSessionManager: FakeJellyfinSessionManager

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

  private val testServer = ServerEntity(
    id = "server-1",
    name = "Test Server",
    url = "https://jellyfin.example.com",
    version = "10.9.0",
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
    fakeAuthRepository = FakeAuthRepository()
    fakeServerRepository = FakeServerRepository()
    fakeAuthService = FakeJellyfinAuthService()
    fakeSdkSessionManager = FakeJellyfinSessionManager()

    sessionManager = DefaultSessionManager(
      authRepository = fakeAuthRepository,
      serverRepository = fakeServerRepository,
      authService = fakeAuthService,
      sdkSessionManager = fakeSdkSessionManager,
      logger = noopLogger,
    )
  }

  @Test
  fun initial_state_is_loading() {
    sessionManager.sessionState.value.shouldBeInstanceOf<SessionState.Loading>()
  }

  @Test
  fun restore_session_with_valid_token() {
    runTest {
      fakeAuthRepository.activeSession = testSession
      fakeServerRepository.servers["server-1"] = testServer
      fakeAuthService.currentUserResult = JellyfinResult.Success(
        UserDto(id = "user-1", name = "testuser"),
      )

      val result = sessionManager.restoreSession()

      result.shouldBeInstanceOf<SessionState.Authenticated>()
      result.session.userId shouldBe "user-1"
      sessionManager.sessionState.value.shouldBeInstanceOf<SessionState.Authenticated>()
    }
  }

  @Test
  fun restore_session_with_expired_token() {
    runTest {
      fakeAuthRepository.activeSession = testSession
      fakeServerRepository.servers["server-1"] = testServer
      fakeAuthService.currentUserResult = JellyfinResult.Error(
        message = "Unauthorized",
        isEphemeral = false,
      )

      val result = sessionManager.restoreSession()

      result.shouldBeInstanceOf<SessionState.SessionExpired>()
      result.session.userId shouldBe "user-1"
      sessionManager.sessionState.value.shouldBeInstanceOf<SessionState.SessionExpired>()
    }
  }

  @Test
  fun restore_session_with_no_active_session() {
    runTest {
      fakeAuthRepository.activeSession = null

      val result = sessionManager.restoreSession()

      result.shouldBeInstanceOf<SessionState.NoSession>()
      sessionManager.sessionState.value.shouldBeInstanceOf<SessionState.NoSession>()
    }
  }

  @Test
  fun restore_session_configures_sdk_session_manager() {
    runTest {
      fakeAuthRepository.activeSession = testSession
      fakeServerRepository.servers["server-1"] = testServer
      fakeAuthService.currentUserResult = JellyfinResult.Success(
        UserDto(id = "user-1", name = "testuser"),
      )

      sessionManager.restoreSession()

      fakeSdkSessionManager.lastServerUrl shouldBe "https://jellyfin.example.com"
      fakeSdkSessionManager.lastAccessToken shouldBe "token-123"
      fakeSdkSessionManager.lastUserId shouldBe "user-1"
    }
  }

  @Test
  fun validate_session_returns_true_for_valid_token() {
    runTest {
      fakeAuthRepository.activeSession = testSession
      fakeServerRepository.servers["server-1"] = testServer
      fakeAuthService.currentUserResult = JellyfinResult.Success(
        UserDto(id = "user-1", name = "testuser"),
      )

      sessionManager.restoreSession()
      val isValid = sessionManager.validateSession()

      isValid shouldBe true
      sessionManager.sessionState.value.shouldBeInstanceOf<SessionState.Authenticated>()
    }
  }

  @Test
  fun validate_session_returns_false_for_expired_token() {
    runTest {
      fakeAuthRepository.activeSession = testSession
      fakeServerRepository.servers["server-1"] = testServer

      // First make restore succeed
      fakeAuthService.currentUserResult = JellyfinResult.Success(
        UserDto(id = "user-1", name = "testuser"),
      )
      sessionManager.restoreSession()

      // Then make validate fail
      fakeAuthService.currentUserResult = JellyfinResult.Error(
        message = "Token expired",
        isEphemeral = false,
      )
      val isValid = sessionManager.validateSession()

      isValid shouldBe false
      sessionManager.sessionState.value.shouldBeInstanceOf<SessionState.SessionExpired>()
    }
  }

  @Test
  fun logout_clears_session_state() {
    runTest {
      fakeAuthRepository.activeSession = testSession
      fakeServerRepository.servers["server-1"] = testServer
      fakeAuthService.currentUserResult = JellyfinResult.Success(
        UserDto(id = "user-1", name = "testuser"),
      )

      sessionManager.restoreSession()
      val result = sessionManager.logout()

      result.isSuccess() shouldBe true
      sessionManager.sessionState.value.shouldBeInstanceOf<SessionState.NoSession>()
      fakeSdkSessionManager.wasSessionCleared shouldBe true
    }
  }

  @Test
  fun on_login_success_updates_state() {
    runTest {
      fakeAuthRepository.activeSession = testSession

      sessionManager.onLoginSuccess(
        serverId = "server-1",
        serverUrl = "https://jellyfin.example.com",
        accessToken = "token-123",
        userId = "user-1",
      )

      sessionManager.sessionState.value.shouldBeInstanceOf<SessionState.Authenticated>()
    }
  }

  @Test
  fun session_state_flow_emits_transitions() {
    runTest {
      sessionManager.sessionState.test {
        // Initial loading state
        awaitItem().shouldBeInstanceOf<SessionState.Loading>()

        // Restore with no session
        fakeAuthRepository.activeSession = null
        sessionManager.restoreSession()
        awaitItem().shouldBeInstanceOf<SessionState.NoSession>()

        // Login success
        fakeAuthRepository.activeSession = testSession
        sessionManager.onLoginSuccess(
          serverId = "server-1",
          serverUrl = "https://jellyfin.example.com",
          accessToken = "token-123",
          userId = "user-1",
        )
        awaitItem().shouldBeInstanceOf<SessionState.Authenticated>()

        cancelAndIgnoreRemainingEvents()
      }
    }
  }
}

private class FakeAuthRepository : AuthRepository {
  var activeSession: UserSessionEntity? = null
  var wasLogoutCalled = false

  override fun observeActiveSession(): Flow<UserSessionEntity?> = emptyFlow()
  override fun observeSessionsForServer(serverId: String): Flow<List<UserSessionEntity>> = emptyFlow()

  override suspend fun getActiveSession(): JellyfinResult<UserSessionEntity?> =
    JellyfinResult.Success(activeSession)

  override suspend fun getSessionsForServer(serverId: String): JellyfinResult<List<UserSessionEntity>> =
    JellyfinResult.Success(emptyList())

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
    wasLogoutCalled = true
    activeSession = null
    return JellyfinResult.Success(Unit)
  }

  override suspend fun logoutSession(sessionId: String): JellyfinResult<Unit> =
    JellyfinResult.Success(Unit)

  override suspend fun logoutServer(serverId: String): JellyfinResult<Unit> =
    JellyfinResult.Success(Unit)
}

private class FakeServerRepository : ServerRepository {
  val servers = mutableMapOf<String, ServerEntity>()

  override fun observeServers(): Flow<List<ServerEntity>> = emptyFlow()

  override suspend fun getServers(): JellyfinResult<List<ServerEntity>> =
    JellyfinResult.Success(servers.values.toList())

  override suspend fun getServerById(serverId: String): JellyfinResult<ServerEntity> {
    val server = servers[serverId]
      ?: return JellyfinResult.Error(message = "Not found", isEphemeral = false)
    return JellyfinResult.Success(server)
  }

  override suspend fun getServerByUrl(url: String): JellyfinResult<ServerEntity> =
    JellyfinResult.Error(message = "Not implemented", isEphemeral = false)

  override suspend fun saveServer(server: ServerEntity): JellyfinResult<Unit> =
    JellyfinResult.Success(Unit)

  override suspend fun updateLastUsed(serverId: String, timestamp: Long): JellyfinResult<Unit> =
    JellyfinResult.Success(Unit)

  override suspend fun deleteServer(serverId: String): JellyfinResult<Unit> =
    JellyfinResult.Success(Unit)

  override suspend fun deleteAllServers(): JellyfinResult<Unit> =
    JellyfinResult.Success(Unit)
}

private class FakeJellyfinAuthService : JellyfinAuthService {
  var currentUserResult: JellyfinResult<UserDto> =
    JellyfinResult.Error(message = "Not configured", isEphemeral = false)

  override suspend fun authenticateByName(
    serverUrl: String,
    username: String,
    password: String,
  ): JellyfinResult<AuthenticationResult> =
    JellyfinResult.Error(message = "Not implemented", isEphemeral = false)

  override suspend fun getCurrentUser(): JellyfinResult<UserDto> = currentUserResult

  override suspend fun getPublicUsers(
    serverUrl: String,
  ): JellyfinResult<List<UserDto>> =
    JellyfinResult.Error(message = "Not implemented", isEphemeral = false)

  override suspend fun initiateQuickConnect(
    serverUrl: String,
  ): JellyfinResult<SdkQuickConnectResult> =
    JellyfinResult.Error(message = "Not implemented", isEphemeral = false)

  override suspend fun getQuickConnectStatus(
    serverUrl: String,
    secret: String,
  ): JellyfinResult<SdkQuickConnectResult> =
    JellyfinResult.Error(message = "Not implemented", isEphemeral = false)

  override suspend fun logout(): JellyfinResult<Unit> =
    JellyfinResult.Success(Unit)
}

private class FakeJellyfinSessionManager : JellyfinSessionManager {
  var lastServerUrl: String? = null
  var lastAccessToken: String? = null
  var lastUserId: String? = null
  var wasSessionCleared = false

  override val currentServer: StateFlow<ServerInfo?> = MutableStateFlow(null)
  override val authenticated: StateFlow<Boolean> = MutableStateFlow(false)

  override fun setServer(serverUrl: String, serverId: String?, serverName: String?) {
    lastServerUrl = serverUrl
  }

  override fun setAuthentication(accessToken: String, userId: String) {
    lastAccessToken = accessToken
    lastUserId = userId
  }

  override fun clearAuthentication() {
    lastAccessToken = null
    lastUserId = null
  }

  override fun clearSession() {
    wasSessionCleared = true
    lastServerUrl = null
    lastAccessToken = null
    lastUserId = null
  }
}
