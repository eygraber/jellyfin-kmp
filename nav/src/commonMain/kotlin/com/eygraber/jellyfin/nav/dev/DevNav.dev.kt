package com.eygraber.jellyfin.nav.dev

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.eygraber.jellyfin.nav.BottomSheetSceneStrategy
import com.eygraber.jellyfin.nav.JellyfinNavGraph
import com.eygraber.jellyfin.screens.dev.settings.DevSettingsGraph
import com.eygraber.jellyfin.screens.dev.settings.DevSettingsKey
import com.eygraber.vice.nav3.viceEntry

internal fun EntryProviderScope<NavKey>.jellyfinDevNavGraph(
  navGraph: JellyfinNavGraph,
  backStack: NavBackStack<NavKey>,
) {
  viceEntry<DevSettingsKey>(
    provideDevSettings(navGraph, backStack),
    metadata = BottomSheetSceneStrategy.bottomSheet(),
  )
}

private fun provideDevSettings(
  navGraph: JellyfinNavGraph,
  backStack: NavBackStack<NavKey>,
) = { key: DevSettingsKey ->
  navGraph.devSettingsFactory.createDevSettingsGraph(
    navigator = devSettings(backStack),
    key = key,
  ).navEntryProvider
}

private val JellyfinNavGraph.devSettingsFactory
  get() = this as DevSettingsGraph.Factory
