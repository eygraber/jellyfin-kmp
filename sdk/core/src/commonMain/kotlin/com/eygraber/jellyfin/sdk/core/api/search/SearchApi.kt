package com.eygraber.jellyfin.sdk.core.api.search

import com.eygraber.jellyfin.sdk.core.SdkResult
import com.eygraber.jellyfin.sdk.core.api.BaseApi
import com.eygraber.jellyfin.sdk.core.api.JellyfinApiClient
import com.eygraber.jellyfin.sdk.core.model.SearchHintResult

class SearchApi(
  apiClient: JellyfinApiClient,
) : BaseApi(apiClient) {
  /**
   * Gets search hints based on a search term.
   * Provides fast typeahead results across the library.
   *
   * @param searchTerm The search term to use.
   * @param limit Optional maximum number of results to return.
   * @param includeItemTypes Optional list of item types to include (e.g., "Movie", "Series").
   * @param includeMedia Whether to include media items. Defaults to true.
   * @param includePeople Whether to include people. Defaults to true.
   * @param includeStudios Whether to include studios. Defaults to true.
   * @param includeArtists Whether to include artists. Defaults to true.
   * @param includeGenres Whether to include genres. Defaults to true.
   */
  @Suppress("LongParameterList")
  suspend fun getSearchHints(
    searchTerm: String,
    limit: Int? = null,
    includeItemTypes: List<String>? = null,
    includeMedia: Boolean? = null,
    includePeople: Boolean? = null,
    includeStudios: Boolean? = null,
    includeArtists: Boolean? = null,
    includeGenres: Boolean? = null,
  ): SdkResult<SearchHintResult> = get(
    path = "Search/Hints",
    queryParams = buildMap {
      put(key = "searchTerm", value = searchTerm)
      limit?.let { put(key = "limit", value = it) }
      includeItemTypes?.let { put(key = "includeItemTypes", value = it.joinToString(",")) }
      includeMedia?.let { put(key = "includeMedia", value = it) }
      includePeople?.let { put(key = "includePeople", value = it) }
      includeStudios?.let { put(key = "includeStudios", value = it) }
      includeArtists?.let { put(key = "includeArtists", value = it) }
      includeGenres?.let { put(key = "includeGenres", value = it) }
    },
  )
}
