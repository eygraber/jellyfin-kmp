package com.eygraber.jellyfin.nav

import androidx.compose.material3.adaptive.Posture
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.window.core.layout.WindowSizeClass
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class JellyfinNavigationSuiteScaffoldTest {
  @Test
  fun `compact width picks NavigationBar`() {
    jellyfinNavigationSuiteType(adaptiveInfoFor(widthDp = 400)) shouldBe NavigationSuiteType.NavigationBar
  }

  @Test
  fun `width just below medium breakpoint picks NavigationBar`() {
    jellyfinNavigationSuiteType(adaptiveInfoFor(widthDp = 599)) shouldBe NavigationSuiteType.NavigationBar
  }

  @Test
  fun `medium breakpoint picks NavigationRail`() {
    jellyfinNavigationSuiteType(adaptiveInfoFor(widthDp = 600)) shouldBe NavigationSuiteType.NavigationRail
  }

  @Test
  fun `medium width picks NavigationRail`() {
    jellyfinNavigationSuiteType(adaptiveInfoFor(widthDp = 720)) shouldBe NavigationSuiteType.NavigationRail
  }

  @Test
  fun `width just below expanded breakpoint picks NavigationRail`() {
    jellyfinNavigationSuiteType(adaptiveInfoFor(widthDp = 839)) shouldBe NavigationSuiteType.NavigationRail
  }

  @Test
  fun `expanded breakpoint picks NavigationDrawer`() {
    jellyfinNavigationSuiteType(adaptiveInfoFor(widthDp = 840)) shouldBe NavigationSuiteType.NavigationDrawer
  }

  @Test
  fun `expanded width picks NavigationDrawer`() {
    jellyfinNavigationSuiteType(adaptiveInfoFor(widthDp = 1440)) shouldBe NavigationSuiteType.NavigationDrawer
  }

  private fun adaptiveInfoFor(widthDp: Int): WindowAdaptiveInfo =
    WindowAdaptiveInfo(
      windowSizeClass = WindowSizeClass(minWidthDp = widthDp, minHeightDp = TEST_HEIGHT_DP),
      windowPosture = Posture(),
    )

  companion object {
    private const val TEST_HEIGHT_DP = 800
  }
}
