package com.eygraber.jellyfin.screens.login

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.data.auth.AuthRepository
import com.eygraber.jellyfin.data.auth.QuickConnectResult
import com.eygraber.jellyfin.data.auth.QuickConnectState
import com.eygraber.jellyfin.data.auth.UserSessionEntity
import com.eygraber.jellyfin.data.server.ServerEntity
import com.eygraber.jellyfin.domain.server.ServerConnectionStatus
import com.eygraber.jellyfin.domain.server.ServerManager
import com.eygraber.jellyfin.domain.server.ServerWithStatus
import com.eygraber.jellyfin.domain.session.SessionManager
import com.eygraber.jellyfin.domain.session.SessionState
import com.eygraber.jellyfin.domain.validators.ServerUrlValidator
import com.eygraber.jellyfin.screens.login.model.LoginFieldsModel
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class LoginCompositorTest {
  private lateinit var serverUrlValidator: ServerUrlValidator
  private lateinit var fakeServerManager: FakeServerManager
  private lateinit var fakeAuthRepository: FakeAuthRepository
  private lateinit var fakeSessionManager: FakeSessionManager
  private lateinit var navTracker: NavigatorTracker
  private lateinit var compositor: LoginCompositor

  private val testServer = ServerEntity(
    id = "server-1",
    name = "Test Server",
    url = "https://jellyfin.example.com",
    version = "10.10.0",
    createdAt = 0L,
    lastUsedAt = 0L,
  )

  private val testSession = UserSessionEntity(
    id = "session-1",
    serverId = "server-1",
    userId = "user-1",
    username = "alice",
    accessToken = "token-abc",
    isActive = true,
    createdAt = 0L,
    lastUsedAt = 0L,
  )

  @BeforeTest
  fun setUp() {
    serverUrlValidator = ServerUrlValidator()
    fakeServerManager = FakeServerManager()
    fakeAuthRepository = FakeAuthRepository()
    fakeSessionManager = FakeSessionManager()
    navTracker = NavigatorTracker()
    compositor = LoginCompositor(
      serverUrlValidator = serverUrlValidator,
      serverManager = fakeServerManager,
      authRepository = fakeAuthRepository,
      sessionManager = fakeSessionManager,
      navigator = LoginNavigator(
        onNavigateToHome = { navTracker.navigateToHomeCount++ },
        onNavigateBack = { navTracker.navigateBackCount++ },
      ),
      fieldsModel = LoginFieldsModel(),
    )
  }

  @Test
  fun back_intent_navigates_back() = runTest {
    compositor.onIntent(LoginIntent.BackClicked)

    navTracker.navigateBackCount shouldBe 1
    navTracker.navigateToHomeCount shouldBe 0
  }

  @Test
  fun login_with_blank_url_sets_serverUrlError_Empty() = runTest {
    compositor.onIntent(loginIntent(serverUrl = "   "))

    compositor.serverUrlErrorForTest shouldBe ServerUrlError.Empty
    fakeServerManager.addServerCalls.size shouldBe 0
    fakeAuthRepository.loginCalls.size shouldBe 0
  }

  @Test
  fun login_with_invalid_url_sets_serverUrlError_InvalidFormat() = runTest {
    compositor.onIntent(loginIntent(serverUrl = "not a url"))

    compositor.serverUrlErrorForTest shouldBe ServerUrlError.InvalidFormat
    fakeServerManager.addServerCalls.size shouldBe 0
  }

  @Test
  fun login_with_http_url_sets_serverUrlError_InsecureProtocol() = runTest {
    compositor.onIntent(loginIntent(serverUrl = "http://jellyfin.example.com"))

    compositor.serverUrlErrorForTest shouldBe ServerUrlError.InsecureProtocol
    fakeServerManager.addServerCalls.size shouldBe 0
  }

  @Test
  fun login_with_addServer_failure_sets_loginError_ServerUnreachable() = runTest {
    fakeServerManager.addServerResult = genericError("unreachable")

    compositor.onIntent(loginIntent(serverUrl = "https://jellyfin.example.com"))

    compositor.loginErrorForTest shouldBe LoginError.ServerUnreachable
    compositor.isLoadingForTest shouldBe false
    fakeAuthRepository.loginCalls.size shouldBe 0
  }

  @Test
  fun login_with_invalid_credentials_sets_loginError_InvalidCredentials() = runTest {
    fakeServerManager.addServerResult = JellyfinResult.Success(testServer)
    fakeAuthRepository.loginResult = genericError("Unauthorized")

    compositor.onIntent(
      loginIntent(
        serverUrl = "https://jellyfin.example.com",
        username = "alice",
        password = "wrong",
      ),
    )

    compositor.loginErrorForTest shouldBe LoginError.InvalidCredentials
    compositor.isLoadingForTest shouldBe false
    navTracker.navigateToHomeCount shouldBe 0
  }

  @Test
  fun successful_login_calls_onLoginSuccess_and_navigates_to_home() = runTest {
    fakeServerManager.addServerResult = JellyfinResult.Success(testServer)
    fakeAuthRepository.loginResult = JellyfinResult.Success(testSession)

    compositor.onIntent(
      loginIntent(
        serverUrl = "https://jellyfin.example.com",
        username = "alice",
        password = "secret",
      ),
    )

    fakeSessionManager.onLoginSuccessCalls.size shouldBe 1
    val args = fakeSessionManager.onLoginSuccessCalls.single()
    args.serverId shouldBe testServer.id
    args.serverUrl shouldBe testServer.url
    args.accessToken shouldBe testSession.accessToken
    args.userId shouldBe testSession.userId

    navTracker.navigateToHomeCount shouldBe 1
    compositor.isLoadingForTest shouldBe false
    compositor.loginErrorForTest shouldBe null
  }

  @Test
  fun successful_login_trims_username_but_not_password() = runTest {
    fakeServerManager.addServerResult = JellyfinResult.Success(testServer)
    fakeAuthRepository.loginResult = JellyfinResult.Success(testSession)

    compositor.onIntent(
      loginIntent(
        serverUrl = "https://jellyfin.example.com",
        username = "  alice  ",
        password = "  pass with spaces  ",
      ),
    )

    val call = fakeAuthRepository.loginCalls.single()
    call.username shouldBe "alice"
    call.password shouldBe "  pass with spaces  "
  }

  @Test
  fun successful_login_uses_normalized_url_when_scheme_missing() = runTest {
    fakeServerManager.addServerResult = JellyfinResult.Success(testServer)
    fakeAuthRepository.loginResult = JellyfinResult.Success(testSession)

    compositor.onIntent(loginIntent(serverUrl = "jellyfin.example.com"))

    fakeServerManager.addServerCalls.single() shouldBe "https://jellyfin.example.com"
  }

  private fun loginIntent(
    serverUrl: String = "https://jellyfin.example.com",
    username: String = "alice",
    password: String = "secret",
  ) = LoginIntent.LoginClicked(
    serverUrl = serverUrl,
    username = username,
    password = password,
  )
}

