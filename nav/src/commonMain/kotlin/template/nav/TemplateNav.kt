package template.nav

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.eygraber.vice.nav3.LocalSharedTransitionScope
import com.eygraber.vice.nav3.viceEntry
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.serializer
import template.nav.dev.DetectShakesEffect
import template.nav.dev.templateDevNavGraph
import template.screens.root.RootComponent
import template.screens.root.RootKey
import template.screens.welcome.WelcomeComponent
import template.screens.welcome.WelcomeKey

private val screenTransitionSpec: FiniteAnimationSpec<IntOffset> = tween(400)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TemplateNav(
  navComponent: TemplateNavComponent,
  modifier: Modifier = Modifier,
) {
  val backStack = rememberNavBackStack(
    configuration = SavedStateConfiguration {
      serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
          addSubclasses()
        }
      }
    },
    elements = arrayOf(RootKey),
  )

  DetectShakesEffect(
    shakeDetector = navComponent.shakeDetector,
    backStack = backStack,
  )

  HandleNavShortcutsEffect(
    navShortcutManager = navComponent.shortcutManager,
    backStack = backStack,
  )

  SharedTransitionLayout {
    CompositionLocalProvider(
      LocalSharedTransitionScope provides this,
    ) {
      NavDisplay(
        backStack = backStack,
        modifier = modifier,
        sceneStrategy = DialogSceneStrategy<NavKey>() then BottomSheetSceneStrategy() then SinglePaneSceneStrategy(),
        transitionSpec = {
          ContentTransform(
            targetContentEnter = slideInHorizontally(screenTransitionSpec) { it * 2 },
            initialContentExit = slideOutHorizontally(screenTransitionSpec) { -it },
          )
        },
        popTransitionSpec = {
          ContentTransform(
            targetContentEnter = slideInHorizontally(screenTransitionSpec) { -it },
            initialContentExit = slideOutHorizontally(screenTransitionSpec) { it * 2 },
          )
        },
        predictivePopTransitionSpec = { _ ->
          ContentTransform(
            targetContentEnter = slideInHorizontally(screenTransitionSpec) { -it },
            initialContentExit = slideOutHorizontally(screenTransitionSpec) { it * 2 },
          )
        },
        onBack = { backStack.removeLastOrNull() },
        entryProvider = remember(navComponent, backStack) {
          templateNavEntryProvider(navComponent, backStack)
        },
      )
    }
  }
}

private fun templateNavEntryProvider(
  navComponent: TemplateNavComponent,
  backStack: NavBackStack<NavKey>,
) = entryProvider {
  viceEntry<RootKey>(
    provideRoot(navComponent, backStack),
  )

  viceEntry<WelcomeKey>(
    provideWelcome(navComponent, backStack),
  )

  templateDevNavGraph(
    navComponent = navComponent,
    backStack = backStack,
  )

  entry<TemplateNavKeys.ComingSoon>(
    metadata = DialogSceneStrategy.dialog(),
  ) { key ->
    Card {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(2F),
        contentAlignment = Alignment.Center,
      ) {
        Text("${key.feature} coming soon!")
      }
    }
  }
}

private fun provideRoot(
  navComponent: TemplateNavComponent,
  backStack: NavBackStack<NavKey>,
) = { key: RootKey ->
  navComponent.rootFactory.createRootComponent(
    navigator = TemplateNavigators.root(backStack),
    key = key,
  ).navEntryProvider
}

private fun provideWelcome(
  navComponent: TemplateNavComponent,
  backStack: NavBackStack<NavKey>,
) = { key: WelcomeKey ->
  navComponent.welcomeFactory.createWelcomeComponent(
    navigator = TemplateNavigators.welcome(backStack),
    key = key,
  ).navEntryProvider
}

@Serializable
sealed interface TemplateNavKeys : NavKey {
  @Serializable
  data class ComingSoon(val feature: String) : TemplateNavKeys
}

private val TemplateNavComponent.rootFactory
  get() = this as RootComponent.Factory

private val TemplateNavComponent.welcomeFactory
  get() = this as WelcomeComponent.Factory
