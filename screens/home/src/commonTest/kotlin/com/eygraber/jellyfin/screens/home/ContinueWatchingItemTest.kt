package com.eygraber.jellyfin.screens.home

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ContinueWatchingItemTest {
  @Test
  fun movie_displayName_shows_name() {
    val item = createItem(
      name = "The Dark Knight",
      type = "Movie",
    )
    item.displayName shouldBe "The Dark Knight"
  }

  @Test
  fun movie_subtitle_is_null() {
    val item = createItem(
      name = "The Dark Knight",
      type = "Movie",
    )
    item.subtitle.shouldBeNull()
  }

  @Test
  fun episode_with_series_and_numbers_shows_formatted_displayName() {
    val item = createItem(
      name = "Ozymandias",
      type = "Episode",
      seriesName = "Breaking Bad",
      parentIndexNumber = 5,
      indexNumber = 14,
    )
    item.displayName shouldBe "Breaking Bad - S5:E14"
  }

  @Test
  fun episode_with_series_and_numbers_shows_episode_name_as_subtitle() {
    val item = createItem(
      name = "Ozymandias",
      type = "Episode",
      seriesName = "Breaking Bad",
      parentIndexNumber = 5,
      indexNumber = 14,
    )
    item.subtitle shouldBe "Ozymandias"
  }

  @Test
  fun episode_with_series_but_no_numbers_shows_series_dash_name() {
    val item = createItem(
      name = "Pilot",
      type = "Episode",
      seriesName = "Lost",
    )
    item.displayName shouldBe "Lost - Pilot"
  }

  @Test
  fun episode_with_series_but_no_numbers_shows_name_as_subtitle() {
    val item = createItem(
      name = "Pilot",
      type = "Episode",
      seriesName = "Lost",
    )
    item.subtitle shouldBe "Pilot"
  }

  @Test
  fun episode_with_only_season_number_shows_series_dash_name() {
    val item = createItem(
      name = "Episode 1",
      type = "Episode",
      seriesName = "Some Show",
      parentIndexNumber = 3,
    )
    item.displayName shouldBe "Some Show - Episode 1"
  }

  private fun createItem(
    name: String,
    type: String,
    seriesName: String? = null,
    seasonName: String? = null,
    indexNumber: Int? = null,
    parentIndexNumber: Int? = null,
    progressPercent: Float = 0.5F,
  ) = ContinueWatchingItem(
    id = "test-id",
    name = name,
    type = type,
    seriesName = seriesName,
    seasonName = seasonName,
    indexNumber = indexNumber,
    parentIndexNumber = parentIndexNumber,
    progressPercent = progressPercent,
    imageUrl = "https://example.com/image.jpg",
    backdropImageUrl = null,
  )
}
