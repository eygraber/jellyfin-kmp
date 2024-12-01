package template.nav

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.eygraber.compose.material3.navigation.ModalBottomSheetLayout
import com.eygraber.compose.material3.navigation.bottomSheet
import com.eygraber.compose.material3.navigation.rememberModalBottomSheetNavigator
import com.eygraber.vice.nav.LocalSharedTransitionScope
import com.eygraber.vice.nav.viceComposable
import kotlinx.serialization.Serializable
import template.destinations.root.RootDestinationComponent
import template.destinations.root.RootNavigator
import template.destinations.root.RootRoute
import template.destinations.welcome.WelcomeDestinationComponent
import template.destinations.welcome.WelcomeNavigator
import template.destinations.welcome.WelcomeRoute
import template.nav.dev.DetectShakesEffect
import template.nav.dev.templateDevNavGraph

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TemplateNav(
  navComponent: TemplateNavComponent,
  isDarkMode: Boolean,
) {
  val bottomSheetNavigator = rememberModalBottomSheetNavigator()
  val navController = rememberNavController(bottomSheetNavigator)

  DetectShakesEffect(
    shakeDetector = navComponent.shakeDetector,
    navController = navController,
  )

  HandleNavShortcutsEffect(
    navShortcutManager = navComponent.shortcutManager,
    navController = navController,
  )

  SharedTransitionLayout {
    CompositionLocalProvider(
      LocalSharedTransitionScope provides this,
    ) {
      ModalBottomSheetLayout(
        bottomSheetNavigator,
      ) {
        NavHost(
          navController = navController,
          startDestination = RootRoute,
          enterTransition = { slideInHorizontally(tween(500)) { it * 2 } },
          popEnterTransition = { slideInHorizontally(tween(500)) { -it } },
          popExitTransition = { slideOutHorizontally(tween(500)) { it * 2 } },
          exitTransition = { slideOutHorizontally(tween(500)) { -it } },
        ) {
          templateNavGraph(
            navComponent = navComponent,
            navController = navController,
            isDarkMode = isDarkMode,
          )
        }
      }
    }
  }
}

private fun NavGraphBuilder.templateNavGraph(
  navComponent: TemplateNavComponent,
  navController: NavController,
  isDarkMode: Boolean,
) {
  templateDevNavGraph(
    navComponent = navComponent,
    isDarkMode = isDarkMode,
  )

  comingSoonRoute()

  viceComposable<RootRoute> { entry ->
    navComponent.rootFactory.createRootComponent(
      navigator = RootNavigator(
        onNavigateToOnboarding = {
          navController.navigate(TemplateRoutes.Onboarding) {
            popUpTo(RootRoute) {
              inclusive = true
            }
          }
        },
      ),
      route = entry.route,
    ).destination
  }

  onboardingNavGraph(
    navComponent = navComponent,
    navController = navController,
  )
}

private fun NavGraphBuilder.onboardingNavGraph(
  navComponent: TemplateNavComponent,
  navController: NavController,
) {
  navigation<TemplateRoutes.Onboarding>(
    startDestination = WelcomeRoute,
  ) {
    viceComposable<WelcomeRoute> { entry ->
      navComponent.welcomeFactory.createWelcomeComponent(
        navigator = WelcomeNavigator(
          onNavigateToSignUp = { navController.navigate(TemplateRoutes.ComingSoon("SignUp")) },
          onNavigateToLogin = { navController.navigate(TemplateRoutes.ComingSoon("Login")) },
        ),
        route = entry.route,
      ).destination
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun NavGraphBuilder.comingSoonRoute() {
  bottomSheet<TemplateRoutes.ComingSoon> { entry ->
    val comingSoon = entry.toRoute<TemplateRoutes.ComingSoon>()
    Box(
      modifier = Modifier.padding(16.dp),
    ) {
      Text("${comingSoon.feature} coming soon!")
    }
  }
}

@Serializable
private sealed interface TemplateRoutes {
  @Serializable
  data class ComingSoon(val feature: String) : TemplateRoutes

  @Serializable
  data object Onboarding : TemplateRoutes
}

private val TemplateNavComponent.rootFactory
  get() = this as RootDestinationComponent.Factory

private val TemplateNavComponent.welcomeFactory
  get() = this as WelcomeDestinationComponent.Factory
