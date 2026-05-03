package com.eygraber.jellyfin.data.admin.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.data.admin.LibraryAdminRepository
import com.eygraber.jellyfin.data.admin.ManagedLibrary
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

/**
 * Default implementation of [LibraryAdminRepository].
 */
@ContributesBinding(AppScope::class)
internal class DefaultLibraryAdminRepository(
  private val remoteDataSource: AdminRemoteDataSource,
) : LibraryAdminRepository {
  override suspend fun getLibraries(): JellyfinResult<List<ManagedLibrary>> =
    remoteDataSource.getLibraries()

  override suspend fun refreshLibrary(): JellyfinResult<Unit> =
    remoteDataSource.refreshLibrary()
}
