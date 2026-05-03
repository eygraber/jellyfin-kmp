package com.eygraber.jellyfin.data.admin.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.data.admin.ActivityLogRepository
import com.eygraber.jellyfin.data.admin.ServerActivityPage
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

/**
 * Default implementation of [ActivityLogRepository].
 */
@ContributesBinding(AppScope::class)
internal class DefaultActivityLogRepository(
  private val remoteDataSource: AdminRemoteDataSource,
) : ActivityLogRepository {
  override suspend fun getEntries(
    startIndex: Int,
    limit: Int,
    minDate: String?,
    hasUserId: Boolean?,
  ): JellyfinResult<ServerActivityPage> =
    remoteDataSource.getActivityLog(
      startIndex = startIndex,
      limit = limit,
      minDate = minDate,
      hasUserId = hasUserId,
    )
}
