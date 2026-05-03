package com.eygraber.jellyfin.data.admin.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.common.flatMapSuccessTo
import com.eygraber.jellyfin.common.mapSuccessTo
import com.eygraber.jellyfin.data.admin.ActivityLogSeverity
import com.eygraber.jellyfin.data.admin.AdminServerConfiguration
import com.eygraber.jellyfin.data.admin.AdminServerInfo
import com.eygraber.jellyfin.data.admin.AdminUser
import com.eygraber.jellyfin.data.admin.AdminUserPolicy
import com.eygraber.jellyfin.data.admin.ManagedLibrary
import com.eygraber.jellyfin.data.admin.ScheduledTask
import com.eygraber.jellyfin.data.admin.ScheduledTaskExecution
import com.eygraber.jellyfin.data.admin.ScheduledTaskExecutionStatus
import com.eygraber.jellyfin.data.admin.ScheduledTaskState
import com.eygraber.jellyfin.data.admin.ServerActivityEntry
import com.eygraber.jellyfin.data.admin.ServerActivityPage
import com.eygraber.jellyfin.sdk.core.model.ActivityLogEntry
import com.eygraber.jellyfin.sdk.core.model.ScheduledTaskInfo
import com.eygraber.jellyfin.sdk.core.model.ServerConfigurationDto
import com.eygraber.jellyfin.sdk.core.model.SystemInfo
import com.eygraber.jellyfin.sdk.core.model.TaskResult
import com.eygraber.jellyfin.sdk.core.model.UserDto
import com.eygraber.jellyfin.sdk.core.model.UserPolicy
import com.eygraber.jellyfin.sdk.core.model.VirtualFolderInfo
import com.eygraber.jellyfin.services.sdk.JellyfinAdminService
import dev.zacsweers.metro.Inject

/**
 * Remote data source for admin operations.
 *
 * Wraps [JellyfinAdminService] and maps SDK DTOs into the data layer's
 * domain entities. DTOs that lack required fields (e.g. a user with no ID)
 * are dropped from list results rather than surfaced as malformed entities.
 */
