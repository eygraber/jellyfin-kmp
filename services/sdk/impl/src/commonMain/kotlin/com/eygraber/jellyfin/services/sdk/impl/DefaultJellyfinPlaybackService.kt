package com.eygraber.jellyfin.services.sdk.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.common.mapToUnit
import com.eygraber.jellyfin.sdk.core.JellyfinSdk
import com.eygraber.jellyfin.sdk.core.api.media.mediaApi
import com.eygraber.jellyfin.sdk.core.model.PlaybackInfoResponse
import com.eygraber.jellyfin.sdk.core.model.PlaybackProgressInfo
import com.eygraber.jellyfin.sdk.core.model.PlaybackStartInfo
import com.eygraber.jellyfin.sdk.core.model.PlaybackStopInfo
import com.eygraber.jellyfin.services.logging.JellyfinLogger
import com.eygraber.jellyfin.services.sdk.JellyfinPlaybackService
import com.eygraber.jellyfin.services.sdk.JellyfinSessionManager
import com.eygraber.jellyfin.services.sdk.toJellyfinResult
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

/**
 * Default implementation of [JellyfinPlaybackService].
 *
 * Uses the [JellyfinSdk] to manage playback sessions with the Jellyfin server.
 * Requires an active authenticated session with a valid user ID.
 */
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultJellyfinPlaybackService(
  private val sdk: JellyfinSdk,
  private val sessionManager: JellyfinSessionManager,
  private val logger: JellyfinLogger,
) : JellyfinPlaybackService {
  override suspend fun getPlaybackInfo(
    itemId: String,
  ): JellyfinResult<PlaybackInfoResponse> {
    val serverInfo = sessionManager.currentServer.value
      ?: return JellyfinResult.Error(
        message = "Not connected to a server",
        isEphemeral = false,
      )

    val userId = serverInfo.userId
      ?: return JellyfinResult.Error(
        message = "Not authenticated",
        isEphemeral = false,
      )

    logger.debug(tag = TAG, message = "Getting playback info for item: $itemId")

    val apiClient = sdk.createApiClient(serverInfo = serverInfo)
    return try {
      apiClient.mediaApi.getPlaybackInfo(
        itemId = itemId,
        userId = userId,
      ).toJellyfinResult()
    }
    finally {
      apiClient.close()
    }
  }

  @Suppress("LongParameterList")
  override fun getVideoStreamUrl(
    itemId: String,
    mediaSourceId: String?,
    container: String?,
    audioCodec: String?,
    videoCodec: String?,
    maxWidth: Int?,
    maxHeight: Int?,
  ): String {
    val serverInfo = sessionManager.currentServer.value ?: return ""

    val apiClient = sdk.createApiClient(serverInfo = serverInfo)
    return try {
      apiClient.mediaApi.getStreamUrl(
        itemId = itemId,
        mediaSourceId = mediaSourceId,
        container = container,
        audioCodec = audioCodec,
        videoCodec = videoCodec,
        maxWidth = maxWidth,
        maxHeight = maxHeight,
      )
    }
    finally {
      apiClient.close()
    }
  }

  override fun getAudioStreamUrl(
    itemId: String,
    mediaSourceId: String?,
    container: String?,
    maxBitrate: Int?,
  ): String {
    val serverInfo = sessionManager.currentServer.value ?: return ""

    val apiClient = sdk.createApiClient(serverInfo = serverInfo)
    return try {
      apiClient.mediaApi.getAudioStreamUrl(
        itemId = itemId,
        mediaSourceId = mediaSourceId,
        container = container,
        maxBitrate = maxBitrate,
      )
    }
    finally {
      apiClient.close()
    }
  }

  override suspend fun reportPlaybackStart(
    info: PlaybackStartInfo,
  ): JellyfinResult<Unit> {
    logger.debug(tag = TAG, message = "Reporting playback start for item: ${info.itemId}")

    val serverInfo = sessionManager.currentServer.value
      ?: return JellyfinResult.Error(
        message = "Not connected to a server",
        isEphemeral = false,
      )

    val apiClient = sdk.createApiClient(serverInfo = serverInfo)
    return try {
      apiClient.mediaApi.reportPlaybackStart(info = info).toJellyfinResult()
    }
    finally {
      apiClient.close()
    }
  }

  override suspend fun reportPlaybackProgress(
    info: PlaybackProgressInfo,
  ): JellyfinResult<Unit> {
    val serverInfo = sessionManager.currentServer.value
      ?: return JellyfinResult.Error(
        message = "Not connected to a server",
        isEphemeral = false,
      )

    val apiClient = sdk.createApiClient(serverInfo = serverInfo)
    return try {
      apiClient.mediaApi.reportPlaybackProgress(info = info).toJellyfinResult()
    }
    finally {
      apiClient.close()
    }
  }

  override suspend fun reportPlaybackStopped(
    info: PlaybackStopInfo,
  ): JellyfinResult<Unit> {
    logger.debug(tag = TAG, message = "Reporting playback stopped for item: ${info.itemId}")

    val serverInfo = sessionManager.currentServer.value
      ?: return JellyfinResult.Error(
        message = "Not connected to a server",
        isEphemeral = false,
      )

    val apiClient = sdk.createApiClient(serverInfo = serverInfo)
    return try {
      apiClient.mediaApi.reportPlaybackStopped(info = info).toJellyfinResult()
    }
    finally {
      apiClient.close()
    }
  }

  override suspend fun markPlayed(itemId: String): JellyfinResult<Unit> {
    val serverInfo = sessionManager.currentServer.value
      ?: return JellyfinResult.Error(
        message = "Not connected to a server",
        isEphemeral = false,
      )

    val userId = serverInfo.userId
      ?: return JellyfinResult.Error(
        message = "Not authenticated",
        isEphemeral = false,
      )

    logger.debug(tag = TAG, message = "Marking item $itemId as played")

    val apiClient = sdk.createApiClient(serverInfo = serverInfo)
    return try {
      apiClient.mediaApi.markPlayed(
        userId = userId,
        itemId = itemId,
      ).toJellyfinResult().mapToUnit()
    }
    finally {
      apiClient.close()
    }
  }

  override suspend fun markUnplayed(itemId: String): JellyfinResult<Unit> {
    val serverInfo = sessionManager.currentServer.value
      ?: return JellyfinResult.Error(
        message = "Not connected to a server",
        isEphemeral = false,
      )

    val userId = serverInfo.userId
      ?: return JellyfinResult.Error(
        message = "Not authenticated",
        isEphemeral = false,
      )

    logger.debug(tag = TAG, message = "Marking item $itemId as unplayed")

    val apiClient = sdk.createApiClient(serverInfo = serverInfo)
    return try {
      apiClient.mediaApi.markUnplayed(
        userId = userId,
        itemId = itemId,
      ).toJellyfinResult().mapToUnit()
    }
    finally {
      apiClient.close()
    }
  }

  companion object {
    private const val TAG = "JellyfinPlaybackService"
  }
}
