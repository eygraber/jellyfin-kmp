package com.eygraber.jellyfin.services.sdk

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.sdk.core.model.BaseItemDto
import com.eygraber.jellyfin.sdk.core.model.ImageType
import com.eygraber.jellyfin.sdk.core.model.ItemsResult

/**
 * Service for accessing the Jellyfin media library.
 *
 * Provides operations to browse, search, and retrieve media items
 * from the connected Jellyfin server.
 */
interface JellyfinLibraryService {
  /**
   * Gets items that the user has started but not finished watching.
   *
   * Results are sorted by last played date (most recent first) by the server.
   *
   * @param limit Maximum number of items to return.
   * @param mediaTypes Filter by media types (e.g., "Video").
   * @param fields Additional fields to include in the response.
   * @return A [JellyfinResult] containing the [ItemsResult].
   */
  suspend fun getResumeItems(
    limit: Int? = null,
    mediaTypes: List<String>? = null,
    fields: List<String>? = null,
  ): JellyfinResult<ItemsResult>

  /**
   * Gets the latest items added to the library.
   *
   * @param parentId Filter by parent item ID (e.g., library ID).
   * @param includeItemTypes Filter by item types.
   * @param limit Maximum number of items to return.
   * @param fields Additional fields to include in the response.
   * @return A [JellyfinResult] containing a list of [BaseItemDto].
   */
  suspend fun getLatestItems(
    parentId: String? = null,
    includeItemTypes: List<String>? = null,
    limit: Int? = null,
    fields: List<String>? = null,
  ): JellyfinResult<List<BaseItemDto>>

  /**
   * Generates the URL for an item image.
   *
   * @param itemId The item ID.
   * @param imageType The type of image.
   * @param maxWidth Maximum width in pixels.
   * @param maxHeight Maximum height in pixels.
   * @param tag The image tag for cache busting.
   * @param imageIndex The image index (for backdrop images).
   * @return The fully qualified image URL.
   */
  fun getImageUrl(
    itemId: String,
    imageType: ImageType = ImageType.Primary,
    maxWidth: Int? = null,
    maxHeight: Int? = null,
    tag: String? = null,
    imageIndex: Int? = null,
  ): String
}
