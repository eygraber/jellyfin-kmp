package template.nav.dev

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.eygraber.vice.nav3.viceEntry
import template.nav.BottomSheetSceneStrategy
import template.nav.TemplateNavComponent
import template.screens.dev.settings.DevSettingsComponent
import template.screens.dev.settings.DevSettingsKey

internal fun EntryProviderScope<NavKey>.templateDevNavGraph(
  navComponent: TemplateNavComponent,
  backStack: NavBackStack<NavKey>,
) {
  viceEntry<DevSettingsKey>(
    provideDevSettings(navComponent, backStack),
    metadata = BottomSheetSceneStrategy.bottomSheet(),
  )
}

private fun provideDevSettings(
  navComponent: TemplateNavComponent,
  backStack: NavBackStack<NavKey>,
) = { key: DevSettingsKey ->
  navComponent.devSettingsFactory.createDevSettingsComponent(
    navigator = devSettings(backStack),
    key = key,
  ).navEntryProvider
}

private val TemplateNavComponent.devSettingsFactory
  get() = this as DevSettingsComponent.Factory
