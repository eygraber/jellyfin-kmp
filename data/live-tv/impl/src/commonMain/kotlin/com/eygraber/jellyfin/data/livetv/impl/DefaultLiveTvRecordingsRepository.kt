package com.eygraber.jellyfin.data.livetv.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.data.livetv.LiveTvPaginatedResult
import com.eygraber.jellyfin.data.livetv.LiveTvRecordingsRepository
import com.eygraber.jellyfin.data.livetv.RecordingStatus
import com.eygraber.jellyfin.data.livetv.RecordingTimer
import com.eygraber.jellyfin.data.livetv.SeriesRecordingTimer
import com.eygraber.jellyfin.data.livetv.TvRecording
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

/**
 * Default implementation of [LiveTvRecordingsRepository].
 *
 * Stateless: every call delegates to [LiveTvRemoteDataSource].
 */
@ContributesBinding(AppScope::class)
internal class DefaultLiveTvRecordingsRepository(
  private val remoteDataSource: LiveTvRemoteDataSource,
) : LiveTvRecordingsRepository {
  override suspend fun getRecordings(
    startIndex: Int,
    limit: Int,
    status: RecordingStatus?,
  ): JellyfinResult<LiveTvPaginatedResult<TvRecording>> =
    remoteDataSource.getRecordings(
      startIndex = startIndex,
      limit = limit,
      status = status,
    )

  override suspend fun getRecording(recordingId: String): JellyfinResult<TvRecording> =
    remoteDataSource.getRecording(recordingId = recordingId)

  override suspend fun deleteRecording(recordingId: String): JellyfinResult<Unit> =
    remoteDataSource.deleteRecording(recordingId = recordingId)

  override suspend fun getTimers(
    channelId: String?,
  ): JellyfinResult<List<RecordingTimer>> =
    remoteDataSource.getTimers(channelId = channelId)

  override suspend fun createTimer(
    programId: String,
    prePaddingSeconds: Int?,
    postPaddingSeconds: Int?,
  ): JellyfinResult<Unit> =
    remoteDataSource.createTimer(
      programId = programId,
      prePaddingSeconds = prePaddingSeconds,
      postPaddingSeconds = postPaddingSeconds,
    )

  override suspend fun cancelTimer(timerId: String): JellyfinResult<Unit> =
    remoteDataSource.cancelTimer(timerId = timerId)

  override suspend fun getSeriesTimers(): JellyfinResult<List<SeriesRecordingTimer>> =
    remoteDataSource.getSeriesTimers()

  override suspend fun createSeriesTimer(
    programId: String,
    recordAnyChannel: Boolean,
    recordAnyTime: Boolean,
    recordNewOnly: Boolean,
  ): JellyfinResult<Unit> =
    remoteDataSource.createSeriesTimer(
      programId = programId,
      recordAnyChannel = recordAnyChannel,
      recordAnyTime = recordAnyTime,
      recordNewOnly = recordNewOnly,
    )

  override suspend fun cancelSeriesTimer(timerId: String): JellyfinResult<Unit> =
    remoteDataSource.cancelSeriesTimer(timerId = timerId)
}
