package com.eygraber.jellyfin.data.livetv

import com.eygraber.jellyfin.common.JellyfinResult

/**
 * Repository for EPG-level metadata about the guide itself.
 */
interface LiveTvGuideRepository {
  /**
   * Gets metadata describing the date range of available EPG data.
   */
  suspend fun getGuideInfo(): JellyfinResult<GuideInfo>
}
