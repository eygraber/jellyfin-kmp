package com.eygraber.jellyfin.nav

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.window.core.layout.WindowSizeClass

/**
 * Navigation suite scaffold for the Jellyfin app.
 *
 * Displays a [NavigationSuiteScaffold] around [content] that automatically switches between bottom
 * navigation, navigation rail, and navigation drawer based on the current window size:
 *
 * | Window Width | Navigation Style       |
 * |--------------|------------------------|
 * | < 600dp      | Bottom navigation bar  |
 * | 600 - 840dp  | Navigation rail        |
 * | > 840dp      | Permanent nav drawer   |
 *
 * The navigation suite is only shown when the current top-most entry on [backStack] is a known
 * [JellyfinTopLevelDestination]. Onboarding (root, welcome) and detail screens render full-bleed
 * without a navigation surface.
 *
 * Tapping a navigation item replaces the entire back stack with the destination's key. This keeps
 * top-level switches predictable on all form factors and prevents stacking duplicate destinations.
 */
@Composable
internal fun JellyfinNavigationSuiteScaffold(
  backStack: NavBackStack<NavKey>,
  modifier: Modifier = Modifier,
  windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
  content: @Composable () -> Unit,
) {
  val currentTopLevelDestination = remember(backStack.lastOrNull()) {
    JellyfinTopLevelDestination.forKey(backStack.lastOrNull())
  }

  val navigationSuiteType = if(currentTopLevelDestination == null) {
    NavigationSuiteType.None
  }
  else {
    jellyfinNavigationSuiteType(windowAdaptiveInfo)
  }

  // Labels must be resolved in a composable scope; navigationSuiteItems is a non-composable
  // builder lambda.
  val labels = JellyfinTopLevelDestination.entries.associateWith { it.label() }

  NavigationSuiteScaffold(
    navigationSuiteItems = {
      JellyfinTopLevelDestination.entries.forEach { destination ->
        val label = labels.getValue(destination)
        item(
          selected = destination == currentTopLevelDestination,
          onClick = { onTopLevelDestinationSelected(backStack, destination) },
          icon = {
            Icon(
              imageVector = destination.icon,
              // The adjacent Text label is read by accessibility services, so the icon's
              // contentDescription is intentionally null to avoid duplicate announcements.
              contentDescription = null,
            )
          },
          label = { Text(label) },
        )
      }
    },
    modifier = modifier,
    layoutType = navigationSuiteType,
    content = content,
  )
}

private fun onTopLevelDestinationSelected(
  backStack: NavBackStack<NavKey>,
  destination: JellyfinTopLevelDestination,
) {
  if(backStack.lastOrNull() == destination.key) return
  backStack.clear()
  backStack.add(destination.key)
}

/**
 * Resolves the active [NavigationSuiteType] using the project's adaptive breakpoints.
 *
 * Material's recommended `navigationSuiteType` defaults to `ShortNavigationBar*` and
 * `WideNavigationRailCollapsed`. The project's design language calls for a permanent navigation
 * drawer at expanded widths and a navigation rail at medium widths, so we compute the type
 * directly from [WindowSizeClass] breakpoints (600dp, 840dp).
 */
private fun jellyfinNavigationSuiteType(
  windowAdaptiveInfo: WindowAdaptiveInfo,
): NavigationSuiteType {
  val sizeClass = windowAdaptiveInfo.windowSizeClass
  return when {
    sizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) ->
      NavigationSuiteType.NavigationDrawer

    sizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) ->
      NavigationSuiteType.NavigationRail

    else -> NavigationSuiteType.NavigationBar
  }
}
