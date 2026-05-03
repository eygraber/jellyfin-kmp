package com.eygraber.jellyfin.data.livetv

import com.eygraber.jellyfin.common.JellyfinResult

/**
 * Repository for browsing live TV channels.
 *
 * Channel lookups are stateless and always go to the network. Callers should
 * cache results at the screen level if they need to retain a list across
 * recompositions or process death.
 */
interface LiveTvChannelsRepository {
  /**
   * Gets a page of channels.
   *
   * @param startIndex Index of the first channel to return (for pagination).
   * @param limit Maximum number of channels to return.
   * @param channelType Optional filter by channel type (e.g. "Tv", "Radio").
   * @param isFavorite If true, only return channels marked as favorites.
   */
  suspend fun getChannels(
    startIndex: Int = 0,
    limit: Int = DEFAULT_PAGE_SIZE,
    channelType: String? = null,
    isFavorite: Boolean? = null,
  ): JellyfinResult<LiveTvPaginatedResult<TvChannel>>

  /**
   * Gets a single channel by ID.
   */
  suspend fun getChannel(channelId: String): JellyfinResult<TvChannel>

  /**
   * Searches channels by a case-insensitive substring match against the channel name.
   *
   * @param searchTerm The term to search for. Empty strings return all channels.
   * @param limit Maximum number of results to return.
   */
  suspend fun searchChannels(
    searchTerm: String,
    limit: Int = DEFAULT_PAGE_SIZE,
  ): JellyfinResult<LiveTvPaginatedResult<TvChannel>>

  companion object {
    const val DEFAULT_PAGE_SIZE = 50
  }
}
