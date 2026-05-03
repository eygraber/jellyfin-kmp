package com.eygraber.jellyfin.data.livetv

/**
 * A program in the Electronic Program Guide (EPG).
 *
 * Programs are linked to a [channelId] and have a defined airing window. Times
 * are exposed as ISO-8601 strings (as returned by Jellyfin) so consumers can
 * choose the date/time library they want for parsing and formatting.
 */
data class TvProgram(
  val id: String,
  val channelId: String,
  val channelName: String?,
  val name: String,
  val episodeTitle: String?,
  val overview: String?,
  /**
   * ISO-8601 timestamp when the program starts airing.
   */
  val startDate: String?,
  /**
   * ISO-8601 timestamp when the program finishes airing.
   */
  val endDate: String?,
  val officialRating: String?,
  val communityRating: Float?,
  val productionYear: Int?,
  val genres: List<String> = emptyList(),
  val primaryImageTag: String?,
)