@Suppress("TooManyFunctions")
@Inject
class AdminRemoteDataSource(
  private val adminService: JellyfinAdminService,
) {
  // -- Users --

  suspend fun getUsers(
    isHidden: Boolean?,
    isDisabled: Boolean?,
  ): JellyfinResult<List<AdminUser>> =
    adminService.getUsers(
      isHidden = isHidden,
      isDisabled = isDisabled,
    ).mapSuccessTo {
      mapNotNull { it.toAdminUser() }
    }

  suspend fun getUser(userId: String): JellyfinResult<AdminUser> =
    adminService.getUser(userId = userId).flatMapSuccessTo {
      toAdminUser()?.let { JellyfinResult.Success(it) }
        ?: missingFieldError(message = "User $userId is missing required fields")
    }

  suspend fun createUser(name: String, password: String?): JellyfinResult<AdminUser> =
    adminService.createUser(name = name, password = password).flatMapSuccessTo {
      toAdminUser()?.let { JellyfinResult.Success(it) }
        ?: missingFieldError(message = "Created user is missing required fields")
    }

  suspend fun updateUserPolicy(
    userId: String,
    policy: AdminUserPolicy,
  ): JellyfinResult<Unit> =
    adminService.updateUserPolicy(
      userId = userId,
      policy = policy.toDto(),
    )

  suspend fun setUserPassword(
    userId: String,
    newPassword: String,
    currentPassword: String?,
    resetPassword: Boolean,
  ): JellyfinResult<Unit> =
    adminService.updateUserPassword(
      userId = userId,
      newPassword = newPassword,
      currentPassword = currentPassword,
      resetPassword = resetPassword,
    )

  suspend fun deleteUser(userId: String): JellyfinResult<Unit> =
    adminService.deleteUser(userId = userId)

  // -- Libraries --

  suspend fun getLibraries(): JellyfinResult<List<ManagedLibrary>> =
    adminService.getVirtualFolders().mapSuccessTo {
      map { it.toManagedLibrary() }
    }

  suspend fun refreshLibrary(): JellyfinResult<Unit> =
    adminService.refreshLibrary()

  // -- Server --

  suspend fun getServerInfo(): JellyfinResult<AdminServerInfo> =
    adminService.getSystemInfo().mapSuccessTo {
      toAdminServerInfo()
    }

  suspend fun getConfiguration(): JellyfinResult<AdminServerConfiguration> =
    adminService.getConfiguration().mapSuccessTo {
      toAdminServerConfiguration()
    }

  /**
   * Updates the editable subset of server configuration without losing
   * unknown fields: fetches the current full configuration, applies the
   * caller's overrides, and posts the merged result.
   */
  suspend fun updateConfiguration(
    configuration: AdminServerConfiguration,
  ): JellyfinResult<Unit> =
    adminService.getConfiguration().flatMapSuccessTo {
      adminService.updateConfiguration(
        configuration = applyOverrides(current = this, overrides = configuration),
      )
    }

  suspend fun restartServer(): JellyfinResult<Unit> =
    adminService.restartServer()

  suspend fun shutdownServer(): JellyfinResult<Unit> =
    adminService.shutdownServer()

  // -- Scheduled tasks --

  suspend fun getScheduledTasks(
    isHidden: Boolean?,
    isEnabled: Boolean?,
  ): JellyfinResult<List<ScheduledTask>> =
    adminService.getScheduledTasks(
      isHidden = isHidden,
      isEnabled = isEnabled,
    ).mapSuccessTo {
      mapNotNull { it.toScheduledTask() }
    }

  suspend fun getScheduledTask(taskId: String): JellyfinResult<ScheduledTask> =
    adminService.getScheduledTask(taskId = taskId).flatMapSuccessTo {
      toScheduledTask()?.let { JellyfinResult.Success(it) }
        ?: missingFieldError(message = "Task $taskId is missing required fields")
    }

  suspend fun startScheduledTask(taskId: String): JellyfinResult<Unit> =
    adminService.startScheduledTask(taskId = taskId)

  suspend fun stopScheduledTask(taskId: String): JellyfinResult<Unit> =
    adminService.stopScheduledTask(taskId = taskId)

  // -- Activity log --

  suspend fun getActivityLog(
    startIndex: Int,
    limit: Int,
    minDate: String?,
    hasUserId: Boolean?,
  ): JellyfinResult<ServerActivityPage> =
    adminService.getActivityLogEntries(
      startIndex = startIndex,
      limit = limit,
      minDate = minDate,
      hasUserId = hasUserId,
    ).mapSuccessTo {
      ServerActivityPage(
        items = items.mapNotNull { it.toServerActivityEntry() },
        totalRecordCount = totalRecordCount,
        startIndex = this.startIndex,
      )
    }
}

/**
 * Wraps a "server returned a DTO without a required field" condition as a
 * non-ephemeral [JellyfinResult.Error].
 *
 * This shouldn't happen in practice - the SDK contract is that single-item
 * lookups always return an item with an ID - but we surface it as an error
 * rather than crashing the coroutine.
 */
private fun <T> missingFieldError(message: String): JellyfinResult<T> = JellyfinResult.Error(
  message = message,
  isEphemeral = false,
)

private fun UserDto.toAdminUser(): AdminUser? {
  val userId = id ?: return null
  val userName = name ?: return null
  val sourcePolicy = policy ?: UserPolicy()

  return AdminUser(
    id = userId,
    name = userName,
    hasPassword = hasPassword,
    hasConfiguredPassword = hasConfiguredPassword,
    policy = AdminUserPolicy(
      isAdministrator = sourcePolicy.isAdministrator,
      isHidden = sourcePolicy.isHidden,
      isDisabled = sourcePolicy.isDisabled,
      enableUserPreferenceAccess = sourcePolicy.enableUserPreferenceAccess,
      enableRemoteAccess = sourcePolicy.enableRemoteAccess,
    ),
  )
}

