package template.nav.dev

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.eygraber.vice.nav3.viceEntry
import template.nav.BottomSheetSceneStrategy
import template.nav.TemplateNavGraph
import template.screens.dev.settings.DevSettingsGraph
import template.screens.dev.settings.DevSettingsKey

internal fun EntryProviderScope<NavKey>.templateDevNavGraph(
  navGraph: TemplateNavGraph,
  backStack: NavBackStack<NavKey>,
) {
  viceEntry<DevSettingsKey>(
    provideDevSettings(navGraph, backStack),
    metadata = BottomSheetSceneStrategy.bottomSheet(),
  )
}

private fun provideDevSettings(
  navGraph: TemplateNavGraph,
  backStack: NavBackStack<NavKey>,
) = { key: DevSettingsKey ->
  navGraph.devSettingsFactory.createDevSettingsGraph(
    navigator = devSettings(backStack),
    key = key,
  ).navEntryProvider
}

private val TemplateNavGraph.devSettingsFactory
  get() = this as DevSettingsGraph.Factory
