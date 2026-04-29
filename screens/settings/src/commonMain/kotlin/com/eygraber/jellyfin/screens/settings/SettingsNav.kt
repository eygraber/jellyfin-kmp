package com.eygraber.jellyfin.screens.settings

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
data object SettingsKey : NavKey

@Inject
@SingleIn(ScreenScope::class)
class SettingsNavEntryProvider(
  override val compositor: SettingsCompositor,
) : ViceNavEntryProvider<Key, Intent, Compositor, Effects, ViewState>() {
  override val view: View = { state, onIntent -> SettingsView(state, onIntent) }
  override val effects: ViceEffects = ViceEffects.None
}

@GraphExtension(ScreenScope::class)
interface SettingsGraph {
  val navEntryProvider: SettingsNavEntryProvider

  @ContributesTo(NavScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createSettingsGraph(
      @Provides navigator: SettingsNavigator,
      @Provides key: SettingsKey,
    ): SettingsGraph
  }
}

private typealias Key = SettingsKey
private typealias View = SettingsView
private typealias Intent = SettingsIntent
private typealias Compositor = SettingsCompositor
private typealias Effects = ViceEffects
private typealias ViewState = SettingsViewState
