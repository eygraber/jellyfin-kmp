package com.eygraber.jellyfin.data.admin.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.common.isError
import com.eygraber.jellyfin.common.successOrNull
import com.eygraber.jellyfin.data.admin.ActivityLogSeverity
import com.eygraber.jellyfin.data.admin.AdminServerConfiguration
import com.eygraber.jellyfin.data.admin.AdminUserPolicy
import com.eygraber.jellyfin.data.admin.ScheduledTaskExecutionStatus
import com.eygraber.jellyfin.data.admin.ScheduledTaskState
import com.eygraber.jellyfin.sdk.core.model.ActivityLogEntry
import com.eygraber.jellyfin.sdk.core.model.ActivityLogEntryQueryResult
import com.eygraber.jellyfin.sdk.core.model.ScheduledTaskInfo
import com.eygraber.jellyfin.sdk.core.model.ServerConfigurationDto
import com.eygraber.jellyfin.sdk.core.model.SystemInfo
import com.eygraber.jellyfin.sdk.core.model.TaskResult
import com.eygraber.jellyfin.sdk.core.model.UserDto
import com.eygraber.jellyfin.sdk.core.model.UserPolicy
import com.eygraber.jellyfin.sdk.core.model.VirtualFolderInfo
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class AdminRemoteDataSourceTest {
  private lateinit var fakeService: FakeJellyfinAdminService
  private lateinit var dataSource: AdminRemoteDataSource

  @BeforeTest
  fun setUp() {
    fakeService = FakeJellyfinAdminService()
    dataSource = AdminRemoteDataSource(adminService = fakeService)
  }

  // -- Users --

  @Test
  fun getUsers_maps_dtos_and_drops_users_without_id() {
    runTest {
      fakeService.usersResult = JellyfinResult.Success(
        listOf(
          UserDto(
            id = "user-1",
            name = "alice",
            hasPassword = true,
            policy = UserPolicy(isAdministrator = true, isHidden = false),
          ),
          UserDto(id = null, name = "no-id"),
          UserDto(id = "user-2", name = null),
        ),
      )

      val result = dataSource.getUsers(isHidden = true, isDisabled = false)

      val users = result.successOrNull.shouldNotBeNull()
      users.size shouldBe 1
      users[0].id shouldBe "user-1"
      users[0].name shouldBe "alice"
      users[0].hasPassword.shouldBeTrue()
      users[0].policy.isAdministrator.shouldBeTrue()

      fakeService.lastGetUsersIsHidden shouldBe true
      fakeService.lastGetUsersIsDisabled shouldBe false
    }
  }

  @Test
  fun getUser_propagates_error() {
    runTest {
      fakeService.singleUserResult = JellyfinResult.Error(message = "404", isEphemeral = false)

      val result = dataSource.getUser(userId = "missing")

      result.isError().shouldBeTrue()
    }
  }

  @Test
  fun createUser_passes_name_and_password() {
    runTest {
      fakeService.createUserResult = JellyfinResult.Success(
        UserDto(id = "user-new", name = "bob", hasPassword = true),
      )

      val result = dataSource.createUser(name = "bob", password = "secret")

      val user = result.successOrNull.shouldNotBeNull()
      user.id shouldBe "user-new"
      user.name shouldBe "bob"
      fakeService.lastCreateUserName shouldBe "bob"
      fakeService.lastCreateUserPassword shouldBe "secret"
    }
  }

  @Test
  fun updateUserPolicy_maps_policy_to_dto() {
    runTest {
      val policy = AdminUserPolicy(
        isAdministrator = true,
        isHidden = true,
        isDisabled = false,
        enableUserPreferenceAccess = false,
        enableRemoteAccess = true,
      )

      dataSource.updateUserPolicy(userId = "user-1", policy = policy)

      fakeService.lastUpdatePolicyUserId shouldBe "user-1"
      val sent = fakeService.lastUpdatePolicyDto.shouldNotBeNull()
      sent.isAdministrator.shouldBeTrue()
      sent.isHidden.shouldBeTrue()
      sent.isDisabled.shouldBeFalse()
      sent.enableUserPreferenceAccess.shouldBeFalse()
      sent.enableRemoteAccess.shouldBeTrue()
    }
  }

  @Test
  fun setUserPassword_passes_reset_flag() {
    runTest {
      dataSource.setUserPassword(
        userId = "user-1",
        newPassword = "",
        currentPassword = null,
        resetPassword = true,
      )

      fakeService.lastPasswordUserId shouldBe "user-1"
      fakeService.lastPasswordReset shouldBe true
    }
  }

  @Test
  fun deleteUser_delegates_to_service() {
    runTest {
      dataSource.deleteUser(userId = "user-1")
      fakeService.lastDeletedUserId shouldBe "user-1"
    }
  }

  // -- Libraries --

  @Test
  fun getLibraries_maps_virtual_folders() {
    runTest {
      fakeService.virtualFoldersResult = JellyfinResult.Success(
        listOf(
          VirtualFolderInfo(
            name = "Movies",
            itemId = "lib-1",
            collectionType = "movies",
            locations = listOf("/media/movies"),
            refreshProgress = 50.0,
            refreshStatus = "Running",
            primaryImageItemId = "img-1",
          ),
        ),
      )

      val result = dataSource.getLibraries()

      val libs = result.successOrNull.shouldNotBeNull()
      libs.size shouldBe 1
      val lib = libs[0]
      lib.name shouldBe "Movies"
      lib.itemId shouldBe "lib-1"
      lib.collectionType shouldBe "movies"
      lib.locations shouldBe listOf("/media/movies")
      lib.refreshProgressPercent shouldBe 50.0
      lib.refreshStatus shouldBe "Running"
      lib.primaryImageItemId shouldBe "img-1"
    }
  }

  @Test
  fun refreshLibrary_delegates_to_service() {
    runTest {
      dataSource.refreshLibrary()
      fakeService.refreshLibraryCount shouldBe 1
    }
  }

  // -- Server --

  @Test
  fun getServerInfo_maps_system_info() {
    runTest {
      fakeService.systemInfoResult = JellyfinResult.Success(
        SystemInfo(
          id = "srv-1",
          serverName = "Jellyfin",
          version = "10.9.0",
          operatingSystemDisplayName = "Linux",
          productName = "Jellyfin Server",
          hasPendingRestart = true,
          canSelfRestart = true,
          isShuttingDown = false,
          cachePath = "/var/cache",
          logPath = "/var/log",
        ),
      )

      val info = dataSource.getServerInfo().successOrNull.shouldNotBeNull()
      info.id shouldBe "srv-1"
      info.serverName shouldBe "Jellyfin"
      info.version shouldBe "10.9.0"
      info.operatingSystem shouldBe "Linux"
      info.productName shouldBe "Jellyfin Server"
      info.hasPendingRestart.shouldBeTrue()
      info.canSelfRestart.shouldBeTrue()
      info.isShuttingDown.shouldBeFalse()
      info.cachePath shouldBe "/var/cache"
      info.logPath shouldBe "/var/log"
    }
  }

  @Test
  fun getServerInfo_falls_back_to_operating_system_when_display_name_missing() {
    runTest {
      fakeService.systemInfoResult = JellyfinResult.Success(
        SystemInfo(
          id = "srv-1",
          serverName = "Jellyfin",
          version = "10.9.0",
          operatingSystemDisplayName = null,
          operatingSystem = "Unix",
        ),
      )

      val info = dataSource.getServerInfo().successOrNull.shouldNotBeNull()
      info.operatingSystem shouldBe "Unix"
    }
  }

  @Test
  fun getConfiguration_maps_subset_of_dto() {
    runTest {
      fakeService.configurationResult = JellyfinResult.Success(
        ServerConfigurationDto(
          serverName = "Home",
          preferredMetadataLanguage = "en",
          metadataCountryCode = "US",
          cachePath = "/cache",
          metadataPath = "/meta",
          minResumePct = 5,
          maxResumePct = 90,
          minResumeDurationSeconds = 300,
          libraryMonitorDelay = 60,
          quickConnectAvailable = true,
          enableMetrics = true,
        ),
      )

      val config = dataSource.getConfiguration().successOrNull.shouldNotBeNull()
      config.serverName shouldBe "Home"
      config.preferredMetadataLanguage shouldBe "en"
      config.metadataCountryCode shouldBe "US"
      config.minResumePct shouldBe 5
      config.maxResumePct shouldBe 90
      config.quickConnectAvailable.shouldBeTrue()
      config.enableMetrics.shouldBeTrue()
    }
  }

  @Test
  fun updateConfiguration_preserves_unknown_fields() {
    runTest {
      val current = ServerConfigurationDto(
        serverName = "Old",
        preferredMetadataLanguage = "fr",
        sortReplaceCharacters = listOf(",", "."),
        sortRemoveCharacters = listOf("-"),
        sortRemoveWords = listOf("the"),
        inactiveSessionThreshold = 1234,
      )
      fakeService.configurationResult = JellyfinResult.Success(current)

      val overrides = AdminServerConfiguration(
        serverName = "New",
        preferredMetadataLanguage = "en",
        metadataCountryCode = "US",
        cachePath = null,
        metadataPath = null,
        minResumePct = 10,
        maxResumePct = 95,
        minResumeDurationSeconds = 120,
        libraryMonitorDelay = 30,
        quickConnectAvailable = false,
        enableMetrics = false,
      )

      dataSource.updateConfiguration(configuration = overrides)

      val sent = fakeService.lastUpdatedConfiguration.shouldNotBeNull()
      // Overridden fields are applied.
      sent.serverName shouldBe "New"
      sent.preferredMetadataLanguage shouldBe "en"
      sent.metadataCountryCode shouldBe "US"
      sent.minResumePct shouldBe 10
      sent.maxResumePct shouldBe 95
      // Unknown fields are preserved from the current configuration.
      sent.sortReplaceCharacters shouldBe listOf(",", ".")
      sent.sortRemoveCharacters shouldBe listOf("-")
      sent.sortRemoveWords shouldBe listOf("the")
      sent.inactiveSessionThreshold shouldBe 1234
    }
  }

  @Test
  fun updateConfiguration_propagates_fetch_failure() {
    runTest {
      fakeService.configurationResult = JellyfinResult.Error(
        message = "Forbidden",
        isEphemeral = false,
      )

      val overrides = AdminServerConfiguration(
        serverName = "x",
        preferredMetadataLanguage = null,
        metadataCountryCode = null,
        cachePath = null,
        metadataPath = null,
        minResumePct = 0,
        maxResumePct = 0,
        minResumeDurationSeconds = 0,
        libraryMonitorDelay = 0,
        quickConnectAvailable = false,
        enableMetrics = false,
      )

      val result = dataSource.updateConfiguration(configuration = overrides)

      result.isError().shouldBeTrue()
      // Should not have attempted to update when fetch failed.
      fakeService.lastUpdatedConfiguration shouldBe null
    }
  }

  @Test
  fun restartServer_and_shutdownServer_delegate() {
    runTest {
      dataSource.restartServer()
      dataSource.shutdownServer()

      fakeService.restartServerCount shouldBe 1
      fakeService.shutdownServerCount shouldBe 1
    }
  }

  // -- Scheduled tasks --

  @Test
  fun getScheduledTasks_maps_state_and_drops_tasks_without_id() {
    runTest {
      fakeService.scheduledTasksResult = JellyfinResult.Success(
        listOf(
          ScheduledTaskInfo(
            id = "task-1",
            name = "Refresh",
            description = "Refreshes things",
            category = "Library",
            key = "key-1",
            state = "Running",
            currentProgressPercentage = 25.0,
            isHidden = false,
            lastExecutionResult = TaskResult(
              startTimeUtc = "2024-01-01T00:00:00Z",
              endTimeUtc = "2024-01-01T01:00:00Z",
              status = "Completed",
            ),
          ),
          ScheduledTaskInfo(id = null),
          ScheduledTaskInfo(id = "task-2", state = "Idle"),
        ),
      )

      val tasks = dataSource.getScheduledTasks(
        isHidden = null,
        isEnabled = null,
      ).successOrNull.shouldNotBeNull()

      tasks.size shouldBe 2
      val running = tasks[0]
      running.id shouldBe "task-1"
      running.state shouldBe ScheduledTaskState.Running
      running.currentProgressPercent shouldBe 25.0
      running.lastExecution.shouldNotBeNull().status shouldBe ScheduledTaskExecutionStatus.Completed

      tasks[1].state shouldBe ScheduledTaskState.Idle
    }
  }

  @Test
  fun getScheduledTask_maps_unknown_state_to_unknown() {
    runTest {
      fakeService.singleTaskResult = JellyfinResult.Success(
        ScheduledTaskInfo(id = "task-x", state = "FrobnicatingFurther"),
      )

      val task = dataSource.getScheduledTask(taskId = "task-x").successOrNull.shouldNotBeNull()
      task.state shouldBe ScheduledTaskState.Unknown
    }
  }

  @Test
  fun start_and_stop_scheduled_task_delegate() {
    runTest {
      dataSource.startScheduledTask(taskId = "task-1")
      dataSource.stopScheduledTask(taskId = "task-2")

      fakeService.lastStartedTaskId shouldBe "task-1"
      fakeService.lastStoppedTaskId shouldBe "task-2"
    }
  }

  // -- Activity log --

  @Test
  fun getActivityLog_maps_entries_and_passes_query_params() {
    runTest {
      fakeService.activityLogResult = JellyfinResult.Success(
        ActivityLogEntryQueryResult(
          items = listOf(
            ActivityLogEntry(
              id = 1L,
              name = "Login",
              overview = "user logged in",
              shortOverview = "login",
              type = "SessionStarted",
              date = "2024-01-01T00:00:00Z",
              userId = "user-1",
              userPrimaryImageTag = "tag",
              itemId = "item-1",
              severity = "Information",
            ),
            ActivityLogEntry(id = null, name = "missing"),
            ActivityLogEntry(id = 2L, name = "Mystery", severity = "WarpFactor"),
          ),
          totalRecordCount = 3,
          startIndex = 0,
        ),
      )

      val page = dataSource.getActivityLog(
        startIndex = 0,
        limit = 50,
        minDate = "2024-01-01T00:00:00Z",
        hasUserId = true,
      ).successOrNull.shouldNotBeNull()

      page.items.size shouldBe 2
      page.totalRecordCount shouldBe 3
      page.startIndex shouldBe 0
      val entry = page.items[0]
      entry.id shouldBe 1L
      entry.name shouldBe "Login"
      entry.severity shouldBe ActivityLogSeverity.Information
      // Unknown severity falls back to Unknown.
      page.items[1].severity shouldBe ActivityLogSeverity.Unknown

      fakeService.lastActivityStartIndex shouldBe 0
      fakeService.lastActivityLimit shouldBe 50
      fakeService.lastActivityMinDate shouldBe "2024-01-01T00:00:00Z"
      fakeService.lastActivityHasUserId shouldBe true
    }
  }
}
