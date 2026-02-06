package com.eygraber.jellyfin.data.auth.impl

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.common.isError
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.data.auth.QuickConnectResult
import com.eygraber.jellyfin.sdk.core.model.AuthenticationResult
import com.eygraber.jellyfin.sdk.core.model.UserDto
import com.eygraber.jellyfin.services.database.impl.JellyfinDatabase
import com.eygraber.jellyfin.services.logging.JellyfinLogger
import com.eygraber.jellyfin.services.sdk.JellyfinAuthService
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteDatabaseType
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteDriver
import com.eygraber.sqldelight.androidx.driver.File
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import com.eygraber.jellyfin.sdk.core.model.QuickConnectResult as SdkQuickConnectResult

class SqlDelightAuthRepositoryTest {
  private lateinit var driver: AndroidxSqliteDriver
  private lateinit var database: JellyfinDatabase
  private lateinit var repository: SqlDelightAuthRepository
  private lateinit var tempDir: File
  private lateinit var fakeAuthService: FakeJellyfinAuthService
  private var currentTime = 1000L

  private val noopLogger = object : JellyfinLogger {
    override fun verbose(tag: String, message: String) {}
    override fun debug(tag: String, message: String) {}
    override fun info(tag: String, message: String) {}
    override fun warn(tag: String, message: String, throwable: Throwable?) {}
    override fun error(tag: String, message: String, throwable: Throwable?) {}
  }

  @BeforeTest
  fun setUp() {
    currentTime = 1000L
    fakeAuthService = FakeJellyfinAuthService()

    tempDir = File.createTempFile("jellyfin-auth-test", null).apply {
      delete()
      mkdirs()
    }

    driver = AndroidxSqliteDriver(
      driver = BundledSQLiteDriver(),
      databaseType = AndroidxSqliteDatabaseType.File(
        file = File(tempDir, "test.db"),
      ),
      schema = JellyfinDatabase.Schema,
    )
    database = JellyfinDatabase(driver)

    // Insert servers to satisfy FK constraint
    database.serverQueries.insert(
      id = "server-1",
      name = "Test Server",
      url = "https://jellyfin.example.com",
      version = "10.9.0",
      created_at = 1000L,
      last_used_at = 1000L,
    )
    database.serverQueries.insert(
      id = "server-2",
      name = "Other Server",
      url = "https://other.example.com",
      version = "10.9.0",
      created_at = 1000L,
      last_used_at = 1000L,
    )

    val localDataSource = AuthLocalDataSource(database = database)
    val remoteDataSource = AuthRemoteDataSource(authService = fakeAuthService)
    repository = SqlDelightAuthRepository(
      localDataSource = localDataSource,
      remoteDataSource = remoteDataSource,
      logger = noopLogger,
      clock = { currentTime },
    )
  }

  @AfterTest
  fun tearDown() {
    driver.close()
    tempDir.deleteRecursively()
  }

  @Test
  fun login_success_creates_session() {
    runTest {
      fakeAuthService.authenticateResult = JellyfinResult.Success(
        AuthenticationResult(
          accessToken = "token-123",
          user = UserDto(id = "user-1", name = "testuser"),
          serverId = "server-1",
        ),
      )

      val result = repository.login(
        serverId = "server-1",
        serverUrl = "https://jellyfin.example.com",
        username = "testuser",
        password = "password",
      )

      result.isSuccess() shouldBe true
      val session = (result as JellyfinResult.Success).value
      session.serverId shouldBe "server-1"
      session.userId shouldBe "user-1"
      session.username shouldBe "testuser"
      session.accessToken shouldBe "token-123"
      session.isActive shouldBe true
    }
  }

  @Test
  fun login_failure_returns_error() {
    runTest {
      fakeAuthService.authenticateResult = JellyfinResult.Error(
        message = "Invalid credentials",
        isEphemeral = false,
      )

      val result = repository.login(
        serverId = "server-1",
        serverUrl = "https://jellyfin.example.com",
        username = "testuser",
        password = "wrong",
      )

      result.isError() shouldBe true
    }
  }

  @Test
  fun login_missing_token_returns_error() {
    runTest {
      fakeAuthService.authenticateResult = JellyfinResult.Success(
        AuthenticationResult(
          accessToken = null,
          user = UserDto(id = "user-1", name = "testuser"),
        ),
      )

      val result = repository.login(
        serverId = "server-1",
        serverUrl = "https://jellyfin.example.com",
        username = "testuser",
        password = "password",
      )

      result.isError() shouldBe true
    }
  }

