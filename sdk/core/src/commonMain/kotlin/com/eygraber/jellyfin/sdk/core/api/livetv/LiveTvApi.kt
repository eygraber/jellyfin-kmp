package com.eygraber.jellyfin.sdk.core.api.livetv

import com.eygraber.jellyfin.sdk.core.SdkResult
import com.eygraber.jellyfin.sdk.core.api.BaseApi
import com.eygraber.jellyfin.sdk.core.api.JellyfinApiClient
import com.eygraber.jellyfin.sdk.core.model.BaseItemDto
import com.eygraber.jellyfin.sdk.core.model.LiveTvChannelResult
import com.eygraber.jellyfin.sdk.core.model.LiveTvGuideInfo
import com.eygraber.jellyfin.sdk.core.model.LiveTvProgramResult
import com.eygraber.jellyfin.sdk.core.model.LiveTvRecordingResult
import com.eygraber.jellyfin.sdk.core.model.LiveTvSeriesTimerInfoDto
import com.eygraber.jellyfin.sdk.core.model.LiveTvSeriesTimerInfoResult
import com.eygraber.jellyfin.sdk.core.model.LiveTvTimerInfoDto

class LiveTvApi(
  apiClient: JellyfinApiClient,
) : BaseApi(apiClient) {
  /**
   * Gets available live TV channels.
   *
   * @param userId Optional user ID for personalization.
   * @param limit Optional maximum number of results.
   * @param startIndex Optional index to start from for paging.
   * @param isFavorite Optional filter to only return favorite channels.
   * @param channelType Optional filter by channel type (e.g. "Tv", "Radio").
   * @param searchTerm Optional case-insensitive substring filter on the channel name.
   */
  @Suppress("LongParameterList")
  suspend fun getChannels(
    userId: String? = null,
    limit: Int? = null,
    startIndex: Int? = null,
    isFavorite: Boolean? = null,
    channelType: String? = null,
    searchTerm: String? = null,
  ): SdkResult<LiveTvChannelResult> = get(
    path = "LiveTv/Channels",
    queryParams = buildMap {
      userId?.let { put(key = "userId", value = it) }
      limit?.let { put(key = "limit", value = it) }
      startIndex?.let { put(key = "startIndex", value = it) }
      isFavorite?.let { put(key = "isFavorite", value = it) }
      channelType?.let { put(key = "channelType", value = it) }
      searchTerm?.let { put(key = "searchTerm", value = it) }
    },
  )

  /**
   * Gets a single live TV channel by ID.
   */
  suspend fun getChannel(
    channelId: String,
    userId: String? = null,
  ): SdkResult<BaseItemDto> = get(
    path = "LiveTv/Channels/$channelId",
    queryParams = buildMap {
      userId?.let { put(key = "userId", value = it) }
    },
  )

  /**
   * Gets available live TV programs (EPG/guide data).
   *
   * @param userId Optional user ID for personalization.
   * @param channelIds Optional list of channel IDs to filter by.
   * @param limit Optional maximum number of results.
   * @param isAiring Optional filter for currently airing programs.
   * @param hasAired Optional filter for programs that have already aired.
   * @param minStartDate ISO-8601 lower bound (inclusive) for program start time.
   * @param maxStartDate ISO-8601 upper bound (inclusive) for program start time.
   */
  @Suppress("LongParameterList")
  suspend fun getPrograms(
    userId: String? = null,
    channelIds: List<String>? = null,
    limit: Int? = null,
    isAiring: Boolean? = null,
    hasAired: Boolean? = null,
    minStartDate: String? = null,
    maxStartDate: String? = null,
  ): SdkResult<LiveTvProgramResult> = get(
    path = "LiveTv/Programs",
    queryParams = buildMap {
      userId?.let { put(key = "userId", value = it) }
      channelIds?.let { put(key = "channelIds", value = it.joinToString(",")) }
      limit?.let { put(key = "limit", value = it) }
      isAiring?.let { put(key = "isAiring", value = it) }
      hasAired?.let { put(key = "hasAired", value = it) }
      minStartDate?.let { put(key = "minStartDate", value = it) }
      maxStartDate?.let { put(key = "maxStartDate", value = it) }
    },
  )

  /**
   * Gets a single program by ID.
   */
  suspend fun getProgram(
    programId: String,
    userId: String? = null,
  ): SdkResult<BaseItemDto> = get(
    path = "LiveTv/Programs/$programId",
    queryParams = buildMap {
      userId?.let { put(key = "userId", value = it) }
    },
  )

  /**
   * Gets live TV recordings.
   *
   * @param userId Optional user ID for personalization.
   * @param limit Optional maximum number of results.
   * @param startIndex Optional index to start from for paging.
   * @param status Optional filter by recording status.
   */
  suspend fun getRecordings(
    userId: String? = null,
    limit: Int? = null,
    startIndex: Int? = null,
    status: String? = null,
  ): SdkResult<LiveTvRecordingResult> = get(
    path = "LiveTv/Recordings",
    queryParams = buildMap {
      userId?.let { put(key = "userId", value = it) }
      limit?.let { put(key = "limit", value = it) }
      startIndex?.let { put(key = "startIndex", value = it) }
      status?.let { put(key = "status", value = it) }
    },
  )

  /**
   * Gets a single recording by ID.
   */
  suspend fun getRecording(
    recordingId: String,
    userId: String? = null,
  ): SdkResult<BaseItemDto> = get(
    path = "LiveTv/Recordings/$recordingId",
    queryParams = buildMap {
      userId?.let { put(key = "userId", value = it) }
    },
  )

  /**
   * Deletes a recording.
   */
  suspend fun deleteRecording(
    recordingId: String,
  ): SdkResult<Unit> = delete(
    path = "LiveTv/Recordings/$recordingId",
  )

  /**
   * Gets live TV timers (scheduled recordings).
   *
   * @param channelId Optional filter by channel ID.
   */
  suspend fun getTimers(
    channelId: String? = null,
  ): SdkResult<List<LiveTvTimerInfoDto>> = get(
    path = "LiveTv/Timers",
    queryParams = buildMap {
      channelId?.let { put(key = "channelId", value = it) }
    },
  )

  /**
   * Creates a one-off recording timer for a program.
   *
   * The body should contain at minimum a `ProgramId` so the server can populate
   * the timer's metadata. Additional fields (padding, priority) may be supplied.
   */
  suspend fun createTimer(
    body: LiveTvTimerInfoDto,
  ): SdkResult<Unit> = post(
    path = "LiveTv/Timers",
    body = body,
  )

  /**
   * Cancels a scheduled timer.
   */
  suspend fun cancelTimer(
    timerId: String,
  ): SdkResult<Unit> = delete(
    path = "LiveTv/Timers/$timerId",
  )

  /**
   * Gets configured series timers.
   */
  suspend fun getSeriesTimers(): SdkResult<LiveTvSeriesTimerInfoResult> = get(
    path = "LiveTv/SeriesTimers",
  )

  /**
   * Gets a default series timer DTO populated for the given program. Callers
   * typically pass the result back to [createSeriesTimer] (optionally tweaking
   * fields like [LiveTvSeriesTimerInfoDto.recordAnyChannel]).
   */
  suspend fun getDefaultSeriesTimer(
    programId: String,
  ): SdkResult<LiveTvSeriesTimerInfoDto> = get(
    path = "LiveTv/Timers/Defaults",
    queryParams = mapOf("programId" to programId),
  )

  /**
   * Creates a series recording timer.
   *
   * For a program-driven series timer, fetch a default DTO via
   * [getDefaultSeriesTimer] first, mutate the returned object, and then submit
   * it here. The DTO must contain the channel/series info the server needs to
   * schedule recordings.
   */
  suspend fun createSeriesTimer(
    body: LiveTvSeriesTimerInfoDto,
  ): SdkResult<Unit> = post(
    path = "LiveTv/SeriesTimers",
    body = body,
  )

  /**
   * Cancels a series recording timer.
   */
  suspend fun cancelSeriesTimer(
    timerId: String,
  ): SdkResult<Unit> = delete(
    path = "LiveTv/SeriesTimers/$timerId",
  )

  /**
   * Gets EPG guide info such as the available date range.
   */
  suspend fun getGuideInfo(): SdkResult<LiveTvGuideInfo> = get(
    path = "LiveTv/GuideInfo",
  )
}