private fun <T> genericError(message: String): JellyfinResult<T> =
  JellyfinResult.Error.Generic(message = message, isEphemeral = false)

private class NavigatorTracker {
  var navigateToHomeCount = 0
  var navigateBackCount = 0
}

private data class OnLoginSuccessArgs(
  val serverId: String,
  val serverUrl: String,
  val accessToken: String,
  val userId: String,
)

private data class LoginCallArgs(
  val serverId: String,
  val serverUrl: String,
  val username: String,
  val password: String,
)

private class FakeServerManager : ServerManager {
  var addServerResult: JellyfinResult<ServerEntity> = genericError("not configured")
  val addServerCalls = mutableListOf<String>()

  override fun observeServers(): Flow<List<ServerWithStatus>> = flowOf(emptyList())

  override suspend fun addServer(serverUrl: String): JellyfinResult<ServerEntity> {
    addServerCalls.add(serverUrl)
    return addServerResult
  }

  override suspend fun removeServer(serverId: String): JellyfinResult<Unit> = JellyfinResult.Success(Unit)

  override suspend fun checkServerConnection(serverId: String): ServerConnectionStatus =
    ServerConnectionStatus.Unknown

  override suspend fun refreshConnectionStatuses() {}
}

private class FakeAuthRepository : AuthRepository {
  var loginResult: JellyfinResult<UserSessionEntity> = genericError("not configured")
  val loginCalls = mutableListOf<LoginCallArgs>()

  override fun observeActiveSession(): Flow<UserSessionEntity?> = flowOf(null)

  override fun observeSessionsForServer(serverId: String): Flow<List<UserSessionEntity>> = flowOf(emptyList())

  override suspend fun getActiveSession(): JellyfinResult<UserSessionEntity?> = JellyfinResult.Success(null)

  override suspend fun getSessionsForServer(serverId: String): JellyfinResult<List<UserSessionEntity>> =
    JellyfinResult.Success(emptyList())

  override suspend fun login(
    serverId: String,
    serverUrl: String,
    username: String,
    password: String,
  ): JellyfinResult<UserSessionEntity> {
    loginCalls.add(
      LoginCallArgs(
        serverId = serverId,
        serverUrl = serverUrl,
        username = username,
        password = password,
      ),
    )
    return loginResult
  }

  override suspend fun initiateQuickConnect(serverUrl: String): JellyfinResult<QuickConnectState> =
    genericError("not implemented")

  override suspend fun checkQuickConnect(
    serverId: String,
    serverUrl: String,
    secret: String,
  ): JellyfinResult<QuickConnectResult> = genericError("not implemented")

  override suspend fun setActiveSession(sessionId: String): JellyfinResult<Unit> = JellyfinResult.Success(Unit)

  override suspend fun logout(): JellyfinResult<Unit> = JellyfinResult.Success(Unit)

  override suspend fun logoutSession(sessionId: String): JellyfinResult<Unit> = JellyfinResult.Success(Unit)

  override suspend fun logoutServer(serverId: String): JellyfinResult<Unit> = JellyfinResult.Success(Unit)
}

private class FakeSessionManager : SessionManager {
  override val sessionState: StateFlow<SessionState> = MutableStateFlow(SessionState.NoSession)

  val onLoginSuccessCalls = mutableListOf<OnLoginSuccessArgs>()

  override suspend fun restoreSession(): SessionState = SessionState.NoSession

  override suspend fun validateSession(): Boolean = true

  override suspend fun onLoginSuccess(
    serverId: String,
    serverUrl: String,
    accessToken: String,
    userId: String,
  ) {
    onLoginSuccessCalls.add(
      OnLoginSuccessArgs(
        serverId = serverId,
        serverUrl = serverUrl,
        accessToken = accessToken,
        userId = userId,
      ),
    )
  }

  override suspend fun logout(): JellyfinResult<Unit> = JellyfinResult.Success(Unit)

  override suspend fun switchSession(sessionId: String): JellyfinResult<Unit> = JellyfinResult.Success(Unit)
}
