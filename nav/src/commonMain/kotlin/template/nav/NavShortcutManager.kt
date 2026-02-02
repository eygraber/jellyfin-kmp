package template.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.input.key.KeyEvent
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import template.di.scopes.NavScope
import template.nav.dev.handleEnvironment
import template.nav.dev.handleEnvironmentKeyEvent

interface NavShortcutManager {
  val shortcutFlow: Flow<NavShortcuts>

  fun handleKeyEvent(event: KeyEvent): Boolean
}

enum class NavShortcuts {
  DevSettings,
}

@SingleIn(NavScope::class)
@ContributesBinding(NavScope::class)
class RealNavShortcutManager : NavShortcutManager {
  private val shortcuts = MutableSharedFlow<NavShortcuts>(extraBufferCapacity = 1)

  override val shortcutFlow = shortcuts

  override fun handleKeyEvent(event: KeyEvent): Boolean =
    when(val environmentSpecificShortcut = handleEnvironmentKeyEvent(event)) {
      null -> false
      else -> {
        shortcuts.tryEmit(environmentSpecificShortcut)
        true
      }
    }
}

@Composable
internal fun HandleNavShortcutsEffect(
  navShortcutManager: NavShortcutManager,
  backStack: NavBackStack<NavKey>,
) {
  LaunchedEffect(Unit) {
    navShortcutManager.shortcutFlow.collect { shortcut ->
      shortcut.handleEnvironment(backStack)
    }
  }
}
