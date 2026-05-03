package com.eygraber.jellyfin.sdk.core.api.tasks

import com.eygraber.jellyfin.sdk.core.SdkResult
import com.eygraber.jellyfin.sdk.core.api.BaseApi
import com.eygraber.jellyfin.sdk.core.api.JellyfinApiClient
import com.eygraber.jellyfin.sdk.core.model.ScheduledTaskInfo

/**
 * Server scheduled tasks (library scans, metadata refresh, etc.).
 *
 * All operations require administrator privileges.
 */
class ScheduledTasksApi(
  apiClient: JellyfinApiClient,
) : BaseApi(apiClient) {
  /**
   * Lists all scheduled tasks on the server.
   *
   * @param isHidden Filter to hidden / non-hidden tasks when set.
   * @param isEnabled Filter to enabled / disabled tasks when set.
   */
  suspend fun getTasks(
    isHidden: Boolean? = null,
    isEnabled: Boolean? = null,
  ): SdkResult<List<ScheduledTaskInfo>> = get(
    path = "ScheduledTasks",
    queryParams = mapOf(
      "isHidden" to isHidden,
      "isEnabled" to isEnabled,
    ),
  )

  /**
   * Gets a single scheduled task by ID.
   */
  suspend fun getTask(taskId: String): SdkResult<ScheduledTaskInfo> =
    get(path = "ScheduledTasks/$taskId")

  /**
   * Triggers a scheduled task to run immediately.
   */
  suspend fun startTask(taskId: String): SdkResult<Unit> =
    post<Unit, Unit>(path = "ScheduledTasks/Running/$taskId")

  /**
   * Cancels a currently running task.
   */
  suspend fun stopTask(taskId: String): SdkResult<Unit> =
    delete(path = "ScheduledTasks/Running/$taskId")
}
