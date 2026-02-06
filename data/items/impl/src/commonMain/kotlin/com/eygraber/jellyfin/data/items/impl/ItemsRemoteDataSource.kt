package com.eygraber.jellyfin.data.items.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.common.mapSuccessTo
import com.eygraber.jellyfin.data.items.ItemSortBy
import com.eygraber.jellyfin.data.items.LibraryItem
import com.eygraber.jellyfin.data.items.PaginatedResult
import com.eygraber.jellyfin.data.items.SortOrder
import com.eygraber.jellyfin.sdk.core.model.BaseItemDto
import com.eygraber.jellyfin.services.sdk.JellyfinLibraryService
import dev.zacsweers.metro.Inject

@Inject
class ItemsRemoteDataSource(
  private val libraryService: JellyfinLibraryService,
) {
  @Suppress("LongParameterList")
  suspend fun getItems(
    parentId: String,
    includeItemTypes: List<String>?,
    sortBy: ItemSortBy,
    sortOrder: SortOrder,
    startIndex: Int,
    limit: Int,
    genres: List<String>?,
    years: List<Int>?,
    searchTerm: String?,
    fields: List<String>?,
  ): JellyfinResult<PaginatedResult<LibraryItem>> =
    libraryService.getItems(
      parentId = parentId,
      includeItemTypes = includeItemTypes,
      sortBy = listOf(sortBy.apiValue),
      sortOrder = sortOrder.apiValue,
      startIndex = startIndex,
      limit = limit,
      recursive = true,
      genres = genres,
      years = years,
      searchTerm = searchTerm,
      fields = fields,
    ).mapSuccessTo {
      PaginatedResult(
        items = items.mapNotNull { dto -> dto.toLibraryItem() },
        totalRecordCount = totalRecordCount,
        startIndex = startIndex,
      )
    }

  suspend fun getItem(
    itemId: String,
  ): JellyfinResult<LibraryItem> =
    libraryService.getItem(itemId = itemId).mapSuccessTo {
      toLibraryItem() ?: error("Item $itemId has no ID")
    }

  suspend fun getSimilarItems(
    itemId: String,
    limit: Int?,
  ): JellyfinResult<List<LibraryItem>> =
    libraryService.getSimilarItems(
      itemId = itemId,
      limit = limit,
    ).mapSuccessTo {
      items.mapNotNull { dto -> dto.toLibraryItem() }
    }
}

private fun BaseItemDto.toLibraryItem(): LibraryItem? {
  val itemId = id ?: return null

  return LibraryItem(
    id = itemId,
    name = name.orEmpty(),
    sortName = sortName,
    type = type.orEmpty(),
    overview = overview,
    productionYear = productionYear,
    communityRating = communityRating,
    officialRating = officialRating,
    primaryImageTag = imageTags["Primary"],
    backdropImageTags = backdropImageTags,
    seriesName = seriesName,
    seriesId = seriesId,
    childCount = childCount,
    runTimeTicks = runTimeTicks,
  )
}
