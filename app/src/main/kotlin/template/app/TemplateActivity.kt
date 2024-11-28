package template.app

import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import template.nav.TemplateNav
import template.ui.material.theme.SystemDarkModeOverride
import template.ui.material.theme.TemplateTheme

class TemplateActivity : AppCompatActivity() {
  private val activityComponent by lazy {
    application.templateApplicationComponent.createTemplateActivityComponent(
      activity = this,
    )
  }

  private val navComponent by lazy {
    activityComponent.createTemplateNavComponent()
  }

  override fun onKeyShortcut(keyCode: Int, event: KeyEvent?): Boolean =
    if(event != null && navComponent.shortcutManager.handleKeyEvent(keyCode, event)) {
      true
    }
    else {
      super.onKeyShortcut(keyCode, event)
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    activityComponent.splashScreenController.init(
      isAppRestoring = savedInstanceState != null,
    )

    enableEdgeToEdge()

    super.onCreate(savedInstanceState)
    setContent {
      val isDarkMode = when(SystemDarkModeOverride.rememberState()) {
        SystemDarkModeOverride.None -> isSystemInDarkTheme()
        SystemDarkModeOverride.Dark -> true
        SystemDarkModeOverride.Light -> false
      }

      SystemUiController(isDarkMode)

      Content(isDarkMode)
    }
  }

  @Composable
  private fun Content(isDarkMode: Boolean) {
    TemplateTheme(
      isDarkMode = isDarkMode,
    ) {
      Surface(
        modifier = Modifier.fillMaxSize(),
      ) {
        TemplateNav(
          navComponent = navComponent,
          isDarkMode = isDarkMode,
        )
      }
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
