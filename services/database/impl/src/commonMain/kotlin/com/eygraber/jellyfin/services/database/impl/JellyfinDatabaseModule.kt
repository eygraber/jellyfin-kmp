package com.eygraber.jellyfin.services.database.impl

import com.eygraber.jellyfin.services.database.DatabaseConfig
import com.eygraber.jellyfin.services.database.JellyfinDatabaseProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(AppScope::class)
interface JellyfinDatabaseModule {
  @Provides
  @SingleIn(AppScope::class)
  fun provideDatabaseConfig(): DatabaseConfig = DatabaseConfig()

  @Provides
  @SingleIn(AppScope::class)
  fun provideDatabase(
    provider: JellyfinDatabaseProvider,
  ): JellyfinDatabase = JellyfinDatabase(driver = provider.createDriver())
}
