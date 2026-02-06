package com.eygraber.jellyfin.services.database.impl

import com.eygraber.jellyfin.services.database.DatabaseConfig
import com.eygraber.jellyfin.services.database.JellyfinDatabaseProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

/**
 * Metro DI module that provides the [JellyfinDatabase] instance.
 *
 * The database is created as a singleton at app scope so all repositories
 * share the same database connection.
 */
@ContributesTo(AppScope::class)
interface JellyfinDatabaseModule {
  @Provides
  @SingleIn(AppScope::class)
  fun provideDatabaseConfig(): DatabaseConfig = DatabaseConfig()

  @Provides
  @SingleIn(AppScope::class)
  fun provideDatabaseProvider(
    config: DatabaseConfig,
  ): JellyfinDatabaseProvider = JellyfinDatabaseProviderImpl(config = config)

  @Provides
  @SingleIn(AppScope::class)
  fun provideDatabase(
    provider: JellyfinDatabaseProvider,
  ): JellyfinDatabase = JellyfinDatabase(driver = provider.createDriver())
}
