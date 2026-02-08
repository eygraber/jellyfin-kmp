package com.eygraber.jellyfin.data.search.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.common.mapSuccessTo
import com.eygraber.jellyfin.data.search.SearchResultItem
import com.eygraber.jellyfin.sdk.core.model.BaseItemDto
import com.eygraber.jellyfin.services.sdk.JellyfinLibraryService
import dev.zacsweers.metro.Inject

@Inject
class SearchRemoteDataSource(
  private val libraryService: JellyfinLibraryService,
) {
  suspend fun search(
    query: String,
    includeItemTypes: List<String>?,
    limit: Int,
  ): JellyfinResult<List<SearchResultItem>> =
    libraryService.getItems(
      searchTerm = query,
      includeItemTypes = includeItemTypes,
      limit = limit,
      recursive = true,
    ).mapSuccessTo {
      items.mapNotNull { dto -> dto.toSearchResultItem() }
    }
}

private fun BaseItemDto.toSearchResultItem(): SearchResultItem? {
  val itemId = id ?: return null

  return SearchResultItem(
    id = itemId,
    name = name.orEmpty(),
    type = type.orEmpty(),
    productionYear = productionYear,
    primaryImageTag = imageTags["Primary"],
    seriesName = seriesName,
    overview = overview,
  )
}
