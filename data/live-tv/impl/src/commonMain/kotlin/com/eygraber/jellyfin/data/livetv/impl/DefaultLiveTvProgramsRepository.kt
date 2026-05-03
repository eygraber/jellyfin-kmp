package com.eygraber.jellyfin.data.livetv.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.data.livetv.LiveTvProgramsRepository
import com.eygraber.jellyfin.data.livetv.TvProgram
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

/**
 * Default implementation of [LiveTvProgramsRepository].
 *
 * Stateless: every call delegates to [LiveTvRemoteDataSource].
 */
@ContributesBinding(AppScope::class)
internal class DefaultLiveTvProgramsRepository(
  private val remoteDataSource: LiveTvRemoteDataSource,
) : LiveTvProgramsRepository {
  override suspend fun getPrograms(
    channelIds: List<String>?,
    minStartDate: String?,
    maxStartDate: String?,
    limit: Int?,
  ): JellyfinResult<List<TvProgram>> =
    remoteDataSource.getPrograms(
      channelIds = channelIds,
      minStartDate = minStartDate,
      maxStartDate = maxStartDate,
      limit = limit,
    )

  override suspend fun getCurrentPrograms(
    channelIds: List<String>?,
  ): JellyfinResult<List<TvProgram>> =
    remoteDataSource.getCurrentPrograms(channelIds = channelIds)

  override suspend fun getUpcomingPrograms(
    channelIds: List<String>?,
    limit: Int?,
  ): JellyfinResult<List<TvProgram>> =
    remoteDataSource.getUpcomingPrograms(
      channelIds = channelIds,
      limit = limit,
    )

  override suspend fun getProgram(programId: String): JellyfinResult<TvProgram> =
    remoteDataSource.getProgram(programId = programId)
}
