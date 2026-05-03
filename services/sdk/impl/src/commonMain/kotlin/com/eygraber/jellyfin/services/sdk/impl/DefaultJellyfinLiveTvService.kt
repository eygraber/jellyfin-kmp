package com.eygraber.jellyfin.services.sdk.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.sdk.core.JellyfinSdk
import com.eygraber.jellyfin.sdk.core.api.livetv.liveTvApi
import com.eygraber.jellyfin.sdk.core.model.BaseItemDto
import com.eygraber.jellyfin.sdk.core.model.LiveTvChannelResult
import com.eygraber.jellyfin.sdk.core.model.LiveTvGuideInfo
import com.eygraber.jellyfin.sdk.core.model.LiveTvProgramResult
import com.eygraber.jellyfin.sdk.core.model.LiveTvRecordingResult
import com.eygraber.jellyfin.sdk.core.model.LiveTvSeriesTimerInfoDto
import com.eygraber.jellyfin.sdk.core.model.LiveTvSeriesTimerInfoResult
import com.eygraber.jellyfin.sdk.core.model.LiveTvTimerInfoDto
import com.eygraber.jellyfin.services.logging.JellyfinLogger
import com.eygraber.jellyfin.services.sdk.JellyfinLiveTvService
import com.eygraber.jellyfin.services.sdk.JellyfinSessionManager
import com.eygraber.jellyfin.services.sdk.toJellyfinResult
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

