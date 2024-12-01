package template.nav.dev

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.eygraber.compose.material3.navigation.bottomSheet
import com.eygraber.vice.nav.viceComposable
import kotlinx.serialization.Serializable
import template.destinations.dev.settings.DevSettingsDestinationComponent
import template.destinations.dev.settings.DevSettingsNavigator
import template.destinations.dev.settings.DevSettingsRoute
import template.nav.TemplateNavComponent

@Serializable
internal object TemplateRoutesDevSettings

@OptIn(ExperimentalMaterial3Api::class)
internal expect fun platformModalBottomSheetProperties(isDarkMode: Boolean): ModalBottomSheetProperties

@OptIn(ExperimentalMaterial3Api::class)
internal fun NavGraphBuilder.templateDevNavGraph(
  navComponent: TemplateNavComponent,
  isDarkMode: Boolean,
) {
  navigation<TemplateRoutesDevSettings>(
    startDestination = DevSettingsRoute,
  ) {
    bottomSheet<DevSettingsRoute>(
      modalBottomSheetProperties = platformModalBottomSheetProperties(isDarkMode = isDarkMode),
      skipPartiallyExpanded = false,
    ) {
      val devNavController = rememberNavController()
      NavHost(
        navController = devNavController,
        startDestination = DevSettingsRoute,
        enterTransition = { slideInVertically(tween(500)) { it * 2 } },
        popEnterTransition = { slideInHorizontally(tween(500)) { -it } },
        popExitTransition = { slideOutHorizontally(tween(500)) { it * 2 } },
        exitTransition = { slideOutVertically(tween(500)) { it * 2 } },
      ) {
        navGraph(
          navComponent = navComponent,
          navController = devNavController,
        )
      }
    }
  }
}

private fun NavGraphBuilder.navGraph(
  navComponent: TemplateNavComponent,
  navController: NavController,
) {
  viceComposable<DevSettingsRoute>(
    enterTransition = { slideInVertically { it * 2 } },
    exitTransition = { slideOutVertically { it * 2 } },
  ) { entry ->
    navComponent.devSettingsFactory.createDevSettingsComponent(
      navigator = DevSettingsNavigator(
        onNavigateBack = { navController.popBackStack() },
      ),
      route = entry.route,
    ).destination
  }
}

private val TemplateNavComponent.devSettingsFactory
  get() = this as DevSettingsDestinationComponent.Factory
