package com.eygraber.jellyfin.sdk.core.api.livetv

import com.eygraber.jellyfin.sdk.core.SdkResult
import com.eygraber.jellyfin.sdk.core.api.BaseApi
import com.eygraber.jellyfin.sdk.core.api.JellyfinApiClient
import com.eygraber.jellyfin.sdk.core.model.LiveTvChannelResult
import com.eygraber.jellyfin.sdk.core.model.LiveTvProgramResult
import com.eygraber.jellyfin.sdk.core.model.LiveTvRecordingResult
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
   */
  suspend fun getChannels(
    userId: String? = null,
    limit: Int? = null,
    startIndex: Int? = null,
    isFavorite: Boolean? = null,
  ): SdkResult<LiveTvChannelResult> = get(
    path = "LiveTv/Channels",
    queryParams = buildMap {
      userId?.let { put(key = "userId", value = it) }
      limit?.let { put(key = "limit", value = it) }
      startIndex?.let { put(key = "startIndex", value = it) }
      isFavorite?.let { put(key = "isFavorite", value = it) }
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
   */
  @Suppress("LongParameterList")
  suspend fun getPrograms(
    userId: String? = null,
    channelIds: List<String>? = null,
    limit: Int? = null,
    isAiring: Boolean? = null,
    hasAired: Boolean? = null,
  ): SdkResult<LiveTvProgramResult> = get(
    path = "LiveTv/Programs",
    queryParams = buildMap {
      userId?.let { put(key = "userId", value = it) }
      channelIds?.let { put(key = "channelIds", value = it.joinToString(",")) }
      limit?.let { put(key = "limit", value = it) }
      isAiring?.let { put(key = "isAiring", value = it) }
      hasAired?.let { put(key = "hasAired", value = it) }
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
}
