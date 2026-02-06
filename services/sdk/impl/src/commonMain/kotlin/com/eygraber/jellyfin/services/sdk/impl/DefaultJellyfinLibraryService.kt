package com.eygraber.jellyfin.services.sdk.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.sdk.core.JellyfinSdk
import com.eygraber.jellyfin.sdk.core.api.library.libraryApi
import com.eygraber.jellyfin.sdk.core.model.BaseItemDto
import com.eygraber.jellyfin.sdk.core.model.ImageType
import com.eygraber.jellyfin.sdk.core.model.ItemsResult
import com.eygraber.jellyfin.services.logging.JellyfinLogger
import com.eygraber.jellyfin.services.sdk.JellyfinLibraryService
import com.eygraber.jellyfin.services.sdk.JellyfinSessionManager
import com.eygraber.jellyfin.services.sdk.toJellyfinResult
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

/**
 * Default implementation of [JellyfinLibraryService].
 *
 * Uses the [JellyfinSdk] to access the Jellyfin media library.
 * Requires an active authenticated session with a valid user ID.
 */
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultJellyfinLibraryService(
  private val sdk: JellyfinSdk,
  private val sessionManager: JellyfinSessionManager,
  private val logger: JellyfinLogger,
) : JellyfinLibraryService {
  override suspend fun getResumeItems(
    limit: Int?,
    mediaTypes: List<String>?,
    fields: List<String>?,
  ): JellyfinResult<ItemsResult> {
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

    logger.debug(tag = TAG, message = "Fetching resume items for user: $userId")

    val apiClient = sdk.createApiClient(serverInfo = serverInfo)
    return try {
      apiClient.libraryApi.getResumeItems(
        userId = userId,
        limit = limit,
        includeItemTypes = mediaTypes,
        fields = fields,
      ).toJellyfinResult()
    }
    finally {
      apiClient.close()
    }
  }

  override suspend fun getLatestItems(
    parentId: String?,
    includeItemTypes: List<String>?,
    limit: Int?,
    fields: List<String>?,
  ): JellyfinResult<List<BaseItemDto>> {
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

    logger.debug(tag = TAG, message = "Fetching latest items for user: $userId")

    val apiClient = sdk.createApiClient(serverInfo = serverInfo)
    return try {
      apiClient.libraryApi.getLatestItems(
        userId = userId,
        parentId = parentId,
        includeItemTypes = includeItemTypes,
        limit = limit,
        fields = fields,
      ).toJellyfinResult()
    }
    finally {
      apiClient.close()
    }
  }

  override suspend fun getNextUpEpisodes(
    limit: Int?,
    fields: List<String>?,
  ): JellyfinResult<ItemsResult> {
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

    logger.debug(tag = TAG, message = "Fetching next up episodes for user: $userId")

    val apiClient = sdk.createApiClient(serverInfo = serverInfo)
    return try {
      apiClient.libraryApi.getNextUpEpisodes(
        userId = userId,
        limit = limit,
        fields = fields,
      ).toJellyfinResult()
    }
    finally {
      apiClient.close()
    }
  }

  override fun getImageUrl(
    itemId: String,
    imageType: ImageType,
    maxWidth: Int?,
    maxHeight: Int?,
    tag: String?,
    imageIndex: Int?,
  ): String {
    val serverInfo = sessionManager.currentServer.value
      ?: return ""

    val apiClient = sdk.createApiClient(serverInfo = serverInfo)
    return try {
      apiClient.libraryApi.getImageUrl(
        itemId = itemId,
        imageType = imageType,
        maxWidth = maxWidth,
        maxHeight = maxHeight,
        tag = tag,
        imageIndex = imageIndex,
      )
    }
    finally {
      apiClient.close()
    }
  }

  companion object {
    private const val TAG = "JellyfinLibraryService"
  }
}
