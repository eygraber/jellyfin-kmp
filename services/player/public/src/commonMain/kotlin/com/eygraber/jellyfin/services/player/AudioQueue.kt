package com.eygraber.jellyfin.services.player

import kotlin.random.Random

/**
 * Pure, immutable representation of an audio queue plus its shuffle / repeat state.
 *
 * Lives in `services/player/public` so the per-platform [AudioPlayerService]
 * implementations can share the queue arithmetic (next, previous, shuffle order
 * generation, repeat handling) without each re-deriving it. Every state-changing
 * method returns a new [AudioQueue] — never mutates `this` — so the implementations
 * can hand the result to a `MutableStateFlow` and trust that observers see a clean
 * before/after pair.
 *
 * The queue tracks two orderings:
 *  - [tracks] is the canonical ordering as inserted by the caller.
 *  - [shuffleOrder] is a permutation of `tracks` indices used when [shuffleMode]
 *    is [ShuffleMode.On]. When shuffle is off this is empty (so `==` comparison
 *    of two un-shuffled queues stays cheap and correct).
 *
 * "Position" everywhere refers to an index into [tracks] (the canonical position),
 * not into [shuffleOrder]. This is what UI surfaces care about ("track 5 of 12") and
 * what the data layer correlates with item IDs.
 *
 * @property tracks Canonical list of tracks.
 * @property currentIndex Index into [tracks] of the currently active track, or `-1`
 *   when the queue is empty.
 * @property shuffleMode Whether [shuffleOrder] should be consulted for next/previous.
 * @property repeatMode Boundary behavior when reaching the start or end of the queue.
 * @property shuffleOrder When [shuffleMode] is on: a permutation of
 *   `tracks.indices` describing playback order. When off: empty.
 */
