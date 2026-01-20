package template.nav

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import template.screens.root.RootKey

internal fun PolymorphicModuleBuilder<NavKey>.addSubclasses() {
  subclass(RootKey::class, RootKey.serializer())
}
