package com.eygraber.jellyfin.data.search.history.impl

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.common.runResult
import com.eygraber.jellyfin.data.search.history.SearchHistoryEntry
import com.eygraber.jellyfin.data.search.history.SearchHistoryRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.Flow

/**
 * Default implementation of [SearchHistoryRepository] backed by SQLDelight.
 *
 * Coordinates with [SearchHistoryLocalDataSource] for persistence.
 * The repository is stateless and not scoped as a singleton.
 */
@ContributesBinding(AppScope::class)
class DefaultSearchHistoryRepository(
  private val localDataSource: SearchHistoryLocalDataSource,
  private val clock: () -> Long = { currentTimeMillis() },
) : SearchHistoryRepository {
  override fun observeRecentSearches(limit: Int): Flow<List<SearchHistoryEntry>> =
    localDataSource.observeRecent(limit = limit)

  override suspend fun saveSearch(query: String): JellyfinResult<Unit> = runResult {
    localDataSource.upsert(
      query = query,
      timestamp = clock(),
    )
    localDataSource.pruneOldEntries(keepCount = SearchHistoryRepository.DEFAULT_HISTORY_LIMIT)
  }

  override suspend fun deleteSearch(query: String): JellyfinResult<Unit> = runResult {
    localDataSource.deleteByQuery(query = query)
  }

  override suspend fun clearHistory(): JellyfinResult<Unit> = runResult {
    localDataSource.deleteAll()
  }
}