data class AudioQueue(
  val tracks: List<AudioTrack> = emptyList(),
  val currentIndex: Int = -1,
  val shuffleMode: ShuffleMode = ShuffleMode.Off,
  val repeatMode: RepeatMode = RepeatMode.Off,
  val shuffleOrder: List<Int> = emptyList(),
) {
  init {
    require(tracks.isEmpty() || currentIndex in tracks.indices) {
      "currentIndex $currentIndex out of bounds for queue of size ${tracks.size}"
    }
    require(shuffleMode == ShuffleMode.Off || shuffleOrder.size == tracks.size) {
      "shuffleOrder must be a permutation of tracks indices when shuffle is on"
    }
  }

  /** The currently active track, or `null` when the queue is empty. */
  val currentTrack: AudioTrack? get() = tracks.getOrNull(currentIndex)

  /** `true` when there are no tracks loaded. */
  val isEmpty: Boolean get() = tracks.isEmpty()

  /**
   * Replaces the queue with [newTracks] and positions the cursor at [startIndex].
   *
   * Out-of-range start indexes coerce to `0` (matching the [AudioPlayerService.setQueue]
   * contract). Shuffle order is regenerated when [shuffleMode] is on so the new
   * track set has a fresh permutation.
   *
   * @param random Source of randomness used when re-generating the shuffle order.
   *   Tests pass a seeded [Random] so shuffle behavior is reproducible.
   */
  fun replace(
    newTracks: List<AudioTrack>,
    startIndex: Int = 0,
    random: Random = Random.Default,
  ): AudioQueue {
    if(newTracks.isEmpty()) {
      return AudioQueue(
        shuffleMode = shuffleMode,
        repeatMode = repeatMode,
      )
    }

    val safeStart = if(startIndex in newTracks.indices) startIndex else 0
    val newShuffleOrder = when(shuffleMode) {
      ShuffleMode.Off -> emptyList()
      ShuffleMode.On -> generateShuffleOrder(
        size = newTracks.size,
        pinnedIndex = safeStart,
        random = random,
      )
    }

    return AudioQueue(
      tracks = newTracks,
      currentIndex = safeStart,
      shuffleMode = shuffleMode,
      repeatMode = repeatMode,
      shuffleOrder = newShuffleOrder,
    )
  }

  /**
   * Appends [moreTracks] to the end of the queue.
   *
   * The current track is preserved. When shuffle is on the appended tracks are
   * spliced into a random position *after* the current shuffle position so they
   * play in the upcoming portion of the queue but in a randomized order.
   */
  fun append(moreTracks: List<AudioTrack>, random: Random = Random.Default): AudioQueue {
    if(moreTracks.isEmpty()) return this
    if(tracks.isEmpty()) return replace(newTracks = moreTracks, random = random)

    val newTracks = tracks + moreTracks
    val newShuffleOrder = when(shuffleMode) {
      ShuffleMode.Off -> emptyList()
      ShuffleMode.On -> {
        val appendedIndices = (tracks.size until newTracks.size).toList().shuffled(random)
        // Splice the new (already-shuffled) appended indices in after the current
        // shuffle position so they play in the upcoming portion of the shuffle order.
        val currentShufflePos = shuffleOrder.indexOf(currentIndex)
          .coerceAtLeast(minimumValue = 0)
        shuffleOrder.subList(fromIndex = 0, toIndex = currentShufflePos + 1) +
          appendedIndices +
          shuffleOrder.subList(fromIndex = currentShufflePos + 1, toIndex = shuffleOrder.size)
      }
    }

    return copy(
      tracks = newTracks,
      shuffleOrder = newShuffleOrder,
    )
  }

  /**
   * Computes the queue after a "next" press.
   *
   * - Empty queue → unchanged.
   * - Last track + [RepeatMode.Off] → `null` to signal the player should stop.
   *   ([RepeatMode.One] also stops on manual next-at-end since "skip overrides
   *   repeat-one" and there's no next track to skip to.)
   * - Last track + [RepeatMode.All] → wraps to the first track (or the first entry
   *   in [shuffleOrder] when shuffled).
   * - Otherwise → advances by one step in the active order.
   *
   * Returns `null` to signal "playback should end" — the caller is expected to
   * release the underlying player and surface a stopped state.
   */
  fun next(): AudioQueue? {
    if(isEmpty) return this

    val nextIndex = nextIndexOrNull() ?: return null
    return copy(currentIndex = nextIndex)
  }

  /**
   * Computes the queue after a "previous" press.
   *
   * - Empty queue → unchanged.
   * - First track + [RepeatMode.Off] → unchanged (caller may still seek to 0L).
   * - First track + [RepeatMode.All] → wraps to the last track.
   * - Otherwise → moves back one step in the active order.
   *
   * Note: this method only handles the index transition. The "if past the restart
   * threshold, just seek to 0L" behavior described on
   * [AudioPlayerService.PREVIOUS_RESTART_THRESHOLD_MS] is the player implementation's
   * responsibility — it knows the current playback position.
   */
  fun previous(): AudioQueue {
    if(isEmpty) return this

    val previousIndex = previousIndexOrNull() ?: return this
    return copy(currentIndex = previousIndex)
  }

  /**
   * Computes the queue after a track ends naturally (player reached the end of
   * the source).
   *
   * Differs from [next] only in [RepeatMode.One]: a natural end under repeat-one
   * stays on the same track so the player can replay it.
   *
   * Returns `null` when the queue is exhausted ([RepeatMode.Off] at the last track).
   */
  fun onTrackEnded(): AudioQueue? = when(repeatMode) {
    RepeatMode.One -> this
    RepeatMode.Off, RepeatMode.All -> next()
  }

  /**
   * Jumps directly to [index] in the canonical [tracks] list. Out-of-range indexes
   * leave the queue unchanged.
   */
  fun skipTo(index: Int): AudioQueue {
    if(index !in tracks.indices) return this
    return copy(currentIndex = index)
  }

  /**
   * Toggles shuffle on/off.
   *
   * Turning shuffle on generates a fresh permutation pinned around the current
   * track (so playback continues from where the listener was). Turning shuffle
   * off restores canonical order with the current track preserved (so the listener
   * doesn't suddenly hear a different song).
   *
   * Calling with the current mode is a no-op (avoids re-generating shuffle order
   * unnecessarily, which would change the upcoming-tracks list).
   */
  fun setShuffleMode(mode: ShuffleMode, random: Random = Random.Default): AudioQueue {
    if(mode == shuffleMode) return this

    val newShuffleOrder = when(mode) {
      ShuffleMode.Off -> emptyList()
      ShuffleMode.On -> if(tracks.isEmpty()) {
        emptyList()
      }
      else {
        generateShuffleOrder(size = tracks.size, pinnedIndex = currentIndex, random = random)
      }
    }

    return copy(
      shuffleMode = mode,
      shuffleOrder = newShuffleOrder,
    )
  }

  /** Updates the repeat mode. The queue contents are unchanged. */
  fun setRepeatMode(mode: RepeatMode): AudioQueue = copy(repeatMode = mode)

  /**
   * Computes the next index per the current [shuffleMode] and [repeatMode], or
   * `null` when the queue is exhausted.
   *
   * Visible for testing — production code should call [next] (which preserves
   * the rest of the queue state).
   */
  internal fun nextIndexOrNull(): Int? {
    if(isEmpty) return null
    val order = activeOrder()
    val pos = order.indexOf(currentIndex)
    if(pos < 0) return null

    return when {
      pos < order.lastIndex -> order[pos + 1]
      repeatMode == RepeatMode.All -> order.first()
      else -> null
    }
  }

  /**
   * Computes the previous index per the current [shuffleMode] and [repeatMode],
   * or `null` when "previous" should be a no-op.
   */
  internal fun previousIndexOrNull(): Int? {
    if(isEmpty) return null
    val order = activeOrder()
    val pos = order.indexOf(currentIndex)
    if(pos < 0) return null

    return when {
      pos > 0 -> order[pos - 1]
      repeatMode == RepeatMode.All -> order.last()
      else -> null
    }
  }

  /**
   * Returns the active playback order — the shuffled permutation when shuffle is
   * on, or a 0..lastIndex sequence when it's off.
   */
  private fun activeOrder(): List<Int> = when(shuffleMode) {
    ShuffleMode.On -> shuffleOrder
    ShuffleMode.Off -> tracks.indices.toList()
  }

  companion object {
    /**
     * Generates a shuffle permutation of `0 until size` with [pinnedIndex] placed
     * at the front, so playback can continue immediately from the current track
     * after enabling shuffle.
     *
     * Behavior:
     *  - `size <= 1` → returns `[0]` (or empty when `size == 0`); a one-track queue
     *    has nothing to shuffle.
     *  - Otherwise → the first element is `pinnedIndex` and the remainder is a
     *    random permutation of the other indices.
     */
    fun generateShuffleOrder(
      size: Int,
      pinnedIndex: Int,
      random: Random = Random.Default,
    ): List<Int> {
      if(size <= 0) return emptyList()
      if(size == 1) return listOf(0)
      val safePinned = pinnedIndex.coerceIn(minimumValue = 0, maximumValue = size - 1)
      val others = (0 until size).filter { it != safePinned }.shuffled(random)
      return listOf(safePinned) + others
    }
  }
}
