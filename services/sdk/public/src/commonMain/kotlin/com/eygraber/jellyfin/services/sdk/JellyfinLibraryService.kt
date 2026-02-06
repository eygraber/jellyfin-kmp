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
   * Gets the next episodes the user should watch for their in-progress series.
   *
   * @param limit Maximum number of items to return.
   * @param fields Additional fields to include in the response.
   * @return A [JellyfinResult] containing the [ItemsResult].
   */
  suspend fun getNextUpEpisodes(
    limit: Int? = null,
    fields: List<String>? = null,
  ): JellyfinResult<ItemsResult>

  /**
   * Gets the user's library views (e.g., Movies, TV Shows, Music).
   *
   * @return A [JellyfinResult] containing the [ItemsResult] of library views.
   */
  suspend fun getUserViews(): JellyfinResult<ItemsResult>

  /**
   * Gets items from the library with optional filters and pagination.
   *
   * @param parentId Filter by parent item ID (e.g., library ID).
   * @param includeItemTypes Filter by item types (e.g., "Movie", "Series").
   * @param sortBy Sort criteria (e.g., "SortName", "DateCreated").
   * @param sortOrder Sort direction ("Ascending" or "Descending").
   * @param startIndex Pagination start index.
   * @param limit Maximum number of results.
   * @param recursive Whether to search recursively.
   * @param genres Filter by genre names.
   * @param years Filter by production years.
   * @param searchTerm Search term to filter results.
   * @param fields Additional fields to include in the response.
   * @return A [JellyfinResult] containing the [ItemsResult].
   */
  @Suppress("LongParameterList")
  suspend fun getItems(
    parentId: String? = null,
    includeItemTypes: List<String>? = null,
    sortBy: List<String>? = null,
    sortOrder: String? = null,
    startIndex: Int? = null,
    limit: Int? = null,
    recursive: Boolean? = null,
    genres: List<String>? = null,
    years: List<Int>? = null,
    searchTerm: String? = null,
    fields: List<String>? = null,
  ): JellyfinResult<ItemsResult>

  /**
   * Gets a single item by its ID.
   *
   * @param itemId The item's unique ID.
   * @return A [JellyfinResult] containing the [BaseItemDto].
   */
  suspend fun getItem(itemId: String): JellyfinResult<BaseItemDto>

  /**
   * Gets items similar to the specified item.
   *
   * @param itemId The item to find similar items for.
   * @param limit Maximum number of similar items to return.
   * @return A [JellyfinResult] containing the [ItemsResult].
   */
  suspend fun getSimilarItems(
    itemId: String,
    limit: Int? = null,
  ): JellyfinResult<ItemsResult>

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
