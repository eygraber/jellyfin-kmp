package com.eygraber.jellyfin.data.admin

/**
 * A single entry in the server's activity log.
 */
data class ServerActivityEntry(
  val id: Long,
  val name: String,
  val overview: String?,
  val shortOverview: String?,
  val type: String?,
  val date: String?,
  val severity: ActivityLogSeverity,
  val userId: String?,
  val userPrimaryImageTag: String?,
  val itemId: String?,
)

/**
 * Severity of a [ServerActivityEntry].
 *
 * Maps from the server's `Severity` field. [Unknown] covers any value the
 * client doesn't recognize.
 */
enum class ActivityLogSeverity(val apiValue: String) {
  Trace("Trace"),
  Debug("Debug"),
  Information("Information"),
  Warn("Warn"),
  Error("Error"),
  Critical("Critical"),
  None("None"),
  Unknown(""),
  ;

  companion object {
    fun fromApiValue(value: String?): ActivityLogSeverity =
      entries.firstOrNull { it.apiValue.equals(value, ignoreCase = true) } ?: Unknown
  }
}

/**
 * Paginated container of [ServerActivityEntry] returned by the activity log
 * endpoint.
 */
data class ServerActivityPage(
  val items: List<ServerActivityEntry>,
  val totalRecordCount: Int,
  val startIndex: Int,
)
