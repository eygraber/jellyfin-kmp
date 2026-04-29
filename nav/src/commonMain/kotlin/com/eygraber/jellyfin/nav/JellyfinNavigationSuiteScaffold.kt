package com.eygraber.jellyfin.nav

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
 * This composable assumes it is only rendered when a top-level destination is active; the
 * containing [TopLevelDestinationScene] is responsible for that gating. Callers pass the active
 * [currentTopLevelDestination] (which drives the selected highlight) and a click handler that
 * receives the chosen destination.
 *
 * Keeping this composable inside a scene that is shared across top-level destinations means the
 * navigation suite stays mounted while inner content swaps, avoiding the visual flash of the
 * scaffold animating between destinations.
 */
@Composable
internal fun JellyfinNavigationSuiteScaffold(
  currentTopLevelDestination: JellyfinTopLevelDestination?,
  onSelectTopLevelDestination: (JellyfinTopLevelDestination) -> Unit,
  modifier: Modifier = Modifier,
  windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
  content: @Composable () -> Unit,
) {
  // Labels must be resolved in a composable scope; navigationSuiteItems is a non-composable
  // builder lambda.
  val labels = JellyfinTopLevelDestination.entries.associateWith { it.label() }

  NavigationSuiteScaffold(
    navigationSuiteItems = {
      JellyfinTopLevelDestination.entries.forEach { destination ->
        val label = labels.getValue(destination)
        item(
          selected = destination == currentTopLevelDestination,
          onClick = { onSelectTopLevelDestination(destination) },
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
    layoutType = jellyfinNavigationSuiteType(windowAdaptiveInfo),
    content = content,
  )
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
