package com.eygraber.jellyfin.services.player

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.random.Random
import kotlin.test.Test

class AudioQueueTest {
  @Test
  fun empty_queue_has_no_current_track() {
    val queue = AudioQueue()
    queue.currentTrack.shouldBeNull()
    queue.isEmpty shouldBe true
  }

  @Test
  fun replace_with_empty_clears_queue_but_preserves_modes() {
    val queue = AudioQueue()
      .setRepeatMode(RepeatMode.All)
      .replace(newTracks = tracks(3))

    val cleared = queue.replace(newTracks = emptyList())

    cleared.tracks shouldBe emptyList()
    cleared.currentIndex shouldBe -1
    cleared.repeatMode shouldBe RepeatMode.All
  }

  @Test
  fun replace_coerces_out_of_range_start_index_to_zero() {
    val queue = AudioQueue().replace(newTracks = tracks(3), startIndex = 99)
    queue.currentIndex shouldBe 0
  }

  @Test
  fun replace_negative_start_index_coerces_to_zero() {
    val queue = AudioQueue().replace(newTracks = tracks(3), startIndex = -5)
    queue.currentIndex shouldBe 0
  }

  // -- next / previous: linear order, RepeatMode.Off ----------------------

  @Test
  fun next_advances_through_queue() {
    val queue = AudioQueue().replace(newTracks = tracks(3), startIndex = 0)

    val second = queue.next().shouldNotBeNull()
    second.currentIndex shouldBe 1

    val third = second.next().shouldNotBeNull()
    third.currentIndex shouldBe 2
  }

  @Test
  fun next_at_end_with_repeat_off_returns_null() {
    val queue = AudioQueue().replace(newTracks = tracks(3), startIndex = 2)
    queue.next().shouldBeNull()
  }

  @Test
  fun previous_at_start_with_repeat_off_is_no_op() {
    val queue = AudioQueue().replace(newTracks = tracks(3), startIndex = 0)
    val result = queue.previous()
    result.currentIndex shouldBe 0
  }

  @Test
  fun previous_moves_back_through_queue() {
    val queue = AudioQueue().replace(newTracks = tracks(3), startIndex = 2)
    queue.previous().currentIndex shouldBe 1
    queue.previous().previous().currentIndex shouldBe 0
  }

  // -- RepeatMode.All wraps -----------------------------------------------

  @Test
  fun next_at_end_with_repeat_all_wraps_to_first() {
    val queue = AudioQueue()
      .setRepeatMode(RepeatMode.All)
      .replace(newTracks = tracks(3), startIndex = 2)

    val wrapped = queue.next().shouldNotBeNull()
    wrapped.currentIndex shouldBe 0
  }

  @Test
  fun previous_at_start_with_repeat_all_wraps_to_last() {
    val queue = AudioQueue()
      .setRepeatMode(RepeatMode.All)
      .replace(newTracks = tracks(3), startIndex = 0)

    queue.previous().currentIndex shouldBe 2
  }

  // -- RepeatMode.One -----------------------------------------------------

  @Test
  fun on_track_ended_with_repeat_one_stays_on_same_track() {
    val queue = AudioQueue()
      .setRepeatMode(RepeatMode.One)
      .replace(newTracks = tracks(3), startIndex = 1)

    val ended = queue.onTrackEnded().shouldNotBeNull()
    ended.currentIndex shouldBe 1
  }

  @Test
  fun manual_next_under_repeat_one_still_advances() {
    val queue = AudioQueue()
      .setRepeatMode(RepeatMode.One)
      .replace(newTracks = tracks(3), startIndex = 1)

    val next = queue.next().shouldNotBeNull()
    next.currentIndex shouldBe 2
  }

  @Test
  fun manual_next_at_end_under_repeat_one_stops() {
    val queue = AudioQueue()
      .setRepeatMode(RepeatMode.One)
      .replace(newTracks = tracks(3), startIndex = 2)

    queue.next().shouldBeNull()
  }

  @Test
  fun on_track_ended_under_repeat_all_wraps() {
    val queue = AudioQueue()
      .setRepeatMode(RepeatMode.All)
      .replace(newTracks = tracks(3), startIndex = 2)

    val wrapped = queue.onTrackEnded().shouldNotBeNull()
    wrapped.currentIndex shouldBe 0
  }

  @Test
  fun on_track_ended_under_repeat_off_at_end_is_null() {
    val queue = AudioQueue().replace(newTracks = tracks(3), startIndex = 2)
    queue.onTrackEnded().shouldBeNull()
  }

  // -- skipTo -------------------------------------------------------------

  @Test
  fun skip_to_valid_index() {
    val queue = AudioQueue().replace(newTracks = tracks(5), startIndex = 0)
    queue.skipTo(3).currentIndex shouldBe 3
  }

  @Test
  fun skip_to_out_of_range_is_no_op() {
    val queue = AudioQueue().replace(newTracks = tracks(3), startIndex = 1)
    queue.skipTo(99).currentIndex shouldBe 1
    queue.skipTo(-1).currentIndex shouldBe 1
  }

  // -- shuffle ------------------------------------------------------------

  @Test
  fun setting_shuffle_off_when_already_off_is_no_op() {
    val queue = AudioQueue().replace(newTracks = tracks(3))
    queue.setShuffleMode(ShuffleMode.Off) shouldBe queue
  }

