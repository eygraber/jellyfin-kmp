package com.eygraber.jellyfin.nav

import com.eygraber.jellyfin.screens.home.HomeKey
import com.eygraber.jellyfin.screens.movie.detail.MovieDetailKey
import com.eygraber.jellyfin.screens.root.RootKey
import com.eygraber.jellyfin.screens.search.SearchKey
import com.eygraber.jellyfin.screens.welcome.WelcomeKey
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class JellyfinTopLevelDestinationTest {
  @Test
  fun `forKey - returns Home for HomeKey`() {
    JellyfinTopLevelDestination.forKey(HomeKey) shouldBe JellyfinTopLevelDestination.Home
  }

  @Test
  fun `forKey - returns Search for SearchKey`() {
    JellyfinTopLevelDestination.forKey(SearchKey) shouldBe JellyfinTopLevelDestination.Search
  }

  @Test
  fun `forKey - returns null for non top-level destinations`() {
    JellyfinTopLevelDestination.forKey(RootKey).shouldBeNull()
    JellyfinTopLevelDestination.forKey(WelcomeKey).shouldBeNull()
    JellyfinTopLevelDestination.forKey(MovieDetailKey(movieId = "id")).shouldBeNull()
  }

  @Test
  fun `forKey - returns null when key is null`() {
    JellyfinTopLevelDestination.forKey(null).shouldBeNull()
  }
}
