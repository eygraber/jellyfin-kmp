package com.eygraber.jellyfin.data.admin

import com.eygraber.jellyfin.common.JellyfinResult

/**
 * Repository for administrative server operations.
 *
 * Requires an authenticated administrator session.
 */
interface ServerAdminRepository {
  /**
   * Gets detailed server information.
   */
  suspend fun getServerInfo(): JellyfinResult<AdminServerInfo>

  /**
   * Gets the curated subset of server configuration that admin UIs edit.
   *
   * Note: only fields exposed via [AdminServerConfiguration] are returned;
   * other fields on the underlying configuration DTO are preserved server-side.
   */
  suspend fun getConfiguration(): JellyfinResult<AdminServerConfiguration>

  /**
   * Updates the editable server configuration fields.
   *
   * The implementation reads the current full configuration, applies the
   * fields exposed by [AdminServerConfiguration], and posts the merged result -
   * so unknown fields are preserved.
   */
  suspend fun updateConfiguration(
    configuration: AdminServerConfiguration,
  ): JellyfinResult<Unit>

  /**
   * Restarts the server.
   */
  suspend fun restartServer(): JellyfinResult<Unit>

  /**
   * Shuts the server down.
   */
  suspend fun shutdownServer(): JellyfinResult<Unit>

  /**
   * Lists scheduled tasks on the server.
   *
   * @param isHidden Filter to hidden / non-hidden tasks when set.
   * @param isEnabled Filter to enabled / disabled tasks when set.
   */
  suspend fun getScheduledTasks(
    isHidden: Boolean? = null,
    isEnabled: Boolean? = null,
  ): JellyfinResult<List<ScheduledTask>>

  /**
   * Gets a single scheduled task by ID.
   */
  suspend fun getScheduledTask(taskId: String): JellyfinResult<ScheduledTask>

  /**
   * Triggers a scheduled task to run immediately.
   */
  suspend fun startScheduledTask(taskId: String): JellyfinResult<Unit>

  /**
   * Cancels a currently running scheduled task.
   */
  suspend fun stopScheduledTask(taskId: String): JellyfinResult<Unit>
}