  @Test
  fun enabling_shuffle_pins_current_index_first() {
    val queue = AudioQueue().replace(newTracks = tracks(5), startIndex = 2)

    val shuffled = queue.setShuffleMode(ShuffleMode.On, random = Random(seed = 42L))

    shuffled.shuffleMode shouldBe ShuffleMode.On
    shuffled.shuffleOrder.first() shouldBe 2
    shuffled.shuffleOrder.size shouldBe 5
    shuffled.shuffleOrder shouldContainExactlyInAnyOrder listOf(0, 1, 2, 3, 4)
  }

  @Test
  fun shuffle_order_is_a_permutation_of_track_indices() {
    val queue = AudioQueue()
      .replace(newTracks = tracks(10), startIndex = 0)
      .setShuffleMode(ShuffleMode.On, random = Random(seed = 1L))

    queue.shuffleOrder shouldContainExactlyInAnyOrder (0..9).toList()
  }

  @Test
  fun next_under_shuffle_follows_shuffle_order() {
    val queue = AudioQueue()
      .replace(newTracks = tracks(4), startIndex = 0)
      .setShuffleMode(ShuffleMode.On, random = Random(seed = 7L))

    // Verify that next/previous traverses the shuffle order, not the canonical 0,1,2,3.
    val expectedOrder = queue.shuffleOrder
    var current = queue
    val visited = mutableListOf(current.currentIndex)
    while(true) {
      current = current.next() ?: break
      visited += current.currentIndex
    }
    visited shouldContainExactly expectedOrder
  }

  @Test
  fun next_at_end_of_shuffle_order_with_repeat_all_wraps_to_shuffle_first() {
    val queue = AudioQueue()
      .setRepeatMode(RepeatMode.All)
      .replace(newTracks = tracks(4), startIndex = 0)
      .setShuffleMode(ShuffleMode.On, random = Random(seed = 7L))
      .copy(currentIndex = queue4LastShuffleIndex(seed = 7L))

    val next = queue.next().shouldNotBeNull()
    next.currentIndex shouldBe queue.shuffleOrder.first()
  }

  @Test
  fun disabling_shuffle_keeps_current_track() {
    val queue = AudioQueue()
      .replace(newTracks = tracks(5), startIndex = 0)
      .setShuffleMode(ShuffleMode.On, random = Random(seed = 99L))

    // Move forward in shuffle order so currentIndex differs from the canonical 0.
    val advanced = queue.next().shouldNotBeNull()
    val keptCurrentIndex = advanced.currentIndex

    val unshuffled = advanced.setShuffleMode(ShuffleMode.Off)

    unshuffled.shuffleMode shouldBe ShuffleMode.Off
    unshuffled.shuffleOrder shouldBe emptyList()
    unshuffled.currentIndex shouldBe keptCurrentIndex
  }

  @Test
  fun shuffle_with_single_track_yields_zero() {
    val order = AudioQueue.generateShuffleOrder(size = 1, pinnedIndex = 0)
    order shouldBe listOf(0)
  }

  @Test
  fun shuffle_with_zero_size_yields_empty() {
    val order = AudioQueue.generateShuffleOrder(size = 0, pinnedIndex = 0)
    order shouldBe emptyList()
  }

  @Test
  fun shuffle_pinned_index_out_of_range_coerces() {
    val order = AudioQueue.generateShuffleOrder(
      size = 3,
      pinnedIndex = 99,
      random = Random(seed = 0L),
    )
    order.first() shouldBe 2
    order shouldContainExactlyInAnyOrder listOf(0, 1, 2)
  }

  // -- append -------------------------------------------------------------

  @Test
  fun append_to_empty_queue_starts_a_new_queue() {
    val queue = AudioQueue().append(moreTracks = tracks(3))
    queue.tracks.size shouldBe 3
    queue.currentIndex shouldBe 0
  }

  @Test
  fun append_keeps_current_track_intact() {
    val queue = AudioQueue()
      .replace(newTracks = tracks(3), startIndex = 1)
      .append(moreTracks = tracks(prefix = "extra-", count = 2))

    queue.tracks.size shouldBe 5
    queue.currentIndex shouldBe 1
    queue.currentTrack?.id shouldBe "track-1"
  }

  @Test
  fun append_under_shuffle_inserts_into_upcoming_portion() {
    val queue = AudioQueue()
      .replace(newTracks = tracks(3), startIndex = 0)
      .setShuffleMode(ShuffleMode.On, random = Random(seed = 5L))
      .append(moreTracks = tracks(prefix = "extra-", count = 2), random = Random(seed = 5L))

    // After append the shuffle order must still be a permutation of all track indices.
    queue.shuffleOrder shouldContainExactlyInAnyOrder (0..4).toList()
    // Current shuffle position must be unchanged - the appended tracks go after it.
    queue.shuffleOrder.indexOf(queue.currentIndex) shouldBe 0
  }

  // -- helpers ------------------------------------------------------------

  private fun tracks(count: Int, prefix: String = "track-"): List<AudioTrack> =
    List(count) { i ->
      AudioTrack(
        id = "$prefix$i",
        streamUrl = "https://example.test/$prefix$i.mp3",
        title = "Title $i",
        artist = "Artist",
      )
    }

  /**
   * Helper for [next_at_end_of_shuffle_order_with_repeat_all_wraps_to_shuffle_first].
   * Mirrors how the shuffled order is generated to find the *last* shuffled index for
   * a 4-track queue with the given seed, so the test can position the cursor there
   * without relying on knowledge of the random sequence.
   */
  private fun queue4LastShuffleIndex(seed: Long): Int {
    val order = AudioQueue.generateShuffleOrder(size = 4, pinnedIndex = 0, random = Random(seed = seed))
    return order.last()
  }
}
