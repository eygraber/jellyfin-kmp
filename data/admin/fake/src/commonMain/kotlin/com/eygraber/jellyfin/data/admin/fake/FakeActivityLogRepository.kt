package com.eygraber.jellyfin.data.admin.fake

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.data.admin.ActivityLogRepository
import com.eygraber.jellyfin.data.admin.ServerActivityEntry
import com.eygraber.jellyfin.data.admin.ServerActivityPage

/**
 * In-memory fake of [ActivityLogRepository] for tests.
 *
 * Pagination is performed against [entries] using `startIndex`/`limit`.
 * `minDate` and `hasUserId` filter the entries before pagination.
 */
class FakeActivityLogRepository(
  var entries: List<ServerActivityEntry> = emptyList(),
) : ActivityLogRepository {
  var nextResult: JellyfinResult<ServerActivityPage>? = null

  /**
   * Last query parameters received, useful for assertions.
   */
  var lastQuery: ActivityLogQuery? = null
    private set

  override suspend fun getEntries(
    startIndex: Int,
    limit: Int,
    minDate: String?,
    hasUserId: Boolean?,
  ): JellyfinResult<ServerActivityPage> {
    lastQuery = ActivityLogQuery(
      startIndex = startIndex,
      limit = limit,
      minDate = minDate,
      hasUserId = hasUserId,
    )
    nextResult?.let { override ->
      nextResult = null
      return override
    }

    val filtered = entries.filter { entry ->
      val userMatches = when(hasUserId) {
        null -> true
        true -> entry.userId != null
        false -> entry.userId == null
      }
      val dateMatches = minDate == null ||
        run {
          val date = entry.date
          date != null && date >= minDate
        }
      userMatches && dateMatches
    }

    val page = filtered.drop(startIndex).take(limit)

    return JellyfinResult.Success(
      ServerActivityPage(
        items = page,
        totalRecordCount = filtered.size,
        startIndex = startIndex,
      ),
    )
  }

  data class ActivityLogQuery(
    val startIndex: Int,
    val limit: Int,
    val minDate: String?,
    val hasUserId: Boolean?,
  )
}