private fun AdminUserPolicy.toDto(): UserPolicy = UserPolicy(
  isAdministrator = isAdministrator,
  isHidden = isHidden,
  isDisabled = isDisabled,
  enableUserPreferenceAccess = enableUserPreferenceAccess,
  enableRemoteAccess = enableRemoteAccess,
)

private fun VirtualFolderInfo.toManagedLibrary(): ManagedLibrary = ManagedLibrary(
  itemId = itemId,
  name = name.orEmpty(),
  collectionType = collectionType,
  locations = locations,
  refreshProgressPercent = refreshProgress,
  refreshStatus = refreshStatus,
  primaryImageItemId = primaryImageItemId,
)

private fun SystemInfo.toAdminServerInfo(): AdminServerInfo = AdminServerInfo(
  id = id.orEmpty(),
  serverName = serverName.orEmpty(),
  version = version.orEmpty(),
  operatingSystem = operatingSystemDisplayName ?: operatingSystem,
  productName = productName,
  hasPendingRestart = hasPendingRestart,
  canSelfRestart = canSelfRestart,
  isShuttingDown = isShuttingDown,
  cachePath = cachePath,
  logPath = logPath,
)

private fun ServerConfigurationDto.toAdminServerConfiguration(): AdminServerConfiguration =
  AdminServerConfiguration(
    serverName = serverName,
    preferredMetadataLanguage = preferredMetadataLanguage,
    metadataCountryCode = metadataCountryCode,
    cachePath = cachePath,
    metadataPath = metadataPath,
    minResumePct = minResumePct,
    maxResumePct = maxResumePct,
    minResumeDurationSeconds = minResumeDurationSeconds,
    libraryMonitorDelay = libraryMonitorDelay,
    quickConnectAvailable = quickConnectAvailable,
    enableMetrics = enableMetrics,
  )

private fun applyOverrides(
  current: ServerConfigurationDto,
  overrides: AdminServerConfiguration,
): ServerConfigurationDto = current.copy(
  serverName = overrides.serverName,
  preferredMetadataLanguage = overrides.preferredMetadataLanguage,
  metadataCountryCode = overrides.metadataCountryCode,
  cachePath = overrides.cachePath,
  metadataPath = overrides.metadataPath,
  minResumePct = overrides.minResumePct,
  maxResumePct = overrides.maxResumePct,
  minResumeDurationSeconds = overrides.minResumeDurationSeconds,
  libraryMonitorDelay = overrides.libraryMonitorDelay,
  quickConnectAvailable = overrides.quickConnectAvailable,
  enableMetrics = overrides.enableMetrics,
)

private fun ScheduledTaskInfo.toScheduledTask(): ScheduledTask? {
  val taskId = id ?: return null
  return ScheduledTask(
    id = taskId,
    name = name.orEmpty(),
    description = description,
    category = category,
    key = key,
    state = ScheduledTaskState.fromApiValue(state),
    currentProgressPercent = currentProgressPercentage,
    isHidden = isHidden,
    lastExecution = lastExecutionResult?.toExecution(),
  )
}

private fun TaskResult.toExecution(): ScheduledTaskExecution = ScheduledTaskExecution(
  startTimeUtc = startTimeUtc,
  endTimeUtc = endTimeUtc,
  status = ScheduledTaskExecutionStatus.fromApiValue(status),
  errorMessage = errorMessage ?: longErrorMessage,
)

private fun ActivityLogEntry.toServerActivityEntry(): ServerActivityEntry? {
  val entryId = id ?: return null
  return ServerActivityEntry(
    id = entryId,
    name = name.orEmpty(),
    overview = overview,
    shortOverview = shortOverview,
    type = type,
    date = date,
    severity = ActivityLogSeverity.fromApiValue(severity),
    userId = userId,
    userPrimaryImageTag = userPrimaryImageTag,
    itemId = itemId,
  )
}
