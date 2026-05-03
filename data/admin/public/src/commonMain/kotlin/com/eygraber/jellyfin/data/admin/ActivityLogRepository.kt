package com.eygraber.jellyfin.data.admin

import com.eygraber.jellyfin.common.JellyfinResult

/**
 * Repository for the server activity / audit log.
 *
 * Requires an authenticated administrator session.
 */
interface ActivityLogRepository {
  /**
   * Lists activity log entries.
   *
   * @param startIndex Pagination start index (0-based).
   * @param limit Maximum number of entries to return.
   * @param minDate ISO-8601 lower bound (inclusive) for entry date.
   * @param hasUserId If true, only include entries with a user; if false, only
   *                  entries without a user. When null, both are returned.
   */
  suspend fun getEntries(
    startIndex: Int = 0,
    limit: Int = DEFAULT_PAGE_SIZE,
    minDate: String? = null,
    hasUserId: Boolean? = null,
  ): JellyfinResult<ServerActivityPage>

  companion object {
    const val DEFAULT_PAGE_SIZE = 50
  }
}
