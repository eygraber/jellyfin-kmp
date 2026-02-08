package com.eygraber.jellyfin.data.playback.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.data.playback.PlaybackRepository
import com.eygraber.jellyfin.data.playback.PlaybackSession
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

/**
 * Default implementation of [PlaybackRepository].
 *
 * Coordinates with [PlaybackRemoteDataSource] for all server operations.
 * This repository is stateless per project conventions.
 */
@ContributesBinding(AppScope::class)
class DefaultPlaybackRepository(
  private val remoteDataSource: PlaybackRemoteDataSource,
) : PlaybackRepository {
  override suspend fun getPlaybackSession(
    itemId: String,
  ): JellyfinResult<PlaybackSession> =
    remoteDataSource.getPlaybackSession(itemId = itemId)

  override suspend fun reportPlaybackStart(
    session: PlaybackSession,
  ): JellyfinResult<Unit> =
    remoteDataSource.reportStart(session = session)

  override suspend fun reportPlaybackProgress(
    session: PlaybackSession,
    positionTicks: Long,
    isPaused: Boolean,
  ): JellyfinResult<Unit> =
    remoteDataSource.reportProgress(
      session = session,
      positionTicks = positionTicks,
      isPaused = isPaused,
    )

  override suspend fun reportPlaybackStopped(
    session: PlaybackSession,
    positionTicks: Long?,
  ): JellyfinResult<Unit> =
    remoteDataSource.reportStopped(
      session = session,
      positionTicks = positionTicks,
    )

  override suspend fun markPlayed(itemId: String): JellyfinResult<Unit> =
    remoteDataSource.markPlayed(itemId = itemId)

  override suspend fun markUnplayed(itemId: String): JellyfinResult<Unit> =
    remoteDataSource.markUnplayed(itemId = itemId)
}
