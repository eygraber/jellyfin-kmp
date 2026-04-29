package com.eygraber.jellyfin.nav

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.SceneStrategyScope
import com.eygraber.jellyfin.screens.home.HomeKey
import com.eygraber.jellyfin.screens.movie.detail.MovieDetailKey
import com.eygraber.jellyfin.screens.root.RootKey
import com.eygraber.jellyfin.screens.search.SearchKey
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class TopLevelDestinationSceneStrategyTest {
  private val strategy = TopLevelDestinationSceneStrategy(
    onTopLevelDestinationSelected = {},
  )

  @Test
  fun `calculateScene - returns scene when last entry is HomeKey`() {
    val entries = listOf(navEntry(HomeKey))

    val scene = strategy.calculate(entries)

    scene.shouldBeInstanceOf<TopLevelDestinationScene>()
    scene.key shouldBe TopLevelDestinationScene.SCENE_KEY
    scene.entries.size shouldBe 1
    scene.previousEntries.shouldBeEmpty()
  }

  @Test
  fun `calculateScene - returns scene when last entry is SearchKey`() {
    val entries = listOf(navEntry(SearchKey))

    val scene = strategy.calculate(entries)

    scene.shouldBeInstanceOf<TopLevelDestinationScene>()
  }

  @Test
  fun `calculateScene - shares the same key across top-level destinations`() {
    val homeScene = strategy.calculate(listOf(navEntry(HomeKey)))
    val searchScene = strategy.calculate(listOf(navEntry(SearchKey)))

    homeScene?.key shouldBe searchScene?.key
  }

  @Test
  fun `calculateScene - returns null when last entry is non-top-level`() {
    val entries = listOf(
      navEntry(HomeKey),
      navEntry(MovieDetailKey(movieId = "id")),
    )

    val scene = strategy.calculate(entries)

    scene.shouldBeNull()
  }

  @Test
  fun `calculateScene - returns null when last entry is RootKey`() {
    val entries = listOf(navEntry(RootKey))

    val scene = strategy.calculate(entries)

    scene.shouldBeNull()
  }

  @Test
  fun `calculateScene - returns null when entries is empty`() {
    val entries = emptyList<NavEntry<NavKey>>()

    val scene = strategy.calculate(entries)

    scene.shouldBeNull()
  }

  @Test
  fun `calculateScene - sets previousEntries to all but last entry`() {
    val rootEntry = navEntry(RootKey)
    val entries = listOf(rootEntry, navEntry(HomeKey))

    val scene = strategy.calculate(entries)

    scene.shouldBeInstanceOf<TopLevelDestinationScene>()
    scene.previousEntries shouldBe listOf(rootEntry)
  }

  private fun TopLevelDestinationSceneStrategy.calculate(
    entries: List<NavEntry<NavKey>>,
  ) = with(this) { SceneStrategyScope<NavKey>().calculateScene(entries) }

  private fun navEntry(key: NavKey): NavEntry<NavKey> = NavEntry(
    key = key,
    content = {},
  )
}
