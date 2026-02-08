package com.eygraber.jellyfin.data.search.history.impl

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.services.database.impl.JellyfinDatabase
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteDatabaseType
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteDriver
import com.eygraber.sqldelight.androidx.driver.File
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class DefaultSearchHistoryRepositoryTest {
  private lateinit var driver: AndroidxSqliteDriver
  private lateinit var database: JellyfinDatabase
  private lateinit var repository: DefaultSearchHistoryRepository
  private lateinit var tempDir: File
  private var currentTime = 1000L

  @BeforeTest
  fun setUp() {
    currentTime = 1000L

    tempDir = File.createTempFile("jellyfin-search-history-test", null).apply {
      delete()
      mkdirs()
    }

    driver = AndroidxSqliteDriver(
      driver = BundledSQLiteDriver(),
      databaseType = AndroidxSqliteDatabaseType.File(
        file = File(tempDir, "test.db"),
      ),
      schema = JellyfinDatabase.Schema,
    )
    database = JellyfinDatabase(driver)

    val localDataSource = SearchHistoryLocalDataSource(
      database = database,
    )

    repository = DefaultSearchHistoryRepository(
      localDataSource = localDataSource,
      clock = { currentTime },
    )
  }

  @AfterTest
  fun tearDown() {
    driver.close()
    tempDir.deleteRecursively()
  }

  @Test
  fun saveSearch_persists_entry() {
    runTest {
      val result = repository.saveSearch(query = "inception")
      result.isSuccess().shouldBeTrue()

      val entries = repository.observeRecentSearches().first()
      entries shouldHaveSize 1
      entries[0].query shouldBe "inception"
      entries[0].searchedAt shouldBe 1000L
    }
  }

  @Test
  fun saveSearch_deduplicates_and_updates_timestamp() {
    runTest {
      currentTime = 1000L
      repository.saveSearch(query = "inception")

      currentTime = 2000L
      repository.saveSearch(query = "inception")

      val entries = repository.observeRecentSearches().first()
      entries shouldHaveSize 1
      entries[0].query shouldBe "inception"
      entries[0].searchedAt shouldBe 2000L
    }
  }

  @Test
  fun observeRecentSearches_returns_entries_ordered_by_most_recent() {
    runTest {
      currentTime = 1000L
      repository.saveSearch(query = "first")

      currentTime = 2000L
      repository.saveSearch(query = "second")

      currentTime = 3000L
      repository.saveSearch(query = "third")

      val entries = repository.observeRecentSearches().first()
      entries shouldHaveSize 3
      entries[0].query shouldBe "third"
      entries[1].query shouldBe "second"
      entries[2].query shouldBe "first"
    }
  }

  @Test
  fun deleteSearch_removes_specific_entry() {
    runTest {
      currentTime = 1000L
      repository.saveSearch(query = "keep")

      currentTime = 2000L
      repository.saveSearch(query = "remove")

      repository.deleteSearch(query = "remove")

      val entries = repository.observeRecentSearches().first()
      entries shouldHaveSize 1
      entries[0].query shouldBe "keep"
    }
  }

  @Test
  fun clearHistory_removes_all_entries() {
    runTest {
      repository.saveSearch(query = "one")
      repository.saveSearch(query = "two")

      repository.clearHistory()

      val entries = repository.observeRecentSearches().first()
      entries.shouldBeEmpty()
    }
  }

  @Test
  fun saveSearch_prunes_entries_beyond_limit() {
    runTest {
      // Save 22 entries (beyond the 20 limit)
      for(i in 1..22) {
        currentTime = i.toLong() * 1000
        repository.saveSearch(query = "query-$i")
      }

      val entries = repository.observeRecentSearches().first()
      entries shouldHaveSize 20
      // Most recent should be first
      entries[0].query shouldBe "query-22"
      // Oldest kept should be query-3 (query-1 and query-2 pruned)
      entries[19].query shouldBe "query-3"
    }
  }

  @Test
  fun observeRecentSearches_respects_limit_parameter() {
    runTest {
      for(i in 1..5) {
        currentTime = i.toLong() * 1000
        repository.saveSearch(query = "query-$i")
      }

      val entries = repository.observeRecentSearches(limit = 3).first()
      entries shouldHaveSize 3
      entries[0].query shouldBe "query-5"
      entries[1].query shouldBe "query-4"
      entries[2].query shouldBe "query-3"
    }
  }
}
