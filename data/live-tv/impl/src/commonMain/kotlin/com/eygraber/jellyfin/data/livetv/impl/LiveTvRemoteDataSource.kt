package com.eygraber.jellyfin.data.livetv.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.common.flatMapSuccessTo
import com.eygraber.jellyfin.common.mapSuccessTo
import com.eygraber.jellyfin.data.livetv.GuideInfo
import com.eygraber.jellyfin.data.livetv.LiveTvPaginatedResult
import com.eygraber.jellyfin.data.livetv.RecordingStatus
import com.eygraber.jellyfin.data.livetv.RecordingTimer
import com.eygraber.jellyfin.data.livetv.SeriesRecordingTimer
import com.eygraber.jellyfin.data.livetv.TvChannel
import com.eygraber.jellyfin.data.livetv.TvProgram
import com.eygraber.jellyfin.data.livetv.TvRecording
import com.eygraber.jellyfin.sdk.core.model.BaseItemDto
import com.eygraber.jellyfin.sdk.core.model.LiveTvSeriesTimerInfoDto
import com.eygraber.jellyfin.sdk.core.model.LiveTvTimerInfoDto
import com.eygraber.jellyfin.services.sdk.JellyfinLiveTvService
import dev.zacsweers.metro.Inject

/**
 * Remote data source for Live TV operations.
 *
 * Wraps [JellyfinLiveTvService] and maps SDK DTOs into the Live TV data
 * layer's domain entities.
 */
@Inject
class LiveTvRemoteDataSource(
  private val liveTvService: JellyfinLiveTvService,
) {
  suspend fun getChannels(
    startIndex: Int,
    limit: Int,
    channelType: String?,
    isFavorite: Boolean?,
  ): JellyfinResult<LiveTvPaginatedResult<TvChannel>> =
    liveTvService.getChannels(
      startIndex = startIndex,
      limit = limit,
      isFavorite = isFavorite,
      channelType = channelType,
    ).mapSuccessTo {
      LiveTvPaginatedResult(
        items = items.mapNotNull { it.toTvChannel() },
        totalRecordCount = totalRecordCount,
        startIndex = startIndex,
      )
    }

  suspend fun searchChannels(
    searchTerm: String,
    limit: Int,
  ): JellyfinResult<LiveTvPaginatedResult<TvChannel>> =
    liveTvService.getChannels(
      searchTerm = searchTerm,
      limit = limit,
    ).mapSuccessTo {
      LiveTvPaginatedResult(
        items = items.mapNotNull { it.toTvChannel() },
        totalRecordCount = totalRecordCount,
        startIndex = 0,
      )
    }

  suspend fun getChannel(channelId: String): JellyfinResult<TvChannel> =
    liveTvService.getChannel(channelId = channelId).mapSuccessTo {
      toTvChannel() ?: error("Channel $channelId has no ID")
    }

  suspend fun getPrograms(
    channelIds: List<String>?,
    minStartDate: String?,
    maxStartDate: String?,
    limit: Int?,
  ): JellyfinResult<List<TvProgram>> =
    liveTvService.getPrograms(
      channelIds = channelIds,
      limit = limit,
      minStartDate = minStartDate,
      maxStartDate = maxStartDate,
    ).mapSuccessTo {
      items.mapNotNull { it.toTvProgram() }
    }

  suspend fun getCurrentPrograms(
    channelIds: List<String>?,
  ): JellyfinResult<List<TvProgram>> =
    liveTvService.getPrograms(
      channelIds = channelIds,
      isAiring = true,
    ).mapSuccessTo {
      items.mapNotNull { it.toTvProgram() }
    }

  suspend fun getUpcomingPrograms(
    channelIds: List<String>?,
    limit: Int?,
  ): JellyfinResult<List<TvProgram>> =
    liveTvService.getPrograms(
      channelIds = channelIds,
      limit = limit,
      hasAired = false,
    ).mapSuccessTo {
      items.mapNotNull { it.toTvProgram() }
    }

  suspend fun getProgram(programId: String): JellyfinResult<TvProgram> =
    liveTvService.getProgram(programId = programId).mapSuccessTo {
      toTvProgram() ?: error("Program $programId has no ID")
    }

  suspend fun getRecordings(
    startIndex: Int,
    limit: Int,
    status: RecordingStatus?,
  ): JellyfinResult<LiveTvPaginatedResult<TvRecording>> =
    liveTvService.getRecordings(
      startIndex = startIndex,
      limit = limit,
      status = status?.takeIf { it != RecordingStatus.Unknown }?.apiValue,
    ).mapSuccessTo {
      LiveTvPaginatedResult(
        items = items.mapNotNull { it.toTvRecording() },
        totalRecordCount = totalRecordCount,
        startIndex = startIndex,
      )
    }

  suspend fun getRecording(recordingId: String): JellyfinResult<TvRecording> =
    liveTvService.getRecording(recordingId = recordingId).mapSuccessTo {
      toTvRecording() ?: error("Recording $recordingId has no ID")
    }

  suspend fun deleteRecording(recordingId: String): JellyfinResult<Unit> =
    liveTvService.deleteRecording(recordingId = recordingId)

  suspend fun getTimers(channelId: String?): JellyfinResult<List<RecordingTimer>> =
    liveTvService.getTimers(channelId = channelId).mapSuccessTo {
      mapNotNull { it.toRecordingTimer() }
    }

  suspend fun createTimer(
    programId: String,
    prePaddingSeconds: Int?,
    postPaddingSeconds: Int?,
  ): JellyfinResult<Unit> =
    liveTvService.createTimer(
      timer = LiveTvTimerInfoDto(
        programId = programId,
        isPrePaddingRequired = prePaddingSeconds != null,
        isPostPaddingRequired = postPaddingSeconds != null,
        prePaddingSeconds = prePaddingSeconds ?: 0,
        postPaddingSeconds = postPaddingSeconds ?: 0,
      ),
    )

  suspend fun cancelTimer(timerId: String): JellyfinResult<Unit> =
    liveTvService.cancelTimer(timerId = timerId)

  suspend fun getSeriesTimers(): JellyfinResult<List<SeriesRecordingTimer>> =
    liveTvService.getSeriesTimers().mapSuccessTo {
      items.mapNotNull { it.toSeriesRecordingTimer() }
    }

  /**
   * Creates a series timer for a program.
   *
   * This is a two-step operation: first the server is asked for a defaults
   * DTO populated from the program (name, channel, start/end, etc.), then we
   * apply the requested rule overrides and POST the result. Doing it this way
   * means we don't have to know any series details up-front - just the program
   * to base the rule on.
   */
  suspend fun createSeriesTimer(
    programId: String,
    recordAnyChannel: Boolean,
    recordAnyTime: Boolean,
    recordNewOnly: Boolean,
  ): JellyfinResult<Unit> =
    liveTvService.getDefaultSeriesTimer(programId = programId).flatMapSuccessTo {
      liveTvService.createSeriesTimer(
        timer = copy(
          recordAnyChannel = recordAnyChannel,
          recordAnyTime = recordAnyTime,
          recordNewOnly = recordNewOnly,
        ),
      )
    }

  suspend fun cancelSeriesTimer(timerId: String): JellyfinResult<Unit> =
    liveTvService.cancelSeriesTimer(timerId = timerId)

  suspend fun getGuideInfo(): JellyfinResult<GuideInfo> =
    liveTvService.getGuideInfo().mapSuccessTo {
      GuideInfo(
        startDate = startDate,
        endDate = endDate,
      )
    }
}

