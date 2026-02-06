package com.eygraber.jellyfin.data.items.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.data.items.ItemSortBy
import com.eygraber.jellyfin.data.items.ItemsRepository
import com.eygraber.jellyfin.data.items.LibraryItem
import com.eygraber.jellyfin.data.items.PaginatedResult
import com.eygraber.jellyfin.data.items.SortOrder
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

/**
 * Default implementation of [ItemsRepository].
 *
 * Delegates to [ItemsRemoteDataSource] for network operations.
 * This repository is stateless per project conventions; caching and
 * state management are handled at the screen/compositor level.
 */
@ContributesBinding(AppScope::class)
class DefaultItemsRepository(
  private val remoteDataSource: ItemsRemoteDataSource,
) : ItemsRepository {
  @Suppress("LongParameterList")
  override suspend fun getItems(
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
    remoteDataSource.getItems(
      parentId = parentId,
      includeItemTypes = includeItemTypes,
      sortBy = sortBy,
      sortOrder = sortOrder,
      startIndex = startIndex,
      limit = limit,
      genres = genres,
      years = years,
      searchTerm = searchTerm,
      fields = fields,
    )

  override suspend fun getItem(itemId: String): JellyfinResult<LibraryItem> =
    remoteDataSource.getItem(itemId = itemId)

  override suspend fun getSimilarItems(
    itemId: String,
    limit: Int?,
  ): JellyfinResult<List<LibraryItem>> =
    remoteDataSource.getSimilarItems(
      itemId = itemId,
      limit = limit,
    )
}
