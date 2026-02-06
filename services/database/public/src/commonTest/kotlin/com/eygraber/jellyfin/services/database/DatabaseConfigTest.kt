package com.eygraber.jellyfin.services.database

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class DatabaseConfigTest {
  @Test
  fun default_database_name() {
    val config = DatabaseConfig()
    config.databaseName shouldBe "jellyfin.db"
  }

  @Test
  fun custom_database_name() {
    val config = DatabaseConfig(databaseName = "custom.db")
    config.databaseName shouldBe "custom.db"
  }
}
