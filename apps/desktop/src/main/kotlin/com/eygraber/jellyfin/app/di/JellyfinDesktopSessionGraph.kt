package com.eygraber.jellyfin.app.di

import com.eygraber.jellyfin.di.scopes.SessionScope
import com.eygraber.jellyfin.nav.JellyfinNavGraph
import com.eygraber.jellyfin.services.splash.screen.SplashScreenController
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension

@GraphExtension(SessionScope::class)
interface JellyfinDesktopSessionGraph : JellyfinNavGraph.Factory {
  val splashScreenController: SplashScreenController

  @ContributesTo(AppScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createJellyfinDesktopSessionGraph(): JellyfinDesktopSessionGraph
  }
}
