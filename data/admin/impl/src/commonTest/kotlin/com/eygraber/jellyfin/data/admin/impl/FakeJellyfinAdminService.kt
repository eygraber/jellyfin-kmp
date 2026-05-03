@file:Suppress("BooleanPropertyNaming")

package com.eygraber.jellyfin.data.admin.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.sdk.core.model.ActivityLogEntryQueryResult
import com.eygraber.jellyfin.sdk.core.model.ScheduledTaskInfo
import com.eygraber.jellyfin.sdk.core.model.ServerConfigurationDto
import com.eygraber.jellyfin.sdk.core.model.SystemInfo
import com.eygraber.jellyfin.sdk.core.model.UserDto
import com.eygraber.jellyfin.sdk.core.model.UserPolicy
import com.eygraber.jellyfin.sdk.core.model.VirtualFolderInfo
import com.eygraber.jellyfin.services.sdk.JellyfinAdminService

/**
 * Fake [JellyfinAdminService] for [AdminRemoteDataSource] tests.
 *
 * Tests set the public `*Result` properties to control return values and
 * inspect the `last*` properties to assert on call arguments.
 */
@Suppress("TooManyFunctions")
internal class FakeJellyfinAdminService : JellyfinAdminService {
  // Defaults are empty/success so tests only set what they care about.

  var usersResult: JellyfinResult<List<UserDto>> = JellyfinResult.Success(emptyList())
  var singleUserResult: JellyfinResult<UserDto> = JellyfinResult.Success(UserDto())
  var createUserResult: JellyfinResult<UserDto> = JellyfinResult.Success(UserDto())
  var virtualFoldersResult: JellyfinResult<List<VirtualFolderInfo>> =
    JellyfinResult.Success(emptyList())
  var systemInfoResult: JellyfinResult<SystemInfo> = JellyfinResult.Success(SystemInfo())
  var configurationResult: JellyfinResult<ServerConfigurationDto> =
    JellyfinResult.Success(ServerConfigurationDto())
  var scheduledTasksResult: JellyfinResult<List<ScheduledTaskInfo>> =
    JellyfinResult.Success(emptyList())
  var singleTaskResult: JellyfinResult<ScheduledTaskInfo> =
    JellyfinResult.Success(ScheduledTaskInfo())
  var activityLogResult: JellyfinResult<ActivityLogEntryQueryResult> =
    JellyfinResult.Success(ActivityLogEntryQueryResult())

  // -- Captured call arguments --
  var lastGetUsersIsHidden: Boolean? = null
  var lastGetUsersIsDisabled: Boolean? = null
  var lastGetUserId: String? = null
  var lastCreateUserName: String? = null
  var lastCreateUserPassword: String? = null
  var lastUpdateUserUserId: String? = null
  var lastUpdateUserDto: UserDto? = null
  var lastUpdatePolicyUserId: String? = null
  var lastUpdatePolicyDto: UserPolicy? = null
  var lastPasswordUserId: String? = null
  var lastPasswordNew: String? = null
  var lastPasswordCurrent: String? = null
  var lastPasswordReset: Boolean? = null
  var lastDeletedUserId: String? = null
  var refreshLibraryCount: Int = 0
  var lastUpdatedConfiguration: ServerConfigurationDto? = null
  var restartServerCount: Int = 0
  var shutdownServerCount: Int = 0
  var lastTasksIsHidden: Boolean? = null
  var lastTasksIsEnabled: Boolean? = null
  var lastTaskId: String? = null
  var lastStartedTaskId: String? = null
  var lastStoppedTaskId: String? = null
  var lastActivityStartIndex: Int? = null
  var lastActivityLimit: Int? = null
  var lastActivityMinDate: String? = null
  var lastActivityHasUserId: Boolean? = null

  override suspend fun getUsers(
    isHidden: Boolean?,
    isDisabled: Boolean?,
  ): JellyfinResult<List<UserDto>> {
    lastGetUsersIsHidden = isHidden
    lastGetUsersIsDisabled = isDisabled
    return usersResult
  }

  override suspend fun getUser(userId: String): JellyfinResult<UserDto> {
    lastGetUserId = userId
    return singleUserResult
  }

  override suspend fun createUser(name: String, password: String?): JellyfinResult<UserDto> {
    lastCreateUserName = name
    lastCreateUserPassword = password
    return createUserResult
  }

  override suspend fun updateUser(userId: String, user: UserDto): JellyfinResult<Unit> {
    lastUpdateUserUserId = userId
    lastUpdateUserDto = user
    return JellyfinResult.Success(Unit)
  }

  override suspend fun updateUserPolicy(
    userId: String,
    policy: UserPolicy,
  ): JellyfinResult<Unit> {
    lastUpdatePolicyUserId = userId
    lastUpdatePolicyDto = policy
    return JellyfinResult.Success(Unit)
  }

  override suspend fun updateUserPassword(
    userId: String,
    newPassword: String,
    currentPassword: String?,
    resetPassword: Boolean,
  ): JellyfinResult<Unit> {
    lastPasswordUserId = userId
    lastPasswordNew = newPassword
    lastPasswordCurrent = currentPassword
    lastPasswordReset = resetPassword
    return JellyfinResult.Success(Unit)
  }

  override suspend fun deleteUser(userId: String): JellyfinResult<Unit> {
    lastDeletedUserId = userId
    return JellyfinResult.Success(Unit)
  }

  override suspend fun getVirtualFolders(): JellyfinResult<List<VirtualFolderInfo>> =
    virtualFoldersResult

  override suspend fun refreshLibrary(): JellyfinResult<Unit> {
    refreshLibraryCount += 1
    return JellyfinResult.Success(Unit)
  }

  override suspend fun getSystemInfo(): JellyfinResult<SystemInfo> = systemInfoResult

  override suspend fun getConfiguration(): JellyfinResult<ServerConfigurationDto> =
    configurationResult

  override suspend fun updateConfiguration(
    configuration: ServerConfigurationDto,
  ): JellyfinResult<Unit> {
    lastUpdatedConfiguration = configuration
    return JellyfinResult.Success(Unit)
  }

  override suspend fun restartServer(): JellyfinResult<Unit> {
    restartServerCount += 1
    return JellyfinResult.Success(Unit)
  }

  override suspend fun shutdownServer(): JellyfinResult<Unit> {
    shutdownServerCount += 1
    return JellyfinResult.Success(Unit)
  }

  override suspend fun getScheduledTasks(
    isHidden: Boolean?,
    isEnabled: Boolean?,
  ): JellyfinResult<List<ScheduledTaskInfo>> {
    lastTasksIsHidden = isHidden
    lastTasksIsEnabled = isEnabled
    return scheduledTasksResult
  }

  override suspend fun getScheduledTask(taskId: String): JellyfinResult<ScheduledTaskInfo> {
    lastTaskId = taskId
    return singleTaskResult
  }

  override suspend fun startScheduledTask(taskId: String): JellyfinResult<Unit> {
    lastStartedTaskId = taskId
    return JellyfinResult.Success(Unit)
  }

  override suspend fun stopScheduledTask(taskId: String): JellyfinResult<Unit> {
    lastStoppedTaskId = taskId
    return JellyfinResult.Success(Unit)
  }

  override suspend fun getActivityLogEntries(
    startIndex: Int?,
    limit: Int?,
    minDate: String?,
    hasUserId: Boolean?,
  ): JellyfinResult<ActivityLogEntryQueryResult> {
    lastActivityStartIndex = startIndex
    lastActivityLimit = limit
    lastActivityMinDate = minDate
    lastActivityHasUserId = hasUserId
    return activityLogResult
  }
}
