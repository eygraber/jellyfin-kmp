package template.nav

import android.view.KeyEvent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import template.di.scopes.NavScope

interface NavShortcutManager {
  val shortcutFlow: Flow<NavShortcuts>

  fun handleKeyEvent(keyCode: Int, event: KeyEvent): Boolean
}

enum class NavShortcuts {
  DevSettings,
}

@Inject
@SingleIn(NavScope::class)
@ContributesBinding(NavScope::class)
class RealNavShortcutManager : NavShortcutManager {
  private val shortcuts = MutableSharedFlow<NavShortcuts>(extraBufferCapacity = 1)

  override val shortcutFlow = shortcuts

  override fun handleKeyEvent(keyCode: Int, event: KeyEvent): Boolean =
    when(val environmentSpecificShortcut = handleEnvironmentKeyEvent(keyCode, event)) {
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
  navController: NavController,
) {
  LaunchedEffect(Unit) {
    navShortcutManager.shortcutFlow.collect { shortcut ->
      shortcut.handleEnvironment(navController)
    }
  }
}
