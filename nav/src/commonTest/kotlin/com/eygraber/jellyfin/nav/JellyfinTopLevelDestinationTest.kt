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

  @Test
  fun `forContentKey - returns Home for HomeKey contentKey`() {
    JellyfinTopLevelDestination.forContentKey(HomeKey.toString()) shouldBe
      JellyfinTopLevelDestination.Home
  }

  @Test
  fun `forContentKey - returns Search for SearchKey contentKey`() {
    JellyfinTopLevelDestination.forContentKey(SearchKey.toString()) shouldBe
      JellyfinTopLevelDestination.Search
  }

  @Test
  fun `forContentKey - returns null for non top-level contentKeys`() {
    JellyfinTopLevelDestination.forContentKey(RootKey.toString()).shouldBeNull()
    JellyfinTopLevelDestination.forContentKey(WelcomeKey.toString()).shouldBeNull()
    JellyfinTopLevelDestination.forContentKey(MovieDetailKey(movieId = "id").toString())
      .shouldBeNull()
  }

  @Test
  fun `forContentKey - returns null when contentKey is null`() {
    JellyfinTopLevelDestination.forContentKey(null).shouldBeNull()
  }
}
