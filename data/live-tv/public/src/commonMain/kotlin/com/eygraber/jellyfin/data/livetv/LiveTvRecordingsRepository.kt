package com.eygraber.jellyfin.data.livetv

import com.eygraber.jellyfin.common.JellyfinResult

/**
 * Repository for managing DVR recordings and the timers that schedule them.
 */
interface LiveTvRecordingsRepository {
  /**
   * Gets a page of recordings.
   *
   * @param startIndex Index of the first recording to return (for pagination).
   * @param limit Maximum number of recordings to return.
   * @param status Optional status filter.
   */
  suspend fun getRecordings(
    startIndex: Int = 0,
    limit: Int = DEFAULT_PAGE_SIZE,
    status: RecordingStatus? = null,
  ): JellyfinResult<LiveTvPaginatedResult<TvRecording>>

  /**
   * Gets a single recording by ID.
   */
  suspend fun getRecording(recordingId: String): JellyfinResult<TvRecording>

  /**
   * Deletes a recording from the server.
   */
  suspend fun deleteRecording(recordingId: String): JellyfinResult<Unit>

  /**
   * Gets all scheduled timers, optionally filtered to a single channel.
   */
  suspend fun getTimers(
    channelId: String? = null,
  ): JellyfinResult<List<RecordingTimer>>

  /**
   * Schedules a one-off recording timer for a program.
   *
   * The server populates timer metadata (channel, start/end, name) from the
   * supplied program.
   *
   * @param programId The program to record.
   * @param prePaddingSeconds Optional padding to record before the program starts.
   * @param postPaddingSeconds Optional padding to record after the program ends.
   */
  suspend fun createTimer(
    programId: String,
    prePaddingSeconds: Int? = null,
    postPaddingSeconds: Int? = null,
  ): JellyfinResult<Unit>

  /**
   * Cancels a scheduled timer.
   */
  suspend fun cancelTimer(timerId: String): JellyfinResult<Unit>

  /**
   * Gets all configured series timers.
   */
  suspend fun getSeriesTimers(): JellyfinResult<List<SeriesRecordingTimer>>

  /**
   * Schedules a series recording rule for a program's series.
   *
   * @param programId The program whose series should be recorded.
   * @param recordAnyChannel Whether to record across all channels that air the series.
   * @param recordAnyTime Whether to record any airing of an episode (vs. only the first).
   * @param recordNewOnly Whether to skip reruns and only record new episodes.
   */
  suspend fun createSeriesTimer(
    programId: String,
    recordAnyChannel: Boolean = false,
    recordAnyTime: Boolean = false,
    recordNewOnly: Boolean = false,
  ): JellyfinResult<Unit>

  /**
   * Cancels a series recording timer.
   */
  suspend fun cancelSeriesTimer(timerId: String): JellyfinResult<Unit>

  companion object {
    const val DEFAULT_PAGE_SIZE = 50
  }
}
