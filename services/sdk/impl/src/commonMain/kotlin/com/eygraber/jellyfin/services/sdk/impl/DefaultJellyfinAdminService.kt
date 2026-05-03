package com.eygraber.jellyfin.services.sdk.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.sdk.core.JellyfinSdk
import com.eygraber.jellyfin.sdk.core.api.JellyfinApiClient
import com.eygraber.jellyfin.sdk.core.api.activity.activityLogApi
import com.eygraber.jellyfin.sdk.core.api.library.libraryApi
import com.eygraber.jellyfin.sdk.core.api.system.systemApi
import com.eygraber.jellyfin.sdk.core.api.tasks.scheduledTasksApi
import com.eygraber.jellyfin.sdk.core.api.user.userApi
import com.eygraber.jellyfin.sdk.core.model.ActivityLogEntryQueryResult
import com.eygraber.jellyfin.sdk.core.model.ScheduledTaskInfo
import com.eygraber.jellyfin.sdk.core.model.ServerConfigurationDto
import com.eygraber.jellyfin.sdk.core.model.SystemInfo
import com.eygraber.jellyfin.sdk.core.model.UserDto
import com.eygraber.jellyfin.sdk.core.model.UserPolicy
import com.eygraber.jellyfin.sdk.core.model.VirtualFolderInfo
import com.eygraber.jellyfin.services.logging.JellyfinLogger
import com.eygraber.jellyfin.services.sdk.JellyfinAdminService
import com.eygraber.jellyfin.services.sdk.JellyfinSessionManager
import com.eygraber.jellyfin.services.sdk.toJellyfinResult
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

/**
 * Default implementation of [JellyfinAdminService].
 *
 * Wraps the underlying SDK admin APIs and converts each [com.eygraber.jellyfin.sdk.core.SdkResult]
 * into a [JellyfinResult]. Every operation requires a connected server; the
 * service does not separately enforce admin privileges - the server returns
 * 403 for unauthorized requests, which is mapped via [JellyfinResult.Error.Detailed].
 */
