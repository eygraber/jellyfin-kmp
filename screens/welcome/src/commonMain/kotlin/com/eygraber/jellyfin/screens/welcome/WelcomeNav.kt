package com.eygraber.jellyfin.screens.welcome

import androidx.navigation3.runtime.NavKey
import com.eygraber.jellyfin.di.scopes.NavScope
import com.eygraber.jellyfin.di.scopes.ScreenScope
import com.eygraber.vice.ViceEffects
import com.eygraber.vice.nav3.ViceNavEntryProvider
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.serialization.Serializable

@Serializable
data object WelcomeKey : NavKey

@Inject
@SingleIn(ScreenScope::class)
class WelcomeNavEntryProvider(
  override val compositor: WelcomeCompositor,
) : ViceNavEntryProvider<Key, Intent, Compositor, Effects, ViewState>() {
  override val view: View = { state, onIntent -> WelcomeView(state, onIntent) }
  override val effects: ViceEffects = ViceEffects.None
}

@GraphExtension(ScreenScope::class)
interface WelcomeGraph {
  val navEntryProvider: WelcomeNavEntryProvider

  @ContributesTo(NavScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createWelcomeGraph(
      @Provides navigator: WelcomeNavigator,
      @Provides key: WelcomeKey,
    ): WelcomeGraph
  }
}

private typealias Key = WelcomeKey
private typealias View = WelcomeView
private typealias Intent = WelcomeIntent
private typealias Compositor = WelcomeCompositor
private typealias Effects = ViceEffects
private typealias ViewState = WelcomeViewState
