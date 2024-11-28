package template.nav

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.eygraber.compose.material3.navigation.ModalBottomSheetLayout
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
      val context = LocalContext.current

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
            context = context,
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
  context: Context,
  navComponent: TemplateNavComponent,
  navController: NavController,
  isDarkMode: Boolean,
) {
  templateDevNavGraph(
    navComponent = navComponent,
    isDarkMode = isDarkMode,
  )

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
    context = context,
    navComponent = navComponent,
  )
}

private fun NavGraphBuilder.onboardingNavGraph(
  context: Context,
  navComponent: TemplateNavComponent,
) {
  navigation<TemplateRoutes.Onboarding>(
    startDestination = WelcomeRoute,
  ) {
    viceComposable<WelcomeRoute> { entry ->
      navComponent.welcomeFactory.createWelcomeComponent(
        navigator = WelcomeNavigator(
          onNavigateToSignUp = { context.showComingSoonToast("SignUp") },
          onNavigateToLogin = { context.showComingSoonToast("Login") },
        ),
        route = entry.route,
      ).destination
    }
  }
}

@Serializable
private sealed interface TemplateRoutes {
  @Serializable
  data object Onboarding : TemplateRoutes
}

private fun Context.showComingSoonToast(feature: String) {
  Toast.makeText(this, "$feature coming soon!", Toast.LENGTH_SHORT).show()
}

private val TemplateNavComponent.rootFactory
  get() = this as RootDestinationComponent.Factory

private val TemplateNavComponent.welcomeFactory
  get() = this as WelcomeDestinationComponent.Factory
