package com.eygraber.jellyfin.services.sdk

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.sdk.core.model.BaseItemDto
import com.eygraber.jellyfin.sdk.core.model.LiveTvChannelResult
import com.eygraber.jellyfin.sdk.core.model.LiveTvGuideInfo
import com.eygraber.jellyfin.sdk.core.model.LiveTvProgramResult
import com.eygraber.jellyfin.sdk.core.model.LiveTvRecordingResult
import com.eygraber.jellyfin.sdk.core.model.LiveTvSeriesTimerInfoDto
import com.eygraber.jellyfin.sdk.core.model.LiveTvSeriesTimerInfoResult
import com.eygraber.jellyfin.sdk.core.model.LiveTvTimerInfoDto

/**
 * Service for accessing Jellyfin Live TV functionality.
 *
 * Provides operations to browse channels, query EPG (Electronic Program Guide)
 * data, manage recordings, and schedule timers. All operations require an
 * authenticated session with a valid user ID.
 */
interface JellyfinLiveTvService {
  /**
   * Gets available live TV channels.
   *
   * @param limit Maximum number of channels to return.
   * @param startIndex Pagination start index.
   * @param isFavorite Filter to only favorite channels when true.
   * @param channelType Filter by channel type (e.g. "Tv", "Radio").
   * @param searchTerm Case-insensitive substring filter on the channel name.
   */
  @Suppress("LongParameterList")
  suspend fun getChannels(
    limit: Int? = null,
    startIndex: Int? = null,
    isFavorite: Boolean? = null,
    channelType: String? = null,
    searchTerm: String? = null,
  ): JellyfinResult<LiveTvChannelResult>

  /**
   * Gets a single channel by ID.
   */
  suspend fun getChannel(channelId: String): JellyfinResult<BaseItemDto>

  /**
   * Gets program (EPG) entries.
   *
   * @param channelIds Optional channel filter.
   * @param limit Maximum number of programs to return.
   * @param isAiring Filter to currently airing programs when true.
   * @param hasAired Filter to programs that have already finished airing.
   * @param minStartDate ISO-8601 lower bound (inclusive) for program start time.
   * @param maxStartDate ISO-8601 upper bound (inclusive) for program start time.
   */
  @Suppress("LongParameterList")
  suspend fun getPrograms(
    channelIds: List<String>? = null,
    limit: Int? = null,
    isAiring: Boolean? = null,
    hasAired: Boolean? = null,
    minStartDate: String? = null,
    maxStartDate: String? = null,
  ): JellyfinResult<LiveTvProgramResult>

  /**
   * Gets a single program by ID.
   */
  suspend fun getProgram(programId: String): JellyfinResult<BaseItemDto>

  /**
   * Gets recordings.
   *
   * @param limit Maximum number of recordings to return.
   * @param startIndex Pagination start index.
   * @param status Optional status filter (e.g. "Completed", "InProgress").
   */
  suspend fun getRecordings(
    limit: Int? = null,
    startIndex: Int? = null,
    status: String? = null,
  ): JellyfinResult<LiveTvRecordingResult>

  /**
   * Gets a single recording by ID.
   */
  suspend fun getRecording(recordingId: String): JellyfinResult<BaseItemDto>

  /**
   * Deletes a recording.
   */
  suspend fun deleteRecording(recordingId: String): JellyfinResult<Unit>

  /**
   * Gets all scheduled timers.
   *
   * @param channelId Optional channel filter.
   */
  suspend fun getTimers(
    channelId: String? = null,
  ): JellyfinResult<List<LiveTvTimerInfoDto>>

  /**
   * Schedules a one-off recording timer.
   */
  suspend fun createTimer(timer: LiveTvTimerInfoDto): JellyfinResult<Unit>

  /**
   * Cancels a scheduled timer.
   */
  suspend fun cancelTimer(timerId: String): JellyfinResult<Unit>

  /**
   * Gets all configured series recording timers.
   */
  suspend fun getSeriesTimers(): JellyfinResult<LiveTvSeriesTimerInfoResult>

  /**
   * Gets a default series timer DTO populated for the given program.
   *
   * Callers typically pass the returned DTO (after optionally mutating the
   * rule fields) to [createSeriesTimer].
   */
  suspend fun getDefaultSeriesTimer(programId: String): JellyfinResult<LiveTvSeriesTimerInfoDto>

  /**
   * Creates a series recording timer.
   */
  suspend fun createSeriesTimer(timer: LiveTvSeriesTimerInfoDto): JellyfinResult<Unit>

  /**
   * Cancels a series recording timer.
   */
  suspend fun cancelSeriesTimer(timerId: String): JellyfinResult<Unit>

  /**
   * Gets EPG guide metadata such as the available date range.
   */
  suspend fun getGuideInfo(): JellyfinResult<LiveTvGuideInfo>
}
