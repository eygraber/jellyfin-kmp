package com.eygraber.jellyfin.sdk.core.api.media

import com.eygraber.jellyfin.sdk.core.SdkResult
import com.eygraber.jellyfin.sdk.core.api.BaseApi
import com.eygraber.jellyfin.sdk.core.api.JellyfinApiClient
import com.eygraber.jellyfin.sdk.core.model.PlaybackInfoResponse
import com.eygraber.jellyfin.sdk.core.model.PlaybackProgressInfo
import com.eygraber.jellyfin.sdk.core.model.PlaybackStartInfo
import com.eygraber.jellyfin.sdk.core.model.PlaybackStopInfo
import com.eygraber.jellyfin.sdk.core.model.UserItemDataDto

class MediaApi(
  apiClient: JellyfinApiClient,
) : BaseApi(apiClient) {
  /**
   * Gets playback info for an item, including available media sources
   * and transcoding options.
   */
  suspend fun getPlaybackInfo(
    itemId: String,
    userId: String,
  ): SdkResult<PlaybackInfoResponse> = get(
    path = "Items/$itemId/PlaybackInfo",
    queryParams = mapOf("userId" to userId),
  )

  /**
   * Reports that playback has started.
   */
  suspend fun reportPlaybackStart(
    info: PlaybackStartInfo,
  ): SdkResult<Unit> = post(
    path = "Sessions/Playing",
    body = info,
  )

  /**
   * Reports playback progress.
   */
  suspend fun reportPlaybackProgress(
    info: PlaybackProgressInfo,
  ): SdkResult<Unit> = post(
    path = "Sessions/Playing/Progress",
    body = info,
  )

  /**
   * Reports that playback has stopped.
   */
  suspend fun reportPlaybackStopped(
    info: PlaybackStopInfo,
  ): SdkResult<Unit> = post(
    path = "Sessions/Playing/Stopped",
    body = info,
  )

  /**
   * Marks an item as played for the user.
   */
  suspend fun markPlayed(
    userId: String,
    itemId: String,
  ): SdkResult<UserItemDataDto> = post<UserItemDataDto, Unit>(
    path = "Users/$userId/PlayedItems/$itemId",
  )

  /**
   * Marks an item as unplayed for the user.
   */
  suspend fun markUnplayed(
    userId: String,
    itemId: String,
  ): SdkResult<UserItemDataDto> = delete(
    path = "Users/$userId/PlayedItems/$itemId",
  )

  /**
   * Generates a direct stream URL for the specified item and media source.
   *
   * @param itemId The item ID.
   * @param mediaSourceId The media source ID.
   * @param container The container format (e.g., "mp4", "mkv").
   * @param audioCodec The preferred audio codec.
   * @param videoCodec The preferred video codec.
   * @param maxAudioBitrate Maximum audio bitrate.
   * @param maxVideoBitrate Maximum video bitrate.
   * @param maxWidth Maximum video width.
   * @param maxHeight Maximum video height.
   */
  @Suppress("LongParameterList")
  fun getStreamUrl(
    itemId: String,
    mediaSourceId: String? = null,
    container: String? = null,
    audioCodec: String? = null,
    videoCodec: String? = null,
    maxAudioBitrate: Int? = null,
    maxVideoBitrate: Int? = null,
    maxWidth: Int? = null,
    maxHeight: Int? = null,
  ): String = buildString {
    append(apiClient.serverInfo.baseUrl.trimEnd('/'))
    append("/Videos/$itemId/stream")
    container?.let { append(".$it") }

    val params = mutableListOf<String>()
    params.add("static=true")
    mediaSourceId?.let { params.add("mediaSourceId=$it") }
    audioCodec?.let { params.add("audioCodec=$it") }
    videoCodec?.let { params.add("videoCodec=$it") }
    maxAudioBitrate?.let { params.add("maxStreamingBitrate=$it") }
    maxVideoBitrate?.let { params.add("videoBitRate=$it") }
    maxWidth?.let { params.add("maxWidth=$it") }
    maxHeight?.let { params.add("maxHeight=$it") }
    apiClient.serverInfo.accessToken?.let { params.add("api_key=$it") }

    append("?")
    append(params.joinToString("&"))
  }

  /**
   * Generates an audio stream URL for the specified item.
   */
  fun getAudioStreamUrl(
    itemId: String,
    mediaSourceId: String? = null,
    container: String? = null,
    maxBitrate: Int? = null,
  ): String = buildString {
    append(apiClient.serverInfo.baseUrl.trimEnd('/'))
    append("/Audio/$itemId/universal")

    val params = mutableListOf<String>()
    mediaSourceId?.let { params.add("mediaSourceId=$it") }
    container?.let { params.add("container=$it") }
    maxBitrate?.let { params.add("maxStreamingBitrate=$it") }
    apiClient.serverInfo.accessToken?.let { params.add("api_key=$it") }
    apiClient.serverInfo.userId?.let { params.add("userId=$it") }

    if(params.isNotEmpty()) {
      append("?")
      append(params.joinToString("&"))
    }
  }
}
