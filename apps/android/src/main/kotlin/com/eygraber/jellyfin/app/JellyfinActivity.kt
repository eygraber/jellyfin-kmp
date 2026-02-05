package com.eygraber.jellyfin.app

import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.eygraber.jellyfin.apps.shared.JellyfinAppSession
import androidx.compose.ui.input.key.KeyEvent as ComposeKeyEvent

class JellyfinActivity : AppCompatActivity() {
  private val activityGraph by lazy {
    application.jellyfinApplicationGraph.createJellyfinActivityGraph(
      activity = this,
    )
  }

  private val navGraph by lazy {
    activityGraph.createJellyfinNavGraph()
  }

  override fun onKeyShortcut(keyCode: Int, event: KeyEvent): Boolean =
    navGraph.shortcutManager.handleKeyEvent(ComposeKeyEvent(event)) ||
      super.onKeyShortcut(keyCode, event)

  override fun onCreate(savedInstanceState: Bundle?) {
    activityGraph.splashScreenController.init(
      isAppRestoring = savedInstanceState != null,
    )

    enableEdgeToEdge()

    super.onCreate(savedInstanceState)
    setContent {
      JellyfinAppSession(
        onDarkMode = { isDarkMode ->
          SystemUiController(isDarkMode)
        },
        navGraph = navGraph,
      )
    }
  }
}

@Composable
private fun ComponentActivity.SystemUiController(
  isDarkMode: Boolean,
) {
  DisposableEffect(isDarkMode) {
    enableEdgeToEdge(
      statusBarStyle = when {
        isDarkMode -> SystemBarStyle.dark(
          scrim = Color.TRANSPARENT,
        )

        else -> SystemBarStyle.light(
          scrim = Color.TRANSPARENT,
          darkScrim = Color.TRANSPARENT,
        )
      },
      navigationBarStyle = when {
        isDarkMode -> SystemBarStyle.dark(
          scrim = darkScrim,
        )

        else -> SystemBarStyle.light(
          scrim = lightScrim,
          darkScrim = darkScrim,
        )
      },
    )

    onDispose {}
  }
}

/**
 * The default light scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=35-38;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val lightScrim = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

/**
 * The default dark scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=40-44;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val darkScrim = Color.argb(0x80, 0x1b, 0x1b, 0x1b)
