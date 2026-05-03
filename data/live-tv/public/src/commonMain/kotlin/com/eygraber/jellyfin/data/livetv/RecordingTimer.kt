@file:Suppress("BooleanPropertyNaming")

package com.eygraber.jellyfin.data.livetv

/**
 * A scheduled one-off recording timer.
 *
 * Created via [LiveTvRecordingsRepository.createTimer] and surfaced from
 * [LiveTvRecordingsRepository.getTimers].
 */
data class RecordingTimer(
  val id: String,
  val channelId: String?,
  val channelName: String?,
  val programId: String?,
  val name: String,
  val overview: String?,
  /**
   * ISO-8601 timestamp when the recording will start.
   */
  val startDate: String?,
  /**
   * ISO-8601 timestamp when the recording will end.
   */
  val endDate: String?,
  val status: RecordingStatus,
  val isPrePaddingRequired: Boolean,
  val isPostPaddingRequired: Boolean,
  val prePaddingSeconds: Int,
  val postPaddingSeconds: Int,
)

/**
 * A scheduled series recording rule.
 *
 * Series timers automatically schedule individual recordings as new episodes
 * appear in the EPG.
 */
data class SeriesRecordingTimer(
  val id: String,
  val channelId: String?,
  val channelName: String?,
  val name: String,
  val overview: String?,
  /**
   * Whether to record the series across any channel that airs it.
   */
  val recordAnyChannel: Boolean,
  /**
   * Whether to record any airing of an episode (vs. only the first).
   */
  val recordAnyTime: Boolean,
  /**
   * Whether to record only new (premiere) airings, skipping reruns.
   */
  val recordNewOnly: Boolean,
  /**
   * Days of the week the timer is active (e.g. "Monday", "Tuesday"). Empty
   * means every day.
   */
  val days: List<String>,
  /**
   * Maximum number of episodes to retain. 0 means keep all.
   */
  val keepUpTo: Int,
  /**
   * Retention policy (e.g. "UntilDeleted", "UntilSpaceNeeded"). Server-defined string.
   */
  val keepUntil: String?,
  val startDate: String?,
  val endDate: String?,
)
