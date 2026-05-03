package com.eygraber.jellyfin.data.livetv

/**
 * A recording of a live TV program (either completed or in progress).
 */
data class TvRecording(
  val id: String,
  val name: String,
  val episodeTitle: String?,
  val overview: String?,
  val channelId: String?,
  val channelName: String?,
  val programId: String?,
  /**
   * ISO-8601 timestamp when the recording starts.
   */
  val startDate: String?,
  /**
   * ISO-8601 timestamp when the recording ends.
   */
  val endDate: String?,
  val runTimeTicks: Long?,
  val status: RecordingStatus,
  /**
   * Path to the recording on the server. May be null for in-progress recordings.
   */
  val path: String?,
  val primaryImageTag: String?,
)

/**
 * Lifecycle state of a recording.
 */
enum class RecordingStatus(val apiValue: String) {
  Completed(apiValue = "Completed"),
  InProgress(apiValue = "InProgress"),
  Cancelled(apiValue = "Cancelled"),
  Error(apiValue = "Error"),
  Unknown(apiValue = ""),
  ;

  companion object {
    fun fromApiValue(value: String?): RecordingStatus =
      entries.firstOrNull { it.apiValue.equals(value, ignoreCase = true) } ?: Unknown
  }
}
