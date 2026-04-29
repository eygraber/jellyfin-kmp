package com.eygraber.jellyfin.nav

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope

/**
 * A [Scene] that renders a single top-level [NavEntry] inside the
 * [JellyfinNavigationSuiteScaffold].
 *
 * All instances of this scene share the same [SCENE_KEY], which means
 * [androidx.navigation3.ui.NavDisplay] treats transitions between top-level destinations as the
 * same scene. As a result the surrounding navigation suite (bottom bar / rail / drawer) stays
 * stable across these transitions and only the inner entry content animates via the nested
 * [AnimatedContent] inside [content].
 *
 * Transitions to and from non-top-level destinations (detail / onboarding screens) still go
 * through a different scene, so the navigation suite participates in the standard scene-level
 * animation in those cases.
 */
@Immutable
internal class TopLevelDestinationScene(
  private val entry: NavEntry<NavKey>,
  override val previousEntries: List<NavEntry<NavKey>>,
  private val onTopLevelDestinationSelected: (JellyfinTopLevelDestination) -> Unit,
) : Scene<NavKey> {
  override val key: Any = SCENE_KEY

  override val entries: List<NavEntry<NavKey>> = listOf(entry)

  override val content: @Composable () -> Unit = {
    JellyfinNavigationSuiteScaffold(
      currentTopLevelDestination = JellyfinTopLevelDestination.forContentKey(entry.contentKey),
      onSelectTopLevelDestination = onTopLevelDestinationSelected,
    ) {
      AnimatedContent(
        targetState = entry,
        contentKey = { it.contentKey },
        transitionSpec = {
          fadeIn(topLevelTransitionSpec) togetherWith fadeOut(topLevelTransitionSpec)
        },
        label = "top-level-destination",
      ) { targetEntry ->
        targetEntry.Content()
      }
    }
  }

  override fun equals(other: Any?): Boolean {
    if(this === other) return true
    if(other !is TopLevelDestinationScene) return false

    return entry == other.entry &&
      previousEntries == other.previousEntries
  }

  override fun hashCode(): Int {
    var result = entry.hashCode()
    result = 31 * result + previousEntries.hashCode()
    return result
  }

  companion object {
    /**
     * A constant key shared by every [TopLevelDestinationScene] so that the navigation suite is
     * preserved across top-level destination changes. NavDisplay identifies a scene by its
     * `(class, key)` pair, and a stable key tells it that switching between top-level entries is
     * not a scene transition.
     */
    internal const val SCENE_KEY: String = "jellyfin-top-level-destination"

    private val topLevelTransitionSpec = tween<Float>(durationMillis = 200)
  }
}

/**
 * Returns a [TopLevelDestinationScene] when the top of the back stack is a known
 * [JellyfinTopLevelDestination] (e.g. Home, Search). Returns `null` for any other entry, allowing
 * the next [SceneStrategy] in the chain to handle it.
 */
internal class TopLevelDestinationSceneStrategy(
  private val onTopLevelDestinationSelected: (JellyfinTopLevelDestination) -> Unit,
) : SceneStrategy<NavKey> {
  override fun SceneStrategyScope<NavKey>.calculateScene(
    entries: List<NavEntry<NavKey>>,
  ): Scene<NavKey>? {
    val lastEntry = entries.lastOrNull() ?: return null
    if(JellyfinTopLevelDestination.forContentKey(lastEntry.contentKey) == null) return null

    return TopLevelDestinationScene(
      entry = lastEntry,
      previousEntries = entries.dropLast(1),
      onTopLevelDestinationSelected = onTopLevelDestinationSelected,
    )
  }
}
