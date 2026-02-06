package com.eygraber.jellyfin.screens.home

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class CollectionTypeTest {
  @Test
  fun movies_maps_correctly() {
    CollectionType.fromApiValue("movies") shouldBe CollectionType.Movies
  }

  @Test
  fun tvshows_maps_correctly() {
    CollectionType.fromApiValue("tvshows") shouldBe CollectionType.TvShows
  }

  @Test
  fun music_maps_correctly() {
    CollectionType.fromApiValue("music") shouldBe CollectionType.Music
  }

  @Test
  fun musicvideos_maps_correctly() {
    CollectionType.fromApiValue("musicvideos") shouldBe CollectionType.MusicVideos
  }

  @Test
  fun boxsets_maps_correctly() {
    CollectionType.fromApiValue("boxsets") shouldBe CollectionType.Collections
  }

  @Test
  fun playlists_maps_correctly() {
    CollectionType.fromApiValue("playlists") shouldBe CollectionType.Playlists
  }

  @Test
  fun livetv_maps_correctly() {
    CollectionType.fromApiValue("livetv") shouldBe CollectionType.LiveTv
  }

  @Test
  fun photos_maps_correctly() {
    CollectionType.fromApiValue("photos") shouldBe CollectionType.Photos
  }

  @Test
  fun homevideos_maps_correctly() {
    CollectionType.fromApiValue("homevideos") shouldBe CollectionType.HomeVideos
  }

  @Test
  fun books_maps_correctly() {
    CollectionType.fromApiValue("books") shouldBe CollectionType.Books
  }

  @Test
  fun case_insensitive_mapping() {
    CollectionType.fromApiValue("Movies") shouldBe CollectionType.Movies
    CollectionType.fromApiValue("TVSHOWS") shouldBe CollectionType.TvShows
  }

  @Test
  fun unknown_value_maps_to_unknown() {
    CollectionType.fromApiValue("somethingCustom") shouldBe CollectionType.Unknown
  }

  @Test
  fun null_value_maps_to_unknown() {
    CollectionType.fromApiValue(null) shouldBe CollectionType.Unknown
  }
}