@Suppress("TooManyFunctions")
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
internal class DefaultJellyfinAdminService(
  private val sdk: JellyfinSdk,
  private val sessionManager: JellyfinSessionManager,
  private val logger: JellyfinLogger,
) : JellyfinAdminService {
  override suspend fun getUsers(
    isHidden: Boolean?,
    isDisabled: Boolean?,
  ): JellyfinResult<List<UserDto>> = withApiClient(operation = "Fetching users") { client ->
    client.userApi.getUsers(isHidden = isHidden, isDisabled = isDisabled).toJellyfinResult()
  }

  override suspend fun getUser(userId: String): JellyfinResult<UserDto> =
    withApiClient(operation = "Fetching user $userId") { client ->
      client.userApi.getUserById(userId = userId).toJellyfinResult()
    }

  override suspend fun createUser(name: String, password: String?): JellyfinResult<UserDto> =
    withApiClient(operation = "Creating user $name") { client ->
      client.userApi.createUser(name = name, password = password).toJellyfinResult()
    }

  override suspend fun updateUser(userId: String, user: UserDto): JellyfinResult<Unit> =
    withApiClient(operation = "Updating user $userId") { client ->
      client.userApi.updateUser(userId = userId, user = user).toJellyfinResult()
    }

  override suspend fun updateUserPolicy(
    userId: String,
    policy: UserPolicy,
  ): JellyfinResult<Unit> =
    withApiClient(operation = "Updating policy for user $userId") { client ->
      client.userApi.updateUserPolicy(userId = userId, policy = policy).toJellyfinResult()
    }

  override suspend fun updateUserPassword(
    userId: String,
    newPassword: String,
    currentPassword: String?,
    resetPassword: Boolean,
  ): JellyfinResult<Unit> =
    withApiClient(operation = "Updating password for user $userId") { client ->
      client.userApi.updateUserPassword(
        userId = userId,
        newPassword = newPassword,
        currentPassword = currentPassword,
        resetPassword = resetPassword,
      ).toJellyfinResult()
    }

  override suspend fun deleteUser(userId: String): JellyfinResult<Unit> =
    withApiClient(operation = "Deleting user $userId") { client ->
      client.userApi.deleteUser(userId = userId).toJellyfinResult()
    }

  override suspend fun getVirtualFolders(): JellyfinResult<List<VirtualFolderInfo>> =
    withApiClient(operation = "Fetching virtual folders") { client ->
      client.libraryApi.getVirtualFolders().toJellyfinResult()
    }

  override suspend fun refreshLibrary(): JellyfinResult<Unit> =
    withApiClient(operation = "Refreshing library") { client ->
      client.libraryApi.refreshLibrary().toJellyfinResult()
    }

  override suspend fun getSystemInfo(): JellyfinResult<SystemInfo> =
    withApiClient(operation = "Fetching system info") { client ->
      client.systemApi.getSystemInfo().toJellyfinResult()
    }

  override suspend fun getConfiguration(): JellyfinResult<ServerConfigurationDto> =
    withApiClient(operation = "Fetching server configuration") { client ->
      client.systemApi.getConfiguration().toJellyfinResult()
    }

  override suspend fun updateConfiguration(
    configuration: ServerConfigurationDto,
  ): JellyfinResult<Unit> =
    withApiClient(operation = "Updating server configuration") { client ->
      client.systemApi.updateConfiguration(configuration = configuration).toJellyfinResult()
    }

  override suspend fun restartServer(): JellyfinResult<Unit> =
    withApiClient(operation = "Restarting server") { client ->
      client.systemApi.restartServer().toJellyfinResult()
    }

  override suspend fun shutdownServer(): JellyfinResult<Unit> =
    withApiClient(operation = "Shutting down server") { client ->
      client.systemApi.shutdownServer().toJellyfinResult()
    }

  override suspend fun getScheduledTasks(
    isHidden: Boolean?,
    isEnabled: Boolean?,
  ): JellyfinResult<List<ScheduledTaskInfo>> =
    withApiClient(operation = "Fetching scheduled tasks") { client ->
      client.scheduledTasksApi.getTasks(
        isHidden = isHidden,
        isEnabled = isEnabled,
      ).toJellyfinResult()
    }

  override suspend fun getScheduledTask(taskId: String): JellyfinResult<ScheduledTaskInfo> =
    withApiClient(operation = "Fetching scheduled task $taskId") { client ->
      client.scheduledTasksApi.getTask(taskId = taskId).toJellyfinResult()
    }

  override suspend fun startScheduledTask(taskId: String): JellyfinResult<Unit> =
    withApiClient(operation = "Starting scheduled task $taskId") { client ->
      client.scheduledTasksApi.startTask(taskId = taskId).toJellyfinResult()
    }

  override suspend fun stopScheduledTask(taskId: String): JellyfinResult<Unit> =
    withApiClient(operation = "Stopping scheduled task $taskId") { client ->
      client.scheduledTasksApi.stopTask(taskId = taskId).toJellyfinResult()
    }

  override suspend fun getActivityLogEntries(
    startIndex: Int?,
    limit: Int?,
    minDate: String?,
    hasUserId: Boolean?,
  ): JellyfinResult<ActivityLogEntryQueryResult> =
    withApiClient(operation = "Fetching activity log") { client ->
      client.activityLogApi.getEntries(
        startIndex = startIndex,
        limit = limit,
        minDate = minDate,
        hasUserId = hasUserId,
      ).toJellyfinResult()
    }

  private suspend inline fun <T> withApiClient(
    operation: String,
    block: suspend (JellyfinApiClient) -> JellyfinResult<T>,
  ): JellyfinResult<T> {
    val serverInfo = sessionManager.currentServer.value
      ?: return JellyfinResult.Error(
        message = "Not connected to a server",
        isEphemeral = false,
      )

    logger.debug(tag = TAG, message = operation)

    val apiClient = sdk.createApiClient(serverInfo = serverInfo)
    return try {
      block(apiClient)
    }
    finally {
      apiClient.close()
    }
  }

  companion object {
    private const val TAG = "JellyfinAdminService"
  }
}
