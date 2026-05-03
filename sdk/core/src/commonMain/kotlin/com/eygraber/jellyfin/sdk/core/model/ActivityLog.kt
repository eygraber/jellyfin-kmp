@file:Suppress("BooleanPropertyNaming")

package com.eygraber.jellyfin.sdk.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Paginated container for activity log entries.
 *
 * Returned by `GET /System/ActivityLog/Entries`.
 */
@Serializable
data class ActivityLogEntryQueryResult(
  @SerialName("Items") val items: List<ActivityLogEntry> = emptyList(),
  @SerialName("TotalRecordCount") val totalRecordCount: Int = 0,
  @SerialName("StartIndex") val startIndex: Int = 0,
)

/**
 * A single activity log entry from the Jellyfin server.
 */
@Serializable
data class ActivityLogEntry(
  @SerialName("Id") val id: Long? = null,
  @SerialName("Name") val name: String? = null,
  @SerialName("Overview") val overview: String? = null,
  @SerialName("ShortOverview") val shortOverview: String? = null,
  @SerialName("Type") val type: String? = null,
  @SerialName("ItemId") val itemId: String? = null,
  @SerialName("Date") val date: String? = null,
  @SerialName("UserId") val userId: String? = null,
  @SerialName("UserPrimaryImageTag") val userPrimaryImageTag: String? = null,
  @SerialName("Severity") val severity: String? = null,
)
