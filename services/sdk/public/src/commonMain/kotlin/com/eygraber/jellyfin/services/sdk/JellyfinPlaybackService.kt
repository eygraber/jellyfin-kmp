package com.eygraber.jellyfin.services.sdk

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.sdk.core.model.PlaybackInfoResponse
import com.eygraber.jellyfin.sdk.core.model.PlaybackProgressInfo
import com.eygraber.jellyfin.sdk.core.model.PlaybackStartInfo
import com.eygraber.jellyfin.sdk.core.model.PlaybackStopInfo

/**
 * Service for managing media playback sessions with the Jellyfin server.
 *
 * Provides operations to get playback info, generate stream URLs,
 * and report playback state to the server.
 */
interface JellyfinPlaybackService {
  /**
   * Gets playback info for an item, including available media sources
   * and transcoding options.
   *
   * @param itemId The item to get playback info for.
   * @return A [JellyfinResult] containing the [PlaybackInfoResponse].
   */
  suspend fun getPlaybackInfo(itemId: String): JellyfinResult<PlaybackInfoResponse>

  /**
   * Generates a direct stream URL for video playback.
   *
   * @param itemId The item ID.
   * @param mediaSourceId The selected media source ID.
   * @param container The container format (e.g., "mp4", "mkv").
   * @param audioCodec The preferred audio codec.
   * @param videoCodec The preferred video codec.
   * @param maxWidth Maximum video width.
   * @param maxHeight Maximum video height.
   * @return The fully qualified stream URL.
   */
  @Suppress("LongParameterList")
  fun getVideoStreamUrl(
    itemId: String,
    mediaSourceId: String? = null,
    container: String? = null,
    audioCodec: String? = null,
    videoCodec: String? = null,
    maxWidth: Int? = null,
    maxHeight: Int? = null,
  ): String

  /**
   * Generates a stream URL for audio playback.
   *
   * @param itemId The item ID.
   * @param mediaSourceId The selected media source ID.
   * @param container The container format (e.g., "mp3", "flac").
   * @param maxBitrate Maximum streaming bitrate.
   * @return The fully qualified audio stream URL.
   */
  fun getAudioStreamUrl(
    itemId: String,
    mediaSourceId: String? = null,
    container: String? = null,
    maxBitrate: Int? = null,
  ): String

  /**
   * Reports that playback has started for an item.
   *
   * @param info The playback start information.
   * @return A [JellyfinResult] indicating success or failure.
   */
  suspend fun reportPlaybackStart(info: PlaybackStartInfo): JellyfinResult<Unit>

  /**
   * Reports playback progress for the currently playing item.
   *
   * @param info The playback progress information.
   * @return A [JellyfinResult] indicating success or failure.
   */
  suspend fun reportPlaybackProgress(info: PlaybackProgressInfo): JellyfinResult<Unit>

  /**
   * Reports that playback has stopped.
   *
   * @param info The playback stop information.
   * @return A [JellyfinResult] indicating success or failure.
   */
  suspend fun reportPlaybackStopped(info: PlaybackStopInfo): JellyfinResult<Unit>

  /**
   * Marks an item as played for the current user.
   *
   * @param itemId The item to mark as played.
   * @return A [JellyfinResult] indicating success or failure.
   */
  suspend fun markPlayed(itemId: String): JellyfinResult<Unit>

  /**
   * Marks an item as unplayed for the current user.
   *
   * @param itemId The item to mark as unplayed.
   * @return A [JellyfinResult] indicating success or failure.
   */
  suspend fun markUnplayed(itemId: String): JellyfinResult<Unit>
}
