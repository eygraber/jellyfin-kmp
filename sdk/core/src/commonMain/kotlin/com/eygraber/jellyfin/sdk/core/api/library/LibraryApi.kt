package com.eygraber.jellyfin.sdk.core.api.library

import com.eygraber.jellyfin.sdk.core.SdkResult
import com.eygraber.jellyfin.sdk.core.api.BaseApi
import com.eygraber.jellyfin.sdk.core.api.JellyfinApiClient
import com.eygraber.jellyfin.sdk.core.model.BaseItemDto
import com.eygraber.jellyfin.sdk.core.model.ImageType
import com.eygraber.jellyfin.sdk.core.model.ItemsResult

class LibraryApi(
  apiClient: JellyfinApiClient,
) : BaseApi(apiClient) {
  /**
   * Gets items from the library with optional filters.
   *
   * @param userId The user ID for personalized results.
   * @param parentId Filter by parent item ID (e.g., library ID).
   * @param includeItemTypes Filter by item types (e.g., "Movie", "Series").
   * @param sortBy Sort criteria (e.g., "SortName", "DateCreated").
   * @param sortOrder Sort direction ("Ascending" or "Descending").
   * @param startIndex Pagination start index.
   * @param limit Maximum number of results.
   * @param recursive Whether to search recursively.
   * @param genres Filter by genre names.
   * @param years Filter by production years.
   * @param personIds Filter by person IDs.
   * @param studioIds Filter by studio IDs.
   * @param searchTerm Search term to filter results.
   * @param fields Additional fields to include in the response.
   */
  @Suppress("LongParameterList")
  suspend fun getItems(
    userId: String? = null,
    parentId: String? = null,
    includeItemTypes: List<String>? = null,
    sortBy: List<String>? = null,
    sortOrder: String? = null,
    startIndex: Int? = null,
    limit: Int? = null,
    recursive: Boolean? = null,
    genres: List<String>? = null,
    years: List<Int>? = null,
    personIds: List<String>? = null,
    studioIds: List<String>? = null,
    searchTerm: String? = null,
    fields: List<String>? = null,
  ): SdkResult<ItemsResult> = get(
    path = "Items",
    queryParams = buildItemsQueryParams(
      userId = userId,
      parentId = parentId,
      includeItemTypes = includeItemTypes,
      sortBy = sortBy,
      sortOrder = sortOrder,
      startIndex = startIndex,
      limit = limit,
      recursive = recursive,
      genres = genres,
      years = years,
      personIds = personIds,
      studioIds = studioIds,
      searchTerm = searchTerm,
      fields = fields,
    ),
  )

  /**
   * Gets an item by its ID.
   */
  suspend fun getItem(
    userId: String,
    itemId: String,
  ): SdkResult<BaseItemDto> = get(
    path = "Users/$userId/Items/$itemId",
  )

  /**
   * Gets the user's library views (e.g., Movies, TV Shows, Music).
   */
  suspend fun getUserViews(userId: String): SdkResult<ItemsResult> = get(
    path = "Users/$userId/Views",
  )

  /**
   * Gets items similar to the specified item.
   */
  suspend fun getSimilarItems(
    itemId: String,
    userId: String? = null,
    limit: Int? = null,
  ): SdkResult<ItemsResult> = get(
    path = "Items/$itemId/Similar",
    queryParams = mapOf(
      "userId" to userId,
      "limit" to limit,
    ),
  )

  /**
   * Gets the latest items added to the library.
   */
  suspend fun getLatestItems(
    userId: String,
    parentId: String? = null,
    includeItemTypes: List<String>? = null,
    limit: Int? = null,
    fields: List<String>? = null,
  ): SdkResult<List<BaseItemDto>> = get(
    path = "Users/$userId/Items/Latest",
    queryParams = mapOf(
      "parentId" to parentId,
      "includeItemTypes" to includeItemTypes?.joinToString(","),
      "limit" to limit,
      "fields" to fields?.joinToString(","),
    ),
  )

  /**
   * Gets items that the user has started but not finished.
   */
  suspend fun getResumeItems(
    userId: String,
    limit: Int? = null,
    includeItemTypes: List<String>? = null,
    fields: List<String>? = null,
  ): SdkResult<ItemsResult> = get(
    path = "Users/$userId/Items/Resume",
    queryParams = mapOf(
      "limit" to limit,
      "includeItemTypes" to includeItemTypes?.joinToString(","),
      "fields" to fields?.joinToString(","),
    ),
  )

  /**
   * Gets the next episodes that the user should watch for their in-progress series.
   *
   * @param userId The user ID.
   * @param limit Maximum number of items to return.
   * @param fields Additional fields to include in the response.
   */
  suspend fun getNextUpEpisodes(
    userId: String,
    limit: Int? = null,
    fields: List<String>? = null,
  ): SdkResult<ItemsResult> = get(
    path = "Shows/NextUp",
    queryParams = mapOf(
      "userId" to userId,
      "limit" to limit,
      "fields" to fields?.joinToString(","),
    ),
  )

  /**
   * Generates the URL for an item image.
   *
   * @param itemId The item ID.
   * @param imageType The type of image.
   * @param maxWidth Maximum width in pixels.
   * @param maxHeight Maximum height in pixels.
   * @param tag The image tag for cache busting.
   * @param imageIndex The image index (for backdrop images).
   */
  fun getImageUrl(
    itemId: String,
    imageType: ImageType = ImageType.Primary,
    maxWidth: Int? = null,
    maxHeight: Int? = null,
    tag: String? = null,
    imageIndex: Int? = null,
  ): String = buildString {
    append(apiClient.serverInfo.baseUrl.trimEnd('/'))
    append("/Items/$itemId/Images/${imageType.apiValue}")
    imageIndex?.let { append("/$it") }

    val params = mutableListOf<String>()
    maxWidth?.let { params.add("maxWidth=$it") }
    maxHeight?.let { params.add("maxHeight=$it") }
    tag?.let { params.add("tag=$it") }

    if(params.isNotEmpty()) {
      append("?")
      append(params.joinToString("&"))
    }
  }

  @Suppress("LongParameterList")
  private fun buildItemsQueryParams(
    userId: String?,
    parentId: String?,
    includeItemTypes: List<String>?,
    sortBy: List<String>?,
    sortOrder: String?,
    startIndex: Int?,
    limit: Int?,
    recursive: Boolean?,
    genres: List<String>?,
    years: List<Int>?,
    personIds: List<String>?,
    studioIds: List<String>?,
    searchTerm: String?,
    fields: List<String>?,
  ): Map<String, Any?> = mapOf(
    "userId" to userId,
    "parentId" to parentId,
    "includeItemTypes" to includeItemTypes?.joinToString(","),
    "sortBy" to sortBy?.joinToString(","),
    "sortOrder" to sortOrder,
    "startIndex" to startIndex,
    "limit" to limit,
    "recursive" to recursive,
    "genres" to genres?.joinToString("|"),
    "years" to years?.joinToString(","),
    "personIds" to personIds?.joinToString(","),
    "studioIds" to studioIds?.joinToString(","),
    "searchTerm" to searchTerm,
    "fields" to fields?.joinToString(","),
  )
}
