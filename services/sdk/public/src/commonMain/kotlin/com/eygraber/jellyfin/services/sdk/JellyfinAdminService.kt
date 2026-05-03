package com.eygraber.jellyfin.services.sdk

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.sdk.core.model.ActivityLogEntryQueryResult
import com.eygraber.jellyfin.sdk.core.model.ScheduledTaskInfo
import com.eygraber.jellyfin.sdk.core.model.ServerConfigurationDto
import com.eygraber.jellyfin.sdk.core.model.SystemInfo
import com.eygraber.jellyfin.sdk.core.model.UserDto
import com.eygraber.jellyfin.sdk.core.model.UserPolicy
import com.eygraber.jellyfin.sdk.core.model.VirtualFolderInfo

/**
 * Server administration operations.
 *
 * All operations require an authenticated session whose user has administrator
 * privileges. The service does **not** validate the privilege client-side -
 * the server is the source of truth and will reject unauthorized calls with a
 * 403.
 */
@Suppress("TooManyFunctions")
interface JellyfinAdminService {
  // -- Users --

  /**
   * Lists all users on the server.
   */
  suspend fun getUsers(
    isHidden: Boolean? = null,
    isDisabled: Boolean? = null,
  ): JellyfinResult<List<UserDto>>

  /**
   * Gets a single user by ID.
   */
  suspend fun getUser(userId: String): JellyfinResult<UserDto>

  /**
   * Creates a new user with the given name and optional initial password.
   */
  suspend fun createUser(
    name: String,
    password: String? = null,
  ): JellyfinResult<UserDto>

  /**
   * Updates a user's profile fields.
   */
  suspend fun updateUser(
    userId: String,
    user: UserDto,
  ): JellyfinResult<Unit>

  /**
   * Updates a user's permissions/policy.
   */
  suspend fun updateUserPolicy(
    userId: String,
    policy: UserPolicy,
  ): JellyfinResult<Unit>

  /**
   * Updates a user's password.
   *
   * Pass [resetPassword] = true to clear the password instead of setting a new one.
   */
  suspend fun updateUserPassword(
    userId: String,
    newPassword: String,
    currentPassword: String? = null,
    resetPassword: Boolean = false,
  ): JellyfinResult<Unit>

  /**
   * Deletes a user.
   */
  suspend fun deleteUser(userId: String): JellyfinResult<Unit>

  // -- Libraries --

  /**
   * Lists the virtual folders ("libraries") configured on the server.
   */
  suspend fun getVirtualFolders(): JellyfinResult<List<VirtualFolderInfo>>

  /**
   * Triggers a server-wide library scan.
   */
  suspend fun refreshLibrary(): JellyfinResult<Unit>

  // -- Server --

  /**
   * Gets detailed server information.
   */
  suspend fun getSystemInfo(): JellyfinResult<SystemInfo>

  /**
   * Gets the server configuration.
   */
  suspend fun getConfiguration(): JellyfinResult<ServerConfigurationDto>

  /**
   * Updates the server configuration.
   */
  suspend fun updateConfiguration(
    configuration: ServerConfigurationDto,
  ): JellyfinResult<Unit>

  /**
   * Restarts the server.
   */
  suspend fun restartServer(): JellyfinResult<Unit>

  /**
   * Shuts the server down.
   */
  suspend fun shutdownServer(): JellyfinResult<Unit>

  // -- Scheduled tasks --

  /**
   * Lists scheduled tasks on the server.
   */
  suspend fun getScheduledTasks(
    isHidden: Boolean? = null,
    isEnabled: Boolean? = null,
  ): JellyfinResult<List<ScheduledTaskInfo>>

  /**
   * Gets a single scheduled task by ID.
   */
  suspend fun getScheduledTask(taskId: String): JellyfinResult<ScheduledTaskInfo>

  /**
   * Triggers a scheduled task to run immediately.
   */
  suspend fun startScheduledTask(taskId: String): JellyfinResult<Unit>

  /**
   * Cancels a currently running scheduled task.
   */
  suspend fun stopScheduledTask(taskId: String): JellyfinResult<Unit>

  // -- Activity log --

  /**
   * Lists activity log entries.
   *
   * @param startIndex Pagination start index.
   * @param limit Maximum number of entries to return.
   * @param minDate ISO-8601 lower bound (inclusive) for entry date.
   * @param hasUserId If true, only include entries with a user; if false, only without.
   */
  suspend fun getActivityLogEntries(
    startIndex: Int? = null,
    limit: Int? = null,
    minDate: String? = null,
    hasUserId: Boolean? = null,
  ): JellyfinResult<ActivityLogEntryQueryResult>
}
