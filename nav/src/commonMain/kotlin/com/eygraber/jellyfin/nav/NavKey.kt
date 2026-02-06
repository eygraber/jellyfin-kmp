package com.eygraber.jellyfin.nav

import androidx.navigation3.runtime.NavKey
import com.eygraber.jellyfin.screens.home.HomeKey
import com.eygraber.jellyfin.screens.root.RootKey
import kotlinx.serialization.modules.PolymorphicModuleBuilder

internal fun PolymorphicModuleBuilder<NavKey>.addSubclasses() {
  subclass(HomeKey::class, HomeKey.serializer())
  subclass(RootKey::class, RootKey.serializer())
}
