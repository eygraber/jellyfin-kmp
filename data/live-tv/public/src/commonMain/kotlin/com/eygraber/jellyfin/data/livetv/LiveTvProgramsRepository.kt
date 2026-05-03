package com.eygraber.jellyfin.data.livetv

import com.eygraber.jellyfin.common.JellyfinResult

/**
 * Repository for accessing EPG (Electronic Program Guide) data.
 *
 * Times are passed and returned as ISO-8601 strings. The server applies
 * filtering using these timestamps verbatim.
 */
interface LiveTvProgramsRepository {
  /**
   * Gets programs airing on the given channels within an optional time window.
   *
   * @param channelIds Filter to specific channels. Empty/null returns programs for all channels.
   * @param minStartDate ISO-8601 lower bound (inclusive) for program start time.
   * @param maxStartDate ISO-8601 upper bound (inclusive) for program start time.
   * @param limit Maximum number of programs to return.
   */
  suspend fun getPrograms(
    channelIds: List<String>? = null,
    minStartDate: String? = null,
    maxStartDate: String? = null,
    limit: Int? = null,
  ): JellyfinResult<List<TvProgram>>

  /**
   * Gets the program currently airing on each requested channel.
   *
   * @param channelIds Optional channel filter. If null/empty, returns currently airing programs across all channels.
   */
  suspend fun getCurrentPrograms(
    channelIds: List<String>? = null,
  ): JellyfinResult<List<TvProgram>>

  /**
   * Gets programs that have not yet aired (i.e. upcoming).
   *
   * @param channelIds Optional channel filter.
   * @param limit Maximum number of programs to return.
   */
  suspend fun getUpcomingPrograms(
    channelIds: List<String>? = null,
    limit: Int? = null,
  ): JellyfinResult<List<TvProgram>>

  /**
   * Gets a single program by ID.
   */
  suspend fun getProgram(programId: String): JellyfinResult<TvProgram>
}
