package com.eygraber.jellyfin.data.server.impl

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.common.isError
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.data.server.ServerEntity
import com.eygraber.jellyfin.services.database.impl.JellyfinDatabase
import com.eygraber.jellyfin.services.logging.JellyfinLogger
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteDatabaseType
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteDriver
import com.eygraber.sqldelight.androidx.driver.File
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class SqlDelightServerRepositoryTest {
  private lateinit var driver: AndroidxSqliteDriver
  private lateinit var database: JellyfinDatabase
  private lateinit var repository: SqlDelightServerRepository
  private lateinit var tempDir: File

  private val noopLogger = object : JellyfinLogger {
    override fun verbose(tag: String, message: String) {}
    override fun debug(tag: String, message: String) {}
    override fun info(tag: String, message: String) {}
    override fun warn(tag: String, message: String, throwable: Throwable?) {}
    override fun error(tag: String, message: String, throwable: Throwable?) {}
  }

  private val testServer = ServerEntity(
    id = "server-1",
    name = "Test Server",
    url = "https://jellyfin.example.com",
    version = "10.9.0",
    createdAt = 1000L,
    lastUsedAt = 2000L,
  )

  @BeforeTest
  fun setUp() {
    tempDir = File.createTempFile("jellyfin-repo-test", null).apply {
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
    val localDataSource = ServerLocalDataSource(database = database)
    repository = SqlDelightServerRepository(
      localDataSource = localDataSource,
      logger = noopLogger,
    )
  }

  @AfterTest
  fun tearDown() {
    driver.close()
    tempDir.deleteRecursively()
  }

  @Test
  fun save_and_get_server() {
    runTest {
      val saveResult = repository.saveServer(testServer)
      saveResult.isSuccess() shouldBe true

      val getResult = repository.getServerById("server-1")
      getResult.isSuccess() shouldBe true
      val server = (getResult as JellyfinResult.Success).value
      server.id shouldBe "server-1"
      server.name shouldBe "Test Server"
      server.url shouldBe "https://jellyfin.example.com"
      server.version shouldBe "10.9.0"
    }
  }

  @Test
  fun get_servers_returns_ordered_by_last_used() {
    runTest {
      repository.saveServer(testServer.copy(id = "s1", lastUsedAt = 1000L))
      repository.saveServer(testServer.copy(id = "s2", lastUsedAt = 3000L))
      repository.saveServer(testServer.copy(id = "s3", lastUsedAt = 2000L))

      val result = repository.getServers()
      result.isSuccess() shouldBe true
      val servers = (result as JellyfinResult.Success).value
      servers shouldHaveSize 3
      servers[0].id shouldBe "s2"
      servers[1].id shouldBe "s3"
      servers[2].id shouldBe "s1"
    }
  }

  @Test
  fun get_server_by_url() {
    runTest {
      repository.saveServer(testServer)

      val result = repository.getServerByUrl("https://jellyfin.example.com")
      result.isSuccess() shouldBe true
      val server = (result as JellyfinResult.Success).value
      server.id shouldBe "server-1"
    }
  }

  @Test
  fun get_non_existent_server_returns_error() {
    runTest {
      val result = repository.getServerById("non-existent")
      result.isError() shouldBe true
    }
  }

  @Test
  fun update_last_used() {
    runTest {
      repository.saveServer(testServer)

      val updateResult = repository.updateLastUsed(
        serverId = "server-1",
        timestamp = 5000L,
      )
      updateResult.isSuccess() shouldBe true

      val getResult = repository.getServerById("server-1")
      val server = (getResult as JellyfinResult.Success).value
      server.lastUsedAt shouldBe 5000L
    }
  }

  @Test
  fun delete_server() {
    runTest {
      repository.saveServer(testServer)

      val deleteResult = repository.deleteServer("server-1")
      deleteResult.isSuccess() shouldBe true

      val getResult = repository.getServerById("server-1")
      getResult.isError() shouldBe true
    }
  }

  @Test
  fun delete_all_servers() {
    runTest {
      repository.saveServer(testServer.copy(id = "s1"))
      repository.saveServer(testServer.copy(id = "s2"))

      val deleteResult = repository.deleteAllServers()
      deleteResult.isSuccess() shouldBe true

      val getResult = repository.getServers()
      val servers = (getResult as JellyfinResult.Success).value
      servers.shouldBeEmpty()
    }
  }

  @Test
  fun observe_servers_emits_initial_value() {
    runTest {
      repository.saveServer(testServer)

      val servers = repository.observeServers().first()
      servers shouldHaveSize 1
      servers[0].id shouldBe "server-1"
    }
  }

  @Test
  fun save_server_with_same_id_updates_existing() {
    runTest {
      repository.saveServer(testServer)
      repository.saveServer(testServer.copy(name = "Updated Server"))

      val result = repository.getServerById("server-1")
      val server = (result as JellyfinResult.Success).value
      server.name shouldBe "Updated Server"
    }
  }

  @Test
  fun save_server_with_null_version() {
    runTest {
      repository.saveServer(testServer.copy(version = null))

      val result = repository.getServerById("server-1")
      val server = (result as JellyfinResult.Success).value
      server.version shouldBe null
    }
  }
}
