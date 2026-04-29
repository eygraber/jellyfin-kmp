package com.eygraber.jellyfin.screens.login

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
data object LoginKey : NavKey

@Inject
@SingleIn(ScreenScope::class)
class LoginNavEntryProvider(
  override val compositor: LoginCompositor,
) : ViceNavEntryProvider<Key, Intent, Compositor, Effects, ViewState>() {
  override val view: View = { state, onIntent -> LoginView(state, onIntent) }
  override val effects: ViceEffects = ViceEffects.None
}

@GraphExtension(ScreenScope::class)
interface LoginGraph {
  val navEntryProvider: LoginNavEntryProvider

  @ContributesTo(NavScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createLoginGraph(
      @Provides navigator: LoginNavigator,
      @Provides key: LoginKey,
    ): LoginGraph
  }
}

private typealias Key = LoginKey
private typealias View = LoginView
private typealias Intent = LoginIntent
private typealias Compositor = LoginCompositor
private typealias Effects = ViceEffects
private typealias ViewState = LoginViewState
