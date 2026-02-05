package com.eygraber.jellyfin.nav

import androidx.navigation3.runtime.NavKey
import com.eygraber.jellyfin.screens.root.RootKey
import kotlinx.serialization.modules.PolymorphicModuleBuilder

internal fun PolymorphicModuleBuilder<NavKey>.addSubclasses() {
  subclass(RootKey::class, RootKey.serializer())
}
