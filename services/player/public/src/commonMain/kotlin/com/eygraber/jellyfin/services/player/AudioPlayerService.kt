package com.eygraber.jellyfin.services.player

import kotlinx.coroutines.flow.StateFlow

/**
 * Platform-agnostic audio player service.
 *
 * Coordinates a queue of [AudioTrack]s with shuffle and repeat semantics, exposing
 * a reactive [audioState] for UI observation. Per-platform implementations (Android
 * [`MediaSession`][androidx.media3.session.MediaSession], iOS `AVAudioSession`,
 * Desktop tray, Web Media Session API) wire this interface to native audio output
 * and system media controls.
 *
 * Unlike [VideoPlayerService] which deals with a single stream at a time, the audio
 * player owns the queue: callers push a list of tracks and the service handles
 * intra-queue navigation ([next] / [previous]) along with [shuffleMode] and
 * [repeatMode] state.
 *
 * Lifecycle is tied to the application (not a screen) so background playback can
 * survive UI navigation. Platform implementations are expected to be application-scoped
 * singletons.
 */
interface AudioPlayerService {
  /**
   * Reactive snapshot of the player's current state — current track, queue,
   * playback position, shuffle/repeat mode, etc.
   */
  val audioState: StateFlow<AudioPlaybackState>

  /**
   * Replaces the queue and starts playback at [startIndex] of the given list.
   *
   * If the new queue is empty the player is released. The current shuffle / repeat
   * settings are preserved across queue changes.
   *
   * @param tracks Ordered list of tracks to play.
   * @param startIndex Index into [tracks] to begin playback from. Coerced to the
   *   valid range; a value outside `0..tracks.lastIndex` falls back to `0`.
   * @param startPositionMs Position within the starting track to begin from.
   */
  fun setQueue(
    tracks: List<AudioTrack>,
    startIndex: Int = 0,
    startPositionMs: Long = 0L,
  )

  /**
   * Appends [tracks] to the end of the queue. Does not change the currently-playing
   * track. No-op if the queue is empty (use [setQueue] to start a new queue).
   */
  fun addToQueue(tracks: List<AudioTrack>)

  /** Starts or resumes playback of the current track. */
  fun play()

  /** Pauses playback without releasing resources. */
  fun pause()

  /**
   * Toggles between [play] and [pause] based on the current state. A no-op when no
   * queue is loaded.
   */
  fun playPause()

  /**
   * Seeks within the current track.
   *
   * @param positionMs Target position in milliseconds, coerced into the track's
   *   `[0, durationMs]` range by the implementation.
   */
  fun seekTo(positionMs: Long)

  /**
   * Advances to the next track per the current [shuffleMode] and [repeatMode].
   *
   * Behavior:
   *  - In [RepeatMode.One] this still advances (replicating the standard "skip
   *    overrides repeat-one" UX), and the next track plays under repeat-one once it
   *    starts.
   *  - In [ShuffleMode.On] the next track is the next entry in the active shuffled
   *    order.
   *  - At the end of the queue: under [RepeatMode.All] wraps to the first track;
   *    under [RepeatMode.Off] the player stops on the last track.
   */
  fun next()

  /**
   * Moves to the previous track. If the current position is past
   * [PREVIOUS_RESTART_THRESHOLD_MS] within the track this seeks back to `0L`
   * instead (matching standard media-player UX).
   *
   * At the start of the queue: under [RepeatMode.All] wraps to the last track;
   * under [RepeatMode.Off] this is a no-op.
   */
  fun previous()

  /** Jumps directly to [index] in the current queue. Out-of-range indexes are ignored. */
  fun skipTo(index: Int)

  /**
   * Updates the shuffle mode. Switching to [ShuffleMode.On] generates a fresh
   * shuffled order pinned around the current track; switching to [ShuffleMode.Off]
   * restores the original queue order with the current track preserved.
   */
  fun setShuffleMode(mode: ShuffleMode)

  /** Updates the repeat mode. */
  fun setRepeatMode(mode: RepeatMode)

  /** Releases all underlying resources and clears the queue. */
  fun release()

  companion object {
    /**
     * Pressing "previous" while past this point in the current track restarts the
     * track instead of skipping back. Mirrors typical music-player UX so that a
     * mis-press doesn't lose the listener's place.
     */
    const val PREVIOUS_RESTART_THRESHOLD_MS: Long = 3_000L
  }
}
