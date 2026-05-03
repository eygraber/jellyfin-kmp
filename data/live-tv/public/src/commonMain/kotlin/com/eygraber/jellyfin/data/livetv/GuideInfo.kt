package com.eygraber.jellyfin.data.livetv

/**
 * Metadata describing the available range of EPG data on the server.
 *
 * Times are exposed as ISO-8601 strings (as returned by Jellyfin) so callers
 * may parse them with whichever date/time library suits them.
 */
data class GuideInfo(
  /**
   * Earliest start time covered by the guide.
   */
  val startDate: String?,
  /**
   * Latest end time covered by the guide.
   */
  val endDate: String?,
)