  @Test
  fun get_active_session_returns_logged_in_session() {
    runTest {
      fakeAuthService.authenticateResult = JellyfinResult.Success(
        AuthenticationResult(
          accessToken = "token-123",
          user = UserDto(id = "user-1", name = "testuser"),
          serverId = "server-1",
        ),
      )

      repository.login(
        serverId = "server-1",
        serverUrl = "https://jellyfin.example.com",
        username = "testuser",
        password = "password",
      )

      val result = repository.getActiveSession()
      result.isSuccess() shouldBe true
      val session = (result as JellyfinResult.Success).value
      session?.userId shouldBe "user-1"
      session?.isActive shouldBe true
    }
  }

  @Test
  fun set_active_session_switches_sessions() {
    runTest {
      fakeAuthService.authenticateResult = JellyfinResult.Success(
        AuthenticationResult(
          accessToken = "token-1",
          user = UserDto(id = "user-1", name = "user1"),
          serverId = "server-1",
        ),
      )
      repository.login(
        serverId = "server-1",
        serverUrl = "https://jellyfin.example.com",
        username = "user1",
        password = "password",
      )

      fakeAuthService.authenticateResult = JellyfinResult.Success(
        AuthenticationResult(
          accessToken = "token-2",
          user = UserDto(id = "user-2", name = "user2"),
          serverId = "server-1",
        ),
      )
      repository.login(
        serverId = "server-1",
        serverUrl = "https://jellyfin.example.com",
        username = "user2",
        password = "password",
      )

      // user-2 should be active now
      var active = (repository.getActiveSession() as JellyfinResult.Success).value
      active?.userId shouldBe "user-2"

      // Switch to user-1
      currentTime = 5000L
      repository.setActiveSession("server-1_user-1")
      active = (repository.getActiveSession() as JellyfinResult.Success).value
      active?.userId shouldBe "user-1"
    }
  }

  @Test
  fun get_sessions_for_server() {
    runTest {
      fakeAuthService.authenticateResult = JellyfinResult.Success(
        AuthenticationResult(
          accessToken = "token-1",
          user = UserDto(id = "user-1", name = "user1"),
          serverId = "server-1",
        ),
      )
      repository.login(
        serverId = "server-1",
        serverUrl = "https://jellyfin.example.com",
        username = "user1",
        password = "password",
      )

      fakeAuthService.authenticateResult = JellyfinResult.Success(
        AuthenticationResult(
          accessToken = "token-2",
          user = UserDto(id = "user-2", name = "user2"),
          serverId = "server-2",
        ),
      )
      repository.login(
        serverId = "server-2",
        serverUrl = "https://other.example.com",
        username = "user2",
        password = "password",
      )

      val result = repository.getSessionsForServer("server-1")
      result.isSuccess() shouldBe true
      val sessions = (result as JellyfinResult.Success).value
      sessions shouldHaveSize 1
      sessions[0].serverId shouldBe "server-1"
    }
  }

  @Test
  fun logout_session_removes_from_database() {
    runTest {
      fakeAuthService.authenticateResult = JellyfinResult.Success(
        AuthenticationResult(
          accessToken = "token-123",
          user = UserDto(id = "user-1", name = "testuser"),
          serverId = "server-1",
        ),
      )
      repository.login(
        serverId = "server-1",
        serverUrl = "https://jellyfin.example.com",
        username = "testuser",
        password = "password",
      )

      val logoutResult = repository.logoutSession("server-1_user-1")
      logoutResult.isSuccess() shouldBe true

      val activeResult = repository.getActiveSession()
      (activeResult as JellyfinResult.Success).value shouldBe null
    }
  }

  @Test
  fun logout_server_removes_all_sessions() {
    runTest {
      fakeAuthService.authenticateResult = JellyfinResult.Success(
        AuthenticationResult(
          accessToken = "token-1",
          user = UserDto(id = "user-1", name = "user1"),
          serverId = "server-1",
        ),
      )
      repository.login(
        serverId = "server-1",
        serverUrl = "https://jellyfin.example.com",
        username = "user1",
        password = "password",
      )

      fakeAuthService.authenticateResult = JellyfinResult.Success(
        AuthenticationResult(
          accessToken = "token-2",
          user = UserDto(id = "user-2", name = "user2"),
          serverId = "server-1",
        ),
      )
      repository.login(
        serverId = "server-1",
        serverUrl = "https://jellyfin.example.com",
        username = "user2",
        password = "password",
      )

      val logoutResult = repository.logoutServer("server-1")
      logoutResult.isSuccess() shouldBe true

      val sessions = repository.getSessionsForServer("server-1")
      (sessions as JellyfinResult.Success).value.shouldBeEmpty()
    }
  }

