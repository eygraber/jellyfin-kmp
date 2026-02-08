package com.eygraber.jellyfin.data.search.history.impl

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.eygraber.jellyfin.data.search.history.SearchHistoryEntry
import com.eygraber.jellyfin.services.database.impl.JellyfinDatabase
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Local data source for search history persistence using SQLDelight.
 *
 * Handles all direct database interactions for search history management.
 */
@Inject
class SearchHistoryLocalDataSource(
  private val database: JellyfinDatabase,
  private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
  fun observeRecent(limit: Int): Flow<List<SearchHistoryEntry>> =
    database.searchHistoryQueries.selectRecent(value_ = limit.toLong())
      .asFlow()
      .mapToList(dispatcher)
      .map { rows -> rows.map { it.toEntry() } }

  suspend fun upsert(query: String, timestamp: Long): Unit = withContext(dispatcher) {
    database.searchHistoryQueries.upsert(
      query = query,
      searched_at = timestamp,
    )
  }

  suspend fun deleteByQuery(query: String): Unit = withContext(dispatcher) {
    database.searchHistoryQueries.deleteByQuery(query = query)
  }

  suspend fun deleteAll(): Unit = withContext(dispatcher) {
    database.searchHistoryQueries.deleteAll()
  }

  suspend fun pruneOldEntries(keepCount: Int): Unit = withContext(dispatcher) {
    database.searchHistoryQueries.deleteOldest(keepCount.toLong())
  }
}

/**
 * Maps a SQLDelight-generated `Search_history` row to a [SearchHistoryEntry].
 */
private fun migrations.Search_history.toEntry() = SearchHistoryEntry(
  query = query,
  searchedAt = searched_at,
)
