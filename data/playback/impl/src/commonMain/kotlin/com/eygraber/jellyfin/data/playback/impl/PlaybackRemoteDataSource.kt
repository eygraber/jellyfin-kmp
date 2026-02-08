package com.eygraber.jellyfin.data.playback.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.data.playback.PlayMethod
import com.eygraber.jellyfin.data.playback.PlaybackMediaSource
import com.eygraber.jellyfin.data.playback.PlaybackMediaStream
import com.eygraber.jellyfin.data.playback.PlaybackSession
import com.eygraber.jellyfin.sdk.core.model.MediaSourceInfo
import com.eygraber.jellyfin.sdk.core.model.MediaStream
import com.eygraber.jellyfin.sdk.core.model.PlaybackInfoResponse
import com.eygraber.jellyfin.sdk.core.model.PlaybackProgressInfo
import com.eygraber.jellyfin.sdk.core.model.PlaybackStartInfo
import com.eygraber.jellyfin.sdk.core.model.PlaybackStopInfo
import com.eygraber.jellyfin.services.sdk.JellyfinPlaybackService
import dev.zacsweers.metro.Inject

/**
 * Remote data source for playback operations.
 *
 * Wraps [JellyfinPlaybackService] and maps SDK models to domain entities.
 * Handles stream selection logic for choosing the best playback method.
 */
@Inject
class PlaybackRemoteDataSource(
  private val playbackService: JellyfinPlaybackService,
) {
  /**
   * Gets playback session info including the best media source and stream URL.
   */
  suspend fun getPlaybackSession(itemId: String): JellyfinResult<PlaybackSession> {
    val result = playbackService.getPlaybackInfo(itemId = itemId)

    return when(result) {
      is JellyfinResult.Success -> {
        val response = result.value
        val selectedSource = selectBestMediaSource(response)
          ?: return JellyfinResult.Error(
            message = "No playable media source found",
            isEphemeral = false,
          )

        val playMethod = determinePlayMethod(selectedSource)
        val streamUrl = resolveStreamUrl(
          itemId = itemId,
          source = selectedSource,
          playMethod = playMethod,
        )

        JellyfinResult.Success(
          PlaybackSession(
            itemId = itemId,
            playSessionId = response.playSessionId.orEmpty(),
            mediaSource = selectedSource.toPlaybackMediaSource(),
            streamUrl = streamUrl,
            playMethod = playMethod,
          ),
        )
      }

      is JellyfinResult.Error -> result
    }
  }

  /**
   * Reports playback start to the server.
   */
  suspend fun reportStart(session: PlaybackSession): JellyfinResult<Unit> =
    playbackService.reportPlaybackStart(
      info = PlaybackStartInfo(
        itemId = session.itemId,
        mediaSourceId = session.mediaSource.id,
        playSessionId = session.playSessionId,
        playMethod = session.playMethod.toApiValue(),
        canSeek = true,
      ),
    )

  /**
   * Reports playback progress to the server.
   */
  suspend fun reportProgress(
    session: PlaybackSession,
    positionTicks: Long,
    isPaused: Boolean,
  ): JellyfinResult<Unit> =
    playbackService.reportPlaybackProgress(
      info = PlaybackProgressInfo(
        itemId = session.itemId,
        mediaSourceId = session.mediaSource.id,
        playSessionId = session.playSessionId,
        positionTicks = positionTicks,
        isPaused = isPaused,
        playMethod = session.playMethod.toApiValue(),
        canSeek = true,
      ),
    )

  /**
   * Reports playback stopped to the server.
   */
  suspend fun reportStopped(
    session: PlaybackSession,
    positionTicks: Long?,
  ): JellyfinResult<Unit> =
    playbackService.reportPlaybackStopped(
      info = PlaybackStopInfo(
        itemId = session.itemId,
        mediaSourceId = session.mediaSource.id,
        playSessionId = session.playSessionId,
        positionTicks = positionTicks,
      ),
    )

  /**
   * Marks an item as played.
   */
  suspend fun markPlayed(itemId: String): JellyfinResult<Unit> =
    playbackService.markPlayed(itemId = itemId)

  /**
   * Marks an item as unplayed.
   */
  suspend fun markUnplayed(itemId: String): JellyfinResult<Unit> =
    playbackService.markUnplayed(itemId = itemId)

  /**
   * Selects the best media source from the playback info response.
   *
   * Priority:
   * 1. Direct play capable sources (no server processing needed)
   * 2. Direct stream capable sources (remuxing only)
   * 3. Transcode capable sources (full transcoding)
   */
  private fun selectBestMediaSource(
    response: PlaybackInfoResponse,
  ): MediaSourceInfo? {
    val sources = response.mediaSources
    if(sources.isEmpty()) return null

    // Prefer direct play, then direct stream, then transcode
    return sources.firstOrNull { it.supportsDirectPlay }
      ?: sources.firstOrNull { it.supportsDirectStream }
      ?: sources.firstOrNull { it.supportsTranscoding }
      ?: sources.first()
  }

  /**
   * Determines the best play method for a given media source.
   */
  private fun determinePlayMethod(source: MediaSourceInfo): PlayMethod = when {
    source.supportsDirectPlay -> PlayMethod.DirectPlay
    source.supportsDirectStream -> PlayMethod.DirectStream
    source.supportsTranscoding -> PlayMethod.Transcode
    else -> PlayMethod.DirectPlay // Fallback to attempt direct play
  }

  /**
   * Resolves the stream URL based on the selected play method.
   */
  private fun resolveStreamUrl(
    itemId: String,
    source: MediaSourceInfo,
    playMethod: PlayMethod,
  ): String = when(playMethod) {
    PlayMethod.Transcode ->
      source.transcodingUrl ?: playbackService.getVideoStreamUrl(
        itemId = itemId,
        mediaSourceId = source.id,
      )

    PlayMethod.DirectStream ->
      source.directStreamUrl ?: playbackService.getVideoStreamUrl(
        itemId = itemId,
        mediaSourceId = source.id,
        container = source.container,
      )

    PlayMethod.DirectPlay ->
      playbackService.getVideoStreamUrl(
        itemId = itemId,
        mediaSourceId = source.id,
        container = source.container,
      )
  }
}

private fun MediaSourceInfo.toPlaybackMediaSource() = PlaybackMediaSource(
  id = id.orEmpty(),
  name = name,
  container = container,
  bitrate = bitrate,
  runtimeTicks = runTimeTicks,
  canDirectPlay = supportsDirectPlay,
  canDirectStream = supportsDirectStream,
  canTranscode = supportsTranscoding,
  transcodingUrl = transcodingUrl,
  videoStreams = mediaStreams
    .filter { it.type == "Video" }
    .map { it.toPlaybackMediaStream() },
  audioStreams = mediaStreams
    .filter { it.type == "Audio" }
    .map { it.toPlaybackMediaStream() },
  subtitleStreams = mediaStreams
    .filter { it.type == "Subtitle" }
    .map { it.toPlaybackMediaStream() },
)

private fun MediaStream.toPlaybackMediaStream() = PlaybackMediaStream(
  index = index ?: 0,
  codec = codec,
  displayTitle = displayTitle,
  language = language,
  isDefault = isDefault,
  isExternal = isExternal,
  isForced = isForced,
  width = width,
  height = height,
  bitRate = bitRate,
  channels = channels,
  deliveryUrl = deliveryUrl,
)

private fun PlayMethod.toApiValue(): String = when(this) {
  PlayMethod.DirectPlay -> "DirectPlay"
  PlayMethod.DirectStream -> "DirectStream"
  PlayMethod.Transcode -> "Transcode"
}
