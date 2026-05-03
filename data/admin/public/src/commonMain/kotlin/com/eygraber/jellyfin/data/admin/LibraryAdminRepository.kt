package com.eygraber.jellyfin.data.admin

import com.eygraber.jellyfin.common.JellyfinResult

/**
 * Repository for administrative library operations.
 *
 * Requires an authenticated administrator session.
 */
interface LibraryAdminRepository {
  /**
   * Lists the virtual folders ("libraries") configured on the server.
   */
  suspend fun getLibraries(): JellyfinResult<List<ManagedLibrary>>

  /**
   * Triggers a server-wide library scan.
   *
   * The server queues the scan and returns immediately; observe progress via
   * [getLibraries] or [com.eygraber.jellyfin.data.admin.ScheduledTask] entries.
   */
  suspend fun refreshLibrary(): JellyfinResult<Unit>
}
