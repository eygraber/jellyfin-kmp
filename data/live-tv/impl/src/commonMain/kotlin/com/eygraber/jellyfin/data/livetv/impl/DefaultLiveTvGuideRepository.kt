package com.eygraber.jellyfin.data.livetv.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.data.livetv.GuideInfo
import com.eygraber.jellyfin.data.livetv.LiveTvGuideRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

/**
 * Default implementation of [LiveTvGuideRepository].
 */
@ContributesBinding(AppScope::class)
internal class DefaultLiveTvGuideRepository(
  private val remoteDataSource: LiveTvRemoteDataSource,
) : LiveTvGuideRepository {
  override suspend fun getGuideInfo(): JellyfinResult<GuideInfo> =
    remoteDataSource.getGuideInfo()
}
