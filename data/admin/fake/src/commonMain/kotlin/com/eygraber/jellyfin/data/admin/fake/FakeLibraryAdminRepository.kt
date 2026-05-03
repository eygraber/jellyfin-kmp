package com.eygraber.jellyfin.data.admin.fake

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.data.admin.LibraryAdminRepository
import com.eygraber.jellyfin.data.admin.ManagedLibrary

/**
 * In-memory fake of [LibraryAdminRepository] for tests.
 */
class FakeLibraryAdminRepository(
  var libraries: List<ManagedLibrary> = emptyList(),
) : LibraryAdminRepository {
  var nextResult: JellyfinResult<Any?>? = null
  var refreshCount: Int = 0
    private set

  override suspend fun getLibraries(): JellyfinResult<List<ManagedLibrary>> =
    consumeOverride() ?: JellyfinResult.Success(libraries)

  override suspend fun refreshLibrary(): JellyfinResult<Unit> {
    consumeOverride<Unit>()?.let { return it }
    refreshCount += 1
    return JellyfinResult.Success(Unit)
  }

  @Suppress("UNCHECKED_CAST")
  private fun <T> consumeOverride(): JellyfinResult<T>? {
    val override = nextResult ?: return null
    nextResult = null
    return override as JellyfinResult<T>
  }
}
