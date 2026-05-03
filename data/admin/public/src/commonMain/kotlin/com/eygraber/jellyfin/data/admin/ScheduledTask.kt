package com.eygraber.jellyfin.data.admin

/**
 * A scheduled background task on the server (library scan, metadata refresh, etc.).
 */
data class ScheduledTask(
  val id: String,
  val name: String,
  val description: String?,
  val category: String?,
  val key: String?,
  val state: ScheduledTaskState,
  /**
   * Progress in `0.0..100.0`, only meaningful when [state] is [ScheduledTaskState.Running].
   */
  val currentProgressPercent: Double?,
  val isHidden: Boolean,
  val lastExecution: ScheduledTaskExecution?,
)

/**
 * Lifecycle state of a [ScheduledTask].
 *
 * Maps from the server's `State` field. [Unknown] covers any value the client
 * doesn't recognize - new server versions may introduce new states.
 */
enum class ScheduledTaskState(val apiValue: String) {
  Idle("Idle"),
  Cancelling("Cancelling"),
  Running("Running"),
  Unknown(""),
  ;

  companion object {
    /**
     * Parses a server state value, returning [Unknown] for unrecognized inputs.
     */
    fun fromApiValue(value: String?): ScheduledTaskState =
      entries.firstOrNull { it.apiValue.equals(value, ignoreCase = true) } ?: Unknown
  }
}

/**
 * Outcome of the most recent run of a [ScheduledTask].
 */
data class ScheduledTaskExecution(
  val startTimeUtc: String?,
  val endTimeUtc: String?,
  val status: ScheduledTaskExecutionStatus,
  val errorMessage: String?,
)

enum class ScheduledTaskExecutionStatus(val apiValue: String) {
  Completed("Completed"),
  Failed("Failed"),
  Cancelled("Cancelled"),
  Aborted("Aborted"),
  Unknown(""),
  ;

  companion object {
    fun fromApiValue(value: String?): ScheduledTaskExecutionStatus =
      entries.firstOrNull { it.apiValue.equals(value, ignoreCase = true) } ?: Unknown
  }
}
