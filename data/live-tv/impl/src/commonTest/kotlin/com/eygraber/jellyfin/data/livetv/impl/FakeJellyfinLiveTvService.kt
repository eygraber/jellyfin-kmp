@file:Suppress("BooleanPropertyNaming")

package com.eygraber.jellyfin.data.livetv.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.sdk.core.model.BaseItemDto
import com.eygraber.jellyfin.sdk.core.model.LiveTvChannelResult
import com.eygraber.jellyfin.sdk.core.model.LiveTvGuideInfo
import com.eygraber.jellyfin.sdk.core.model.LiveTvProgramResult
import com.eygraber.jellyfin.sdk.core.model.LiveTvRecordingResult
import com.eygraber.jellyfin.sdk.core.model.LiveTvSeriesTimerInfoDto
import com.eygraber.jellyfin.sdk.core.model.LiveTvSeriesTimerInfoResult
import com.eygraber.jellyfin.sdk.core.model.LiveTvTimerInfoDto
import com.eygraber.jellyfin.services.sdk.JellyfinLiveTvService

@Suppress("TooManyFunctions")
internal class FakeJellyfinLiveTvService : JellyfinLiveTvService {
  var channelsResult: JellyfinResult<LiveTvChannelResult> = JellyfinResult.Success(
    LiveTvChannelResult(items = emptyList(), totalRecordCount = 0),
  )
  var singleChannelResult: JellyfinResult<BaseItemDto> = JellyfinResult.Success(BaseItemDto(id = "default"))
  var programsResult: JellyfinResult<LiveTvProgramResult> = JellyfinResult.Success(
    LiveTvProgramResult(items = emptyList(), totalRecordCount = 0),
  )
  var singleProgramResult: JellyfinResult<BaseItemDto> = JellyfinResult.Success(BaseItemDto(id = "default"))
  var recordingsResult: JellyfinResult<LiveTvRecordingResult> = JellyfinResult.Success(
    LiveTvRecordingResult(items = emptyList(), totalRecordCount = 0),
  )
  var singleRecordingResult: JellyfinResult<BaseItemDto> = JellyfinResult.Success(BaseItemDto(id = "default"))
  var timersResult: JellyfinResult<List<LiveTvTimerInfoDto>> = JellyfinResult.Success(emptyList())
  var seriesTimersResult: JellyfinResult<LiveTvSeriesTimerInfoResult> = JellyfinResult.Success(
    LiveTvSeriesTimerInfoResult(items = emptyList(), totalRecordCount = 0),
  )
  var defaultSeriesTimerResult: JellyfinResult<LiveTvSeriesTimerInfoDto> = JellyfinResult.Success(
    LiveTvSeriesTimerInfoDto(),
  )
  var guideInfoResult: JellyfinResult<LiveTvGuideInfo> = JellyfinResult.Success(LiveTvGuideInfo())

  var lastChannelsSearchTerm: String? = null
  var lastProgramsChannelIds: List<String>? = null
  var lastProgramsIsAiring: Boolean? = null
  var lastProgramsHasAired: Boolean? = null
  var lastProgramsLimit: Int? = null
  var lastProgramsMinStartDate: String? = null
  var lastProgramsMaxStartDate: String? = null
  var lastRecordingsStatus: String? = null
  var lastDeletedRecordingId: String? = null
  var lastTimersChannelId: String? = null
  var lastCreatedTimer: LiveTvTimerInfoDto? = null
  var lastCancelledTimerId: String? = null
  var lastCreatedSeriesTimer: LiveTvSeriesTimerInfoDto? = null
  var lastCancelledSeriesTimerId: String? = null
  var lastDefaultSeriesTimerProgramId: String? = null

  override suspend fun getChannels(
    limit: Int?,
    startIndex: Int?,
    isFavorite: Boolean?,
    channelType: String?,
    searchTerm: String?,
  ): JellyfinResult<LiveTvChannelResult> {
    lastChannelsSearchTerm = searchTerm
    return channelsResult
  }

  override suspend fun getChannel(channelId: String): JellyfinResult<BaseItemDto> = singleChannelResult

  override suspend fun getPrograms(
    channelIds: List<String>?,
    limit: Int?,
    isAiring: Boolean?,
    hasAired: Boolean?,
    minStartDate: String?,
    maxStartDate: String?,
  ): JellyfinResult<LiveTvProgramResult> {
    lastProgramsChannelIds = channelIds
    lastProgramsIsAiring = isAiring
    lastProgramsHasAired = hasAired
    lastProgramsLimit = limit
    lastProgramsMinStartDate = minStartDate
    lastProgramsMaxStartDate = maxStartDate
    return programsResult
  }

  override suspend fun getProgram(programId: String): JellyfinResult<BaseItemDto> = singleProgramResult

  override suspend fun getRecordings(
    limit: Int?,
    startIndex: Int?,
    status: String?,
  ): JellyfinResult<LiveTvRecordingResult> {
    lastRecordingsStatus = status
    return recordingsResult
  }

  override suspend fun getRecording(recordingId: String): JellyfinResult<BaseItemDto> = singleRecordingResult

  override suspend fun deleteRecording(recordingId: String): JellyfinResult<Unit> {
    lastDeletedRecordingId = recordingId
    return JellyfinResult.Success(Unit)
  }

  override suspend fun getTimers(channelId: String?): JellyfinResult<List<LiveTvTimerInfoDto>> {
    lastTimersChannelId = channelId
    return timersResult
  }

  override suspend fun createTimer(timer: LiveTvTimerInfoDto): JellyfinResult<Unit> {
    lastCreatedTimer = timer
    return JellyfinResult.Success(Unit)
  }

  override suspend fun cancelTimer(timerId: String): JellyfinResult<Unit> {
    lastCancelledTimerId = timerId
    return JellyfinResult.Success(Unit)
  }

  override suspend fun getSeriesTimers(): JellyfinResult<LiveTvSeriesTimerInfoResult> = seriesTimersResult

  override suspend fun getDefaultSeriesTimer(programId: String): JellyfinResult<LiveTvSeriesTimerInfoDto> {
    lastDefaultSeriesTimerProgramId = programId
    return defaultSeriesTimerResult
  }

  override suspend fun createSeriesTimer(timer: LiveTvSeriesTimerInfoDto): JellyfinResult<Unit> {
    lastCreatedSeriesTimer = timer
    return JellyfinResult.Success(Unit)
  }

  override suspend fun cancelSeriesTimer(timerId: String): JellyfinResult<Unit> {
    lastCancelledSeriesTimerId = timerId
    return JellyfinResult.Success(Unit)
  }

  override suspend fun getGuideInfo(): JellyfinResult<LiveTvGuideInfo> = guideInfoResult
}