private fun BaseItemDto.toTvChannel(): TvChannel? {
  val itemId = id ?: return null

  return TvChannel(
    id = itemId,
    name = name.orEmpty(),
    number = number ?: channelNumber,
    type = channelType,
    primaryImageTag = imageTags["Primary"],
  )
}

private fun BaseItemDto.toTvProgram(): TvProgram? {
  val itemId = id ?: return null
  val channelId = channelId ?: return null

  return TvProgram(
    id = itemId,
    channelId = channelId,
    channelName = channelName,
    name = name.orEmpty(),
    episodeTitle = episodeTitle,
    overview = overview,
    startDate = startDate,
    endDate = endDate,
    officialRating = officialRating,
    communityRating = communityRating,
    productionYear = productionYear,
    genres = genres,
    primaryImageTag = imageTags["Primary"],
  )
}

private fun BaseItemDto.toTvRecording(): TvRecording? {
  val itemId = id ?: return null

  return TvRecording(
    id = itemId,
    name = name.orEmpty(),
    episodeTitle = episodeTitle,
    overview = overview,
    channelId = channelId,
    channelName = channelName,
    programId = programId,
    startDate = startDate,
    endDate = endDate,
    runTimeTicks = runTimeTicks,
    status = RecordingStatus.fromApiValue(status),
    path = path,
    primaryImageTag = imageTags["Primary"],
  )
}

private fun LiveTvTimerInfoDto.toRecordingTimer(): RecordingTimer? {
  val timerId = id ?: return null

  return RecordingTimer(
    id = timerId,
    channelId = channelId,
    channelName = channelName,
    programId = programId,
    name = name.orEmpty(),
    overview = overview,
    startDate = startDate,
    endDate = endDate,
    status = RecordingStatus.fromApiValue(status),
    isPrePaddingRequired = isPrePaddingRequired,
    isPostPaddingRequired = isPostPaddingRequired,
    prePaddingSeconds = prePaddingSeconds,
    postPaddingSeconds = postPaddingSeconds,
  )
}

private fun LiveTvSeriesTimerInfoDto.toSeriesRecordingTimer(): SeriesRecordingTimer? {
  val timerId = id ?: return null

  return SeriesRecordingTimer(
    id = timerId,
    channelId = channelId,
    channelName = channelName,
    name = name.orEmpty(),
    overview = overview,
    recordAnyChannel = recordAnyChannel,
    recordAnyTime = recordAnyTime,
    recordNewOnly = recordNewOnly,
    days = days,
    keepUpTo = keepUpTo,
    keepUntil = keepUntil,
    startDate = startDate,
    endDate = endDate,
  )
}
