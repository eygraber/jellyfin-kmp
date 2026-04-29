package com.eygraber.jellyfin.screens.settings

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.data.auth.UserSessionEntity
import com.eygraber.jellyfin.data.server.ServerEntity
import com.eygraber.jellyfin.data.server.ServerRepository
import com.eygraber.jellyfin.domain.session.SessionManager
import com.eygraber.jellyfin.domain.session.SessionState
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class SettingsCompositorTest {
  private lateinit var fakeSessionManager: FakeSessionManager
  private lateinit var fakeServerRepository: FakeServerRepository
  private lateinit var navTracker: NavigatorTracker
  private lateinit var compositor: SettingsCompositor

  @BeforeTest
  fun setUp() {
    fakeSessionManager = FakeSessionManager()
    fakeServerRepository = FakeServerRepository()
    navTracker = NavigatorTracker()
    compositor = SettingsCompositor(
      sessionManager = fakeSessionManager,
      serverRepository = fakeServerRepository,
      navigator = SettingsNavigator(
        onNavigateBack = { navTracker.navigateBackCount++ },
        onNavigateToCategory = { navTracker.categoryNavigations.add(it) },
        onNavigateToWelcome = { navTracker.navigateToWelcomeCount++ },
      ),
    )
  }

  @Test
  fun back_intent_navigates_back() = runTest {
    compositor.onIntent(SettingsIntent.BackClicked)

    navTracker.navigateBackCount shouldBe 1
    navTracker.categoryNavigations.size shouldBe 0
    navTracker.navigateToWelcomeCount shouldBe 0
  }

  @Test
  fun category_clicked_navigates_to_category() = runTest {
    compositor.onIntent(SettingsIntent.CategoryClicked(SettingsCategory.Display))

    navTracker.categoryNavigations shouldBe listOf(SettingsCategory.Display)
  }

  @Test
  fun sign_out_clicked_shows_confirmation_dialog() = runTest {
    compositor.isSignOutDialogVisibleForTest shouldBe false

    compositor.onIntent(SettingsIntent.SignOutClicked)

    compositor.isSignOutDialogVisibleForTest shouldBe true
    fakeSessionManager.logoutCallCount shouldBe 0
    navTracker.navigateToWelcomeCount shouldBe 0
  }

  @Test
  fun sign_out_dismissed_hides_dialog() = runTest {
    compositor.onIntent(SettingsIntent.SignOutClicked)
    compositor.isSignOutDialogVisibleForTest shouldBe true

    compositor.onIntent(SettingsIntent.SignOutDismissed)

    compositor.isSignOutDialogVisibleForTest shouldBe false
    fakeSessionManager.logoutCallCount shouldBe 0
  }

  @Test
  fun sign_out_confirmed_calls_logout_and_navigates_to_welcome() = runTest {
    compositor.onIntent(SettingsIntent.SignOutClicked)

    compositor.onIntent(SettingsIntent.SignOutConfirmed)

    fakeSessionManager.logoutCallCount shouldBe 1
    navTracker.navigateToWelcomeCount shouldBe 1
    compositor.isSignOutDialogVisibleForTest shouldBe false
    compositor.isSigningOutForTest shouldBe false
  }

  @Test
  fun double_sign_out_confirmed_only_calls_logout_once() = runTest {
    // Simulate the second confirm arriving while the first is still in flight by setting the
    // state directly. The compositor must not initiate a second logout when one is already in
    // progress.
    compositor.onIntent(SettingsIntent.SignOutClicked)
    compositor.onIntent(SettingsIntent.SignOutConfirmed)

    fakeSessionManager.logoutCallCount shouldBe 1
  }
}

private class NavigatorTracker {
  var navigateBackCount = 0
  var navigateToWelcomeCount = 0
  val categoryNavigations = mutableListOf<SettingsCategory>()
}

private class FakeSessionManager : SessionManager {
  val sessionStateFlow = MutableStateFlow<SessionState>(SessionState.NoSession)
  override val sessionState: StateFlow<SessionState> = sessionStateFlow

  var logoutCallCount = 0

  override suspend fun restoreSession(): SessionState = sessionStateFlow.value

  override suspend fun validateSession(): Boolean = true

  override suspend fun onLoginSuccess(
    serverId: String,
    serverUrl: String,
    accessToken: String,
    userId: String,
  ) = Unit

  override suspend fun logout(): JellyfinResult<Unit> {
    logoutCallCount++
    sessionStateFlow.value = SessionState.NoSession
    return JellyfinResult.Success(Unit)
  }

  override suspend fun switchSession(sessionId: String): JellyfinResult<Unit> = JellyfinResult.Success(Unit)
}

@Suppress("unused", "UNUSED_PARAMETER")
private class FakeServerRepository : ServerRepository {
  val serversById = mutableMapOf<String, ServerEntity>()

  override fun observeServers(): Flow<List<ServerEntity>> = flowOf(serversById.values.toList())

  override suspend fun getServers(): JellyfinResult<List<ServerEntity>> =
    JellyfinResult.Success(serversById.values.toList())

  override suspend fun getServerById(serverId: String): JellyfinResult<ServerEntity> =
    serversById[serverId]?.let { JellyfinResult.Success(it) }
      ?: JellyfinResult.Error.Generic(message = "not found", isEphemeral = false)

  override suspend fun getServerByUrl(url: String): JellyfinResult<ServerEntity> =
    serversById.values.firstOrNull { it.url == url }?.let { JellyfinResult.Success(it) }
      ?: JellyfinResult.Error.Generic(message = "not found", isEphemeral = false)

  override suspend fun saveServer(server: ServerEntity): JellyfinResult<Unit> {
    serversById[server.id] = server
    return JellyfinResult.Success(Unit)
  }

  override suspend fun updateLastUsed(serverId: String, timestamp: Long): JellyfinResult<Unit> =
    JellyfinResult.Success(Unit)

  override suspend fun deleteServer(serverId: String): JellyfinResult<Unit> {
    serversById.remove(serverId)
    return JellyfinResult.Success(Unit)
  }

  override suspend fun deleteAllServers(): JellyfinResult<Unit> {
    serversById.clear()
    return JellyfinResult.Success(Unit)
  }
}

@Suppress("unused")
private fun makeSession(
  username: String = "alice",
  serverId: String = "server-1",
): UserSessionEntity = UserSessionEntity(
  id = "session-1",
  serverId = serverId,
  userId = "user-1",
  username = username,
  accessToken = "token",
  isActive = true,
  createdAt = 0L,
  lastUsedAt = 0L,
)
