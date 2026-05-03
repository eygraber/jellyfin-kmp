package com.eygraber.jellyfin.sdk.core.api.activity

import com.eygraber.jellyfin.sdk.core.SdkResult
import com.eygraber.jellyfin.sdk.core.api.BaseApi
import com.eygraber.jellyfin.sdk.core.api.JellyfinApiClient
import com.eygraber.jellyfin.sdk.core.model.ActivityLogEntryQueryResult

/**
 * Server activity / audit log.
 *
 * Requires administrator privileges.
 */
class ActivityLogApi(
  apiClient: JellyfinApiClient,
) : BaseApi(apiClient) {
  /**
   * Lists activity log entries.
   *
   * @param startIndex Pagination start index.
   * @param limit Maximum number of entries to return.
   * @param minDate ISO-8601 lower bound (inclusive) for entry date.
   * @param hasUserId If true, only include entries with a user ID; if false, only include
   *                  entries without a user ID. When null, both are returned.
   */
  suspend fun getEntries(
    startIndex: Int? = null,
    limit: Int? = null,
    minDate: String? = null,
    hasUserId: Boolean? = null,
  ): SdkResult<ActivityLogEntryQueryResult> = get(
    path = "System/ActivityLog/Entries",
    queryParams = mapOf(
      "startIndex" to startIndex,
      "limit" to limit,
      "minDate" to minDate,
      "hasUserId" to hasUserId,
    ),
  )
}
