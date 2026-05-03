package com.eygraber.jellyfin.data.livetv.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.data.livetv.LiveTvChannelsRepository
import com.eygraber.jellyfin.data.livetv.LiveTvPaginatedResult
import com.eygraber.jellyfin.data.livetv.TvChannel
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

/**
 * Default implementation of [LiveTvChannelsRepository].
 *
 * Stateless: every call delegates to [LiveTvRemoteDataSource]. Caching of
 * channel lists belongs at the screen/compositor level.
 */
@ContributesBinding(AppScope::class)
internal class DefaultLiveTvChannelsRepository(
  private val remoteDataSource: LiveTvRemoteDataSource,
) : LiveTvChannelsRepository {
  override suspend fun getChannels(
    startIndex: Int,
    limit: Int,
    channelType: String?,
    isFavorite: Boolean?,
  ): JellyfinResult<LiveTvPaginatedResult<TvChannel>> =
    remoteDataSource.getChannels(
      startIndex = startIndex,
      limit = limit,
      channelType = channelType,
      isFavorite = isFavorite,
    )

  override suspend fun getChannel(channelId: String): JellyfinResult<TvChannel> =
    remoteDataSource.getChannel(channelId = channelId)

  override suspend fun searchChannels(
    searchTerm: String,
    limit: Int,
  ): JellyfinResult<LiveTvPaginatedResult<TvChannel>> =
    remoteDataSource.searchChannels(
      searchTerm = searchTerm,
      limit = limit,
    )
}