/**
 * Default implementation of [JellyfinLiveTvService].
 *
 * Uses the [JellyfinSdk] to access Live TV functionality. Mutating endpoints
 * require an active authenticated session with a valid user ID.
 */
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
internal class DefaultJellyfinLiveTvService(
  private val sdk: JellyfinSdk,
  private val sessionManager: JellyfinSessionManager,
  private val logger: JellyfinLogger,
) : JellyfinLiveTvService {
  @Suppress("LongParameterList")
  override suspend fun getChannels(
    limit: Int?,
    startIndex: Int?,
    isFavorite: Boolean?,
    channelType: String?,
    searchTerm: String?,
  ): JellyfinResult<LiveTvChannelResult> {
    val serverInfo = sessionManager.currentServer.value
      ?: return notConnectedError()

    val userId = serverInfo.userId
      ?: return notAuthenticatedError()

    logger.debug(tag = TAG, message = "Fetching live TV channels for user: $userId")

    val apiClient = sdk.createApiClient(serverInfo = serverInfo)
    return try {
      apiClient.liveTvApi.getChannels(
        userId = userId,
        limit = limit,
        startIndex = startIndex,
        isFavorite = isFavorite,
        channelType = channelType,
        searchTerm = searchTerm,
      ).toJellyfinResult()
    }
    finally {
      apiClient.close()
    }
  }

  override suspend fun getChannel(
    channelId: String,
  ): JellyfinResult<BaseItemDto> {
    val serverInfo = sessionManager.currentServer.value
      ?: return notConnectedError()

    val userId = serverInfo.userId
      ?: return notAuthenticatedError()

    logger.debug(tag = TAG, message = "Fetching live TV channel: $channelId")

    val apiClient = sdk.createApiClient(serverInfo = serverInfo)
    return try {
      apiClient.liveTvApi.getChannel(
        channelId = channelId,
        userId = userId,
      ).toJellyfinResult()
    }
    finally {
      apiClient.close()
    }
  }

  @Suppress("LongParameterList")
  override suspend fun getPrograms(
    channelIds: List<String>?,
    limit: Int?,
    isAiring: Boolean?,
    hasAired: Boolean?,
    minStartDate: String?,
    maxStartDate: String?,
  ): JellyfinResult<LiveTvProgramResult> {
    val serverInfo = sessionManager.currentServer.value
      ?: return notConnectedError()

    val userId = serverInfo.userId
      ?: return notAuthenticatedError()

    logger.debug(tag = TAG, message = "Fetching live TV programs for user: $userId")

    val apiClient = sdk.createApiClient(serverInfo = serverInfo)
    return try {
      apiClient.liveTvApi.getPrograms(
        userId = userId,
        channelIds = channelIds,
        limit = limit,
        isAiring = isAiring,
        hasAired = hasAired,
        minStartDate = minStartDate,
        maxStartDate = maxStartDate,
      ).toJellyfinResult()
    }
    finally {
      apiClient.close()
    }
  }

  override suspend fun getProgram(programId: String): JellyfinResult<BaseItemDto> {
    val serverInfo = sessionManager.currentServer.value
      ?: return notConnectedError()

    val userId = serverInfo.userId
      ?: return notAuthenticatedError()

    logger.debug(tag = TAG, message = "Fetching program: $programId")

    val apiClient = sdk.createApiClient(serverInfo = serverInfo)
    return try {
      apiClient.liveTvApi.getProgram(
        programId = programId,
        userId = userId,
      ).toJellyfinResult()
    }
    finally {
      apiClient.close()
    }
  }

  override suspend fun getRecordings(
    limit: Int?,
    startIndex: Int?,
    status: String?,
  ): JellyfinResult<LiveTvRecordingResult> {
    val serverInfo = sessionManager.currentServer.value
      ?: return notConnectedError()

    val userId = serverInfo.userId
      ?: return notAuthenticatedError()

    logger.debug(tag = TAG, message = "Fetching recordings for user: $userId")

    val apiClient = sdk.createApiClient(serverInfo = serverInfo)
    return try {
      apiClient.liveTvApi.getRecordings(
        userId = userId,
        limit = limit,
        startIndex = startIndex,
        status = status,
      ).toJellyfinResult()
    }
    finally {
      apiClient.close()
    }
  }

  override suspend fun getRecording(recordingId: String): JellyfinResult<BaseItemDto> {
    val serverInfo = sessionManager.currentServer.value
      ?: return notConnectedError()

    val userId = serverInfo.userId
      ?: return notAuthenticatedError()

    logger.debug(tag = TAG, message = "Fetching recording: $recordingId")

    val apiClient = sdk.createApiClient(serverInfo = serverInfo)
    return try {
      apiClient.liveTvApi.getRecording(
        recordingId = recordingId,
        userId = userId,
      ).toJellyfinResult()
    }
    finally {
      apiClient.close()
    }
  }

  override suspend fun deleteRecording(recordingId: String): JellyfinResult<Unit> {
    val serverInfo = sessionManager.currentServer.value
      ?: return notConnectedError()

    logger.debug(tag = TAG, message = "Deleting recording: $recordingId")

    val apiClient = sdk.createApiClient(serverInfo = serverInfo)
    return try {
      apiClient.liveTvApi.deleteRecording(
        recordingId = recordingId,
      ).toJellyfinResult()
    }
    finally {
      apiClient.close()
    }
  }

  override suspend fun getTimers(
    channelId: String?,
  ): JellyfinResult<List<LiveTvTimerInfoDto>> {
    val serverInfo = sessionManager.currentServer.value
      ?: return notConnectedError()

    logger.debug(tag = TAG, message = "Fetching timers for channel: ${channelId ?: "<all>"}")

    val apiClient = sdk.createApiClient(serverInfo = serverInfo)
    return try {
      apiClient.liveTvApi.getTimers(
        channelId = channelId,
      ).toJellyfinResult()
    }
    finally {
      apiClient.close()
    }
  }

  override suspend fun createTimer(timer: LiveTvTimerInfoDto): JellyfinResult<Unit> {
    val serverInfo = sessionManager.currentServer.value
      ?: return notConnectedError()

    logger.debug(tag = TAG, message = "Creating timer for program: ${timer.programId ?: "<unknown>"}")

    val apiClient = sdk.createApiClient(serverInfo = serverInfo)
    return try {
      apiClient.liveTvApi.createTimer(body = timer).toJellyfinResult()
    }
    finally {
      apiClient.close()
    }
  }

  override suspend fun cancelTimer(timerId: String): JellyfinResult<Unit> {
    val serverInfo = sessionManager.currentServer.value
      ?: return notConnectedError()

    logger.debug(tag = TAG, message = "Cancelling timer: $timerId")

    val apiClient = sdk.createApiClient(serverInfo = serverInfo)
    return try {
      apiClient.liveTvApi.cancelTimer(timerId = timerId).toJellyfinResult()
    }
    finally {
      apiClient.close()
    }
  }

  override suspend fun getSeriesTimers(): JellyfinResult<LiveTvSeriesTimerInfoResult> {
    val serverInfo = sessionManager.currentServer.value
      ?: return notConnectedError()

    logger.debug(tag = TAG, message = "Fetching series timers")

    val apiClient = sdk.createApiClient(serverInfo = serverInfo)
    return try {
      apiClient.liveTvApi.getSeriesTimers().toJellyfinResult()
    }
    finally {
      apiClient.close()
    }
  }

  override suspend fun getDefaultSeriesTimer(
    programId: String,
  ): JellyfinResult<LiveTvSeriesTimerInfoDto> {
    val serverInfo = sessionManager.currentServer.value
      ?: return notConnectedError()

    logger.debug(tag = TAG, message = "Fetching default series timer for program: $programId")

    val apiClient = sdk.createApiClient(serverInfo = serverInfo)
    return try {
      apiClient.liveTvApi.getDefaultSeriesTimer(programId = programId).toJellyfinResult()
    }
    finally {
      apiClient.close()
    }
  }

  override suspend fun createSeriesTimer(
    timer: LiveTvSeriesTimerInfoDto,
  ): JellyfinResult<Unit> {
    val serverInfo = sessionManager.currentServer.value
      ?: return notConnectedError()

    logger.debug(tag = TAG, message = "Creating series timer for: ${timer.name ?: "<unknown>"}")

    val apiClient = sdk.createApiClient(serverInfo = serverInfo)
    return try {
      apiClient.liveTvApi.createSeriesTimer(body = timer).toJellyfinResult()
    }
    finally {
      apiClient.close()
    }
  }

  override suspend fun cancelSeriesTimer(timerId: String): JellyfinResult<Unit> {
    val serverInfo = sessionManager.currentServer.value
      ?: return notConnectedError()

    logger.debug(tag = TAG, message = "Cancelling series timer: $timerId")

    val apiClient = sdk.createApiClient(serverInfo = serverInfo)
    return try {
      apiClient.liveTvApi.cancelSeriesTimer(timerId = timerId).toJellyfinResult()
    }
    finally {
      apiClient.close()
    }
  }

  override suspend fun getGuideInfo(): JellyfinResult<LiveTvGuideInfo> {
    val serverInfo = sessionManager.currentServer.value
      ?: return notConnectedError()

    logger.debug(tag = TAG, message = "Fetching guide info")

    val apiClient = sdk.createApiClient(serverInfo = serverInfo)
    return try {
      apiClient.liveTvApi.getGuideInfo().toJellyfinResult()
    }
    finally {
      apiClient.close()
    }
  }

  private fun <T> notConnectedError(): JellyfinResult<T> = JellyfinResult.Error(
    message = "Not connected to a server",
    isEphemeral = false,
  )

  private fun <T> notAuthenticatedError(): JellyfinResult<T> = JellyfinResult.Error(
    message = "Not authenticated",
    isEphemeral = false,
  )

  companion object {
    private const val TAG = "JellyfinLiveTvService"
  }
}
