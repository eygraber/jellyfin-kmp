package com.eygraber.jellyfin.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import com.eygraber.jellyfin.screens.home.HomeKey
import com.eygraber.jellyfin.screens.search.SearchKey
import com.eygraber.jellyfin.ui.icons.JellyfinIcons
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import com.eygraber.jellyfin.ui.icons.Home as HomeIcon
import com.eygraber.jellyfin.ui.icons.Search as SearchIcon

/**
 * Top-level destinations rendered as items in the [JellyfinNavigationSuiteScaffold].
 *
 * A destination is considered top-level when it is reachable directly from the navigation suite
 * (bottom bar / rail / drawer) and should always reset the back stack to the destination's [key]
 * when selected.
 */
internal enum class JellyfinTopLevelDestination(
  val key: NavKey,
  val icon: ImageVector,
  val label: StringResource,
) {
  Home(
    key = HomeKey,
    icon = JellyfinIcons.HomeIcon,
    label = Res.string.nav_home,
  ),
  Search(
    key = SearchKey,
    icon = JellyfinIcons.SearchIcon,
    label = Res.string.nav_search,
  ),
  ;

  /**
   * The default `contentKey` for a [androidx.navigation3.runtime.NavEntry] built from this
   * destination's [key]. Vice's `viceEntry` derives the content key by calling `key.toString()`,
   * which matches `NavEntry`'s default; this property mirrors that derivation so callers that only
   * have access to a `NavEntry.contentKey` can still resolve the destination.
   */
  val contentKey: Any = key.toString()

  @Composable
  fun label(): String = stringResource(label)

  companion object {
    /**
     * Returns the [JellyfinTopLevelDestination] that owns the given [key], or `null` if the key
     * does not correspond to a top-level destination (e.g. detail or onboarding screens).
     */
    fun forKey(key: NavKey?): JellyfinTopLevelDestination? = entries.firstOrNull { it.key == key }

    /**
     * Returns the [JellyfinTopLevelDestination] whose [contentKey] matches [contentKey], or `null`
     * if no destination is associated with that content key.
     *
     * Useful when only an entry's `contentKey` is accessible (e.g. inside a custom `Scene`), since
     * `NavEntry.key` itself is not part of the public API.
     */
    fun forContentKey(contentKey: Any?): JellyfinTopLevelDestination? =
      entries.firstOrNull { it.contentKey == contentKey }
  }
}
