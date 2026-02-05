package com.eygraber.jellyfin.app.di

import android.app.Activity
import com.eygraber.jellyfin.app.di.android.AndroidActivityProviders
import com.eygraber.jellyfin.di.scopes.SessionScope
import com.eygraber.jellyfin.nav.JellyfinNavGraph
import com.eygraber.jellyfin.services.splash.screen.SplashScreenController
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

@GraphExtension(SessionScope::class)
interface JellyfinActivityGraph : AndroidActivityProviders, JellyfinNavGraph.Factory {
  val splashScreenController: SplashScreenController

  @ContributesTo(AppScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createJellyfinActivityGraph(
      @Provides activity: Activity,
    ): JellyfinActivityGraph
  }
}
