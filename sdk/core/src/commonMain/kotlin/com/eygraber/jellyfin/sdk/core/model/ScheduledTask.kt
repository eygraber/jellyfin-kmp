@file:Suppress("BooleanPropertyNaming")

package com.eygraber.jellyfin.sdk.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A scheduled background task on the Jellyfin server.
 *
 * Returned by `GET /ScheduledTasks`. Combines static metadata (`name`,
 * `description`, `category`) with the latest run info (`state`, `lastExecutionResult`).
 */
@Serializable
data class ScheduledTaskInfo(
  @SerialName("Id") val id: String? = null,
  @SerialName("Name") val name: String? = null,
  @SerialName("Description") val description: String? = null,
  @SerialName("Category") val category: String? = null,
  @SerialName("Key") val key: String? = null,
  @SerialName("State") val state: String? = null,
  @SerialName("CurrentProgressPercentage") val currentProgressPercentage: Double? = null,
  @SerialName("IsHidden") val isHidden: Boolean = false,
  @SerialName("LastExecutionResult") val lastExecutionResult: TaskResult? = null,
)

/**
 * Result of the last run of a [ScheduledTaskInfo].
 */
@Serializable
data class TaskResult(
  @SerialName("StartTimeUtc") val startTimeUtc: String? = null,
  @SerialName("EndTimeUtc") val endTimeUtc: String? = null,
  @SerialName("Status") val status: String? = null,
  @SerialName("Name") val name: String? = null,
  @SerialName("Key") val key: String? = null,
  @SerialName("Id") val id: String? = null,
  @SerialName("ErrorMessage") val errorMessage: String? = null,
  @SerialName("LongErrorMessage") val longErrorMessage: String? = null,
)
