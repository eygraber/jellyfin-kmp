package com.eygraber.jellyfin.data.admin.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.data.admin.AdminServerConfiguration
import com.eygraber.jellyfin.data.admin.AdminServerInfo
import com.eygraber.jellyfin.data.admin.ScheduledTask
import com.eygraber.jellyfin.data.admin.ServerAdminRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

/**
 * Default implementation of [ServerAdminRepository].
 */
@ContributesBinding(AppScope::class)
internal class DefaultServerAdminRepository(
  private val remoteDataSource: AdminRemoteDataSource,
) : ServerAdminRepository {
  override suspend fun getServerInfo(): JellyfinResult<AdminServerInfo> =
    remoteDataSource.getServerInfo()

  override suspend fun getConfiguration(): JellyfinResult<AdminServerConfiguration> =
    remoteDataSource.getConfiguration()

  override suspend fun updateConfiguration(
    configuration: AdminServerConfiguration,
  ): JellyfinResult<Unit> =
    remoteDataSource.updateConfiguration(configuration = configuration)

  override suspend fun restartServer(): JellyfinResult<Unit> =
    remoteDataSource.restartServer()

  override suspend fun shutdownServer(): JellyfinResult<Unit> =
    remoteDataSource.shutdownServer()

  override suspend fun getScheduledTasks(
    isHidden: Boolean?,
    isEnabled: Boolean?,
  ): JellyfinResult<List<ScheduledTask>> =
    remoteDataSource.getScheduledTasks(isHidden = isHidden, isEnabled = isEnabled)

  override suspend fun getScheduledTask(taskId: String): JellyfinResult<ScheduledTask> =
    remoteDataSource.getScheduledTask(taskId = taskId)

  override suspend fun startScheduledTask(taskId: String): JellyfinResult<Unit> =
    remoteDataSource.startScheduledTask(taskId = taskId)

  override suspend fun stopScheduledTask(taskId: String): JellyfinResult<Unit> =
    remoteDataSource.stopScheduledTask(taskId = taskId)
}
