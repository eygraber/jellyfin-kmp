package com.eygraber.jellyfin.data.playback

import com.eygraber.jellyfin.common.JellyfinResult

/**
 * Repository for managing media playback sessions.
 *
 * Coordinates between the Jellyfin server for playback info, stream URLs,
 * and progress reporting. This repository is the primary API for starting
 * and managing playback from the UI layer.
 */
interface PlaybackRepository {
  /**
   * Gets playback information for an item, including available media sources
   * and the best stream to play.
   *
   * This evaluates the available media sources and selects the best one
   * based on device capabilities and server transcoding support.
   *
   * @param itemId The item to prepare for playback.
   * @return A [JellyfinResult] containing [PlaybackSession] with the selected stream.
   */
  suspend fun getPlaybackSession(itemId: String): JellyfinResult<PlaybackSession>

  /**
   * Reports that playback has started.
   *
   * Should be called when the player begins playing the media.
   *
   * @param session The current playback session.
   * @return A [JellyfinResult] indicating success or failure.
   */
  suspend fun reportPlaybackStart(session: PlaybackSession): JellyfinResult<Unit>

  /**
   * Reports playback progress to the server.
   *
   * Should be called periodically during playback (e.g., every 10 seconds).
   *
   * @param session The current playback session.
   * @param positionTicks The current playback position in ticks (1 tick = 100 nanoseconds).
   * @param isPaused Whether playback is currently paused.
   * @return A [JellyfinResult] indicating success or failure.
   */
  suspend fun reportPlaybackProgress(
    session: PlaybackSession,
    positionTicks: Long,
    isPaused: Boolean = false,
  ): JellyfinResult<Unit>

  /**
   * Reports that playback has stopped.
   *
   * Should be called when the player stops playing, whether by user action
   * or reaching the end of the media.
   *
   * @param session The current playback session.
   * @param positionTicks The final playback position in ticks.
   * @return A [JellyfinResult] indicating success or failure.
   */
  suspend fun reportPlaybackStopped(
    session: PlaybackSession,
    positionTicks: Long?,
  ): JellyfinResult<Unit>

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