  @Test
  fun observe_active_session_emits_updates() {
    runTest {
      val initial = repository.observeActiveSession().first()
      initial shouldBe null

      fakeAuthService.authenticateResult = JellyfinResult.Success(
        AuthenticationResult(
          accessToken = "token-123",
          user = UserDto(id = "user-1", name = "testuser"),
          serverId = "server-1",
        ),
      )
      repository.login(
        serverId = "server-1",
        serverUrl = "https://jellyfin.example.com",
        username = "testuser",
        password = "password",
      )

      val afterLogin = repository.observeActiveSession().first()
      afterLogin?.userId shouldBe "user-1"
    }
  }

  @Test
  fun initiate_quick_connect_returns_state() {
    runTest {
      fakeAuthService.quickConnectResult = JellyfinResult.Success(
        SdkQuickConnectResult(
          code = "ABC123",
          secret = "secret-xyz",
        ),
      )

      val result = repository.initiateQuickConnect(
        serverUrl = "https://jellyfin.example.com",
      )

      result.isSuccess() shouldBe true
      val state = (result as JellyfinResult.Success).value
      state.code shouldBe "ABC123"
      state.secret shouldBe "secret-xyz"
    }
  }

  @Test
  fun check_quick_connect_pending() {
    runTest {
      fakeAuthService.quickConnectStatusResult = JellyfinResult.Success(
        SdkQuickConnectResult(
          authenticated = false,
          secret = "secret-xyz",
        ),
      )

      val result = repository.checkQuickConnect(
        serverId = "server-1",
        serverUrl = "https://jellyfin.example.com",
        secret = "secret-xyz",
      )

      result.isSuccess() shouldBe true
      (result as JellyfinResult.Success).value.shouldBeInstanceOf<QuickConnectResult.Pending>()
    }
  }

  @Test
  fun check_quick_connect_authenticated() {
    runTest {
      fakeAuthService.quickConnectStatusResult = JellyfinResult.Success(
        SdkQuickConnectResult(
          authenticated = true,
          secret = "secret-xyz",
        ),
      )
      fakeAuthService.authenticateResult = JellyfinResult.Success(
        AuthenticationResult(
          accessToken = "token-qc",
          user = UserDto(id = "user-qc", name = "qcuser"),
          serverId = "server-1",
        ),
      )

      val result = repository.checkQuickConnect(
        serverId = "server-1",
        serverUrl = "https://jellyfin.example.com",
        secret = "secret-xyz",
      )

      result.isSuccess() shouldBe true
      val authenticated = (result as JellyfinResult.Success).value
      authenticated.shouldBeInstanceOf<QuickConnectResult.Authenticated>()
      authenticated.session.userId shouldBe "user-qc"
      authenticated.session.accessToken shouldBe "token-qc"
    }
  }
}

/**
 * Fake implementation of [JellyfinAuthService] for testing.
 */
private class FakeJellyfinAuthService : JellyfinAuthService {
  var authenticateResult: JellyfinResult<AuthenticationResult> =
    JellyfinResult.Error(message = "Not configured", isEphemeral = false)

  var quickConnectResult: JellyfinResult<SdkQuickConnectResult> =
    JellyfinResult.Error(message = "Not configured", isEphemeral = false)

  var quickConnectStatusResult: JellyfinResult<SdkQuickConnectResult> =
    JellyfinResult.Error(message = "Not configured", isEphemeral = false)

  override suspend fun authenticateByName(
    serverUrl: String,
    username: String,
    password: String,
  ): JellyfinResult<AuthenticationResult> = authenticateResult

  override suspend fun getCurrentUser(): JellyfinResult<UserDto> =
    JellyfinResult.Error(message = "Not implemented", isEphemeral = false)

  override suspend fun getPublicUsers(serverUrl: String): JellyfinResult<List<UserDto>> =
    JellyfinResult.Error(message = "Not implemented", isEphemeral = false)

  override suspend fun initiateQuickConnect(
    serverUrl: String,
  ): JellyfinResult<SdkQuickConnectResult> = quickConnectResult

  override suspend fun getQuickConnectStatus(
    serverUrl: String,
    secret: String,
  ): JellyfinResult<SdkQuickConnectResult> = quickConnectStatusResult

  override suspend fun logout(): JellyfinResult<Unit> = JellyfinResult.Success(Unit)
}
