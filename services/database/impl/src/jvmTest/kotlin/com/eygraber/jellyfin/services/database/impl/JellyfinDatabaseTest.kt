package com.eygraber.jellyfin.services.database.impl

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteDatabaseType
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteDriver
import com.eygraber.sqldelight.androidx.driver.File
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class JellyfinDatabaseTest {
  private lateinit var driver: AndroidxSqliteDriver
  private lateinit var database: JellyfinDatabase
  private lateinit var tempDir: File

  @BeforeTest
  fun setUp() {
    tempDir = File.createTempFile("jellyfin-test", null).apply {
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
  }

  @AfterTest
  fun tearDown() {
    driver.close()
    tempDir.deleteRecursively()
  }

  @Test
  fun insert_and_select_server() {
    val now = System.currentTimeMillis()

    database.serverQueries.insert(
      id = "server-1",
      name = "My Jellyfin",
      url = "https://jellyfin.example.com",
      version = "10.9.0",
      created_at = now,
      last_used_at = now,
    )

    val server = database.serverQueries.selectById(id = "server-1").executeAsOneOrNull()
    server.shouldNotBeNull()
    server.id shouldBe "server-1"
    server.name shouldBe "My Jellyfin"
    server.url shouldBe "https://jellyfin.example.com"
    server.version shouldBe "10.9.0"
    server.created_at shouldBe now
    server.last_used_at shouldBe now
  }

  @Test
  fun select_all_servers_ordered_by_last_used() {
    val now = System.currentTimeMillis()

    database.serverQueries.insert(
      id = "server-1",
      name = "Old Server",
      url = "https://old.example.com",
      version = "10.8.0",
      created_at = now - 2_000,
      last_used_at = now - 2_000,
    )

    database.serverQueries.insert(
      id = "server-2",
      name = "New Server",
      url = "https://new.example.com",
      version = "10.9.0",
      created_at = now - 1_000,
      last_used_at = now,
    )

    val servers = database.serverQueries.selectAll().executeAsList()
    servers shouldHaveSize 2
    servers[0].id shouldBe "server-2"
    servers[1].id shouldBe "server-1"
  }

  @Test
  fun select_server_by_url() {
    val now = System.currentTimeMillis()

    database.serverQueries.insert(
      id = "server-1",
      name = "My Jellyfin",
      url = "https://jellyfin.example.com",
      version = null,
      created_at = now,
      last_used_at = now,
    )

    val server = database.serverQueries.selectByUrl(
      url = "https://jellyfin.example.com",
    ).executeAsOneOrNull()
    server.shouldNotBeNull()
    server.id shouldBe "server-1"
    server.version.shouldBeNull()
  }

  @Test
  fun update_server_last_used() {
    val now = System.currentTimeMillis()
    val later = now + 5_000

    database.serverQueries.insert(
      id = "server-1",
      name = "My Jellyfin",
      url = "https://jellyfin.example.com",
      version = "10.9.0",
      created_at = now,
      last_used_at = now,
    )

    database.serverQueries.updateLastUsed(
      last_used_at = later,
      id = "server-1",
    )

    val server = database.serverQueries.selectById(id = "server-1").executeAsOneOrNull()
    server.shouldNotBeNull()
    server.last_used_at shouldBe later
  }

  @Test
  fun delete_server() {
    val now = System.currentTimeMillis()

    database.serverQueries.insert(
      id = "server-1",
      name = "My Jellyfin",
      url = "https://jellyfin.example.com",
      version = "10.9.0",
      created_at = now,
      last_used_at = now,
    )

    database.serverQueries.delete(id = "server-1")

    val server = database.serverQueries.selectById(id = "server-1").executeAsOneOrNull()
    server.shouldBeNull()
  }

  @Test
  fun delete_all_servers() {
    val now = System.currentTimeMillis()

    database.serverQueries.insert(
      id = "server-1",
      name = "Server 1",
      url = "https://s1.example.com",
      version = null,
      created_at = now,
      last_used_at = now,
    )

    database.serverQueries.insert(
      id = "server-2",
      name = "Server 2",
      url = "https://s2.example.com",
      version = null,
      created_at = now,
      last_used_at = now,
    )

    database.serverQueries.deleteAll()

    val servers = database.serverQueries.selectAll().executeAsList()
    servers.shouldBeEmpty()
  }

  @Test
  fun insert_and_select_user_session() {
    val now = System.currentTimeMillis()

    database.serverQueries.insert(
      id = "server-1",
      name = "My Jellyfin",
      url = "https://jellyfin.example.com",
      version = "10.9.0",
      created_at = now,
      last_used_at = now,
    )

    database.userSessionQueries.insert(
      id = "session-1",
      server_id = "server-1",
      user_id = "user-1",
      username = "admin",
      access_token = "token-abc",
      is_active = 1,
      created_at = now,
      last_used_at = now,
    )

    val session = database.userSessionQueries.selectById(id = "session-1").executeAsOneOrNull()
    session.shouldNotBeNull()
    session.id shouldBe "session-1"
    session.server_id shouldBe "server-1"
    session.user_id shouldBe "user-1"
    session.username shouldBe "admin"
    session.access_token shouldBe "token-abc"
    session.is_active shouldBe 1
  }

  @Test
  fun select_sessions_by_server_id() {
    val now = System.currentTimeMillis()

    database.serverQueries.insert(
      id = "server-1",
      name = "My Jellyfin",
      url = "https://jellyfin.example.com",
      version = "10.9.0",
      created_at = now,
      last_used_at = now,
    )

    database.userSessionQueries.insert(
      id = "session-1",
      server_id = "server-1",
      user_id = "user-1",
      username = "admin",
      access_token = "token-1",
      is_active = 0,
      created_at = now,
      last_used_at = now,
    )

    database.userSessionQueries.insert(
      id = "session-2",
      server_id = "server-1",
      user_id = "user-2",
      username = "user",
      access_token = "token-2",
      is_active = 0,
      created_at = now,
      last_used_at = now + 1_000,
    )

    val sessions = database.userSessionQueries.selectByServerId(
      server_id = "server-1",
    ).executeAsList()
    sessions shouldHaveSize 2
    sessions[0].id shouldBe "session-2"
    sessions[1].id shouldBe "session-1"
  }

  @Test
  fun set_and_clear_active_session() {
    val now = System.currentTimeMillis()

    database.serverQueries.insert(
      id = "server-1",
      name = "My Jellyfin",
      url = "https://jellyfin.example.com",
      version = "10.9.0",
      created_at = now,
      last_used_at = now,
    )

    database.userSessionQueries.insert(
      id = "session-1",
      server_id = "server-1",
      user_id = "user-1",
      username = "admin",
      access_token = "token-1",
      is_active = 0,
      created_at = now,
      last_used_at = now,
    )

    database.userSessionQueries.setActive(
      last_used_at = now + 1_000,
      id = "session-1",
    )

    val active = database.userSessionQueries.selectActive().executeAsOneOrNull()
    active.shouldNotBeNull()
    active.id shouldBe "session-1"
    active.is_active shouldBe 1

    database.userSessionQueries.clearActive()

    val noActive = database.userSessionQueries.selectActive().executeAsOneOrNull()
    noActive.shouldBeNull()
  }

  @Test
  fun cascade_delete_sessions_when_server_deleted() {
    val now = System.currentTimeMillis()

    database.serverQueries.insert(
      id = "server-1",
      name = "My Jellyfin",
      url = "https://jellyfin.example.com",
      version = "10.9.0",
      created_at = now,
      last_used_at = now,
    )

    database.userSessionQueries.insert(
      id = "session-1",
      server_id = "server-1",
      user_id = "user-1",
      username = "admin",
      access_token = "token-1",
      is_active = 0,
      created_at = now,
      last_used_at = now,
    )

    // Enable foreign keys for cascade to work
    driver.execute(
      identifier = null,
      sql = "PRAGMA foreign_keys = ON",
      parameters = 0,
    )

    database.serverQueries.delete(id = "server-1")

    val sessions = database.userSessionQueries.selectByServerId(
      server_id = "server-1",
    ).executeAsList()
    sessions.shouldBeEmpty()
  }
}
