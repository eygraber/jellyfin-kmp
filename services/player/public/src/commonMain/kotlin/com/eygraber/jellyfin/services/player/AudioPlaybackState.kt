package com.eygraber.jellyfin.services.player

/**
 * Snapshot of the audio player at a point in time. Emitted via
 * [AudioPlayerService.audioState] so UI layers can render Now Playing,
 * mini-players, queue lists, and platform-native controls from a single source.
 *
 * The [queue] is always the canonical (un-shuffled) ordering of tracks. When
 * [shuffleMode] is [ShuffleMode.On] the active playback order is described by
 * [shuffleOrder], a permutation of indices into [queue]; otherwise the playback
 * order matches the queue itself. UI components that just need to render a list
 * should use [queue] and the `shuffleOrder` is primarily an implementation
 * detail surfaced for testability and platform integrations that need to render
 * the upcoming-tracks list.
 *
 * @property queue Canonical, un-shuffled list of tracks the player is working with.
 * @property currentIndex Index into [queue] of the currently-loaded track, or `-1`
 *   when the queue is empty.
 * @property shuffleOrder Permutation of `0..queue.lastIndex` describing the active
 *   playback order when [shuffleMode] is [ShuffleMode.On]. When shuffle is off
 *   this is empty.
 * @property shuffleMode Whether shuffle is currently engaged.
 * @property repeatMode Whether the queue should repeat on completion.
 * @property isPlaying `true` when audio is actively playing (not paused, not
 *   buffering-paused, not idle).
 * @property isBuffering `true` when the player is loading data and cannot yet play.
 * @property hasError `true` when the underlying player reported an error. UI should
 *   surface [errorMessage] when present.
 * @property errorMessage Human-readable error description, or `null` when there is
 *   no active error.
 * @property currentPositionMs Playback position within the current track.
 * @property durationMs Reported duration of the current track. `0L` until the player
 *   has determined the duration.
 * @property bufferedPositionMs How much of the current track has been buffered ahead
 *   of the playhead.
 */
data class AudioPlaybackState(
  val queue: List<AudioTrack> = emptyList(),
  val currentIndex: Int = -1,
  val shuffleOrder: List<Int> = emptyList(),
  val shuffleMode: ShuffleMode = ShuffleMode.Off,
  val repeatMode: RepeatMode = RepeatMode.Off,
  val isPlaying: Boolean = false,
  val isBuffering: Boolean = false,
  val hasError: Boolean = false,
  val errorMessage: String? = null,
  val currentPositionMs: Long = 0L,
  val durationMs: Long = 0L,
  val bufferedPositionMs: Long = 0L,
) {
  /** Convenience: the currently-loaded track, or `null` when the queue is empty. */
  val currentTrack: AudioTrack? get() = queue.getOrNull(currentIndex)

  /**
   * Progress through the current track as a fraction in `[0f, 1f]`. Returns `0f`
   * when [durationMs] is unknown (avoids divide-by-zero noise in UI).
   */
  val progress: Float
    get() = if(durationMs > 0L) {
      (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(minimumValue = 0f, maximumValue = 1f)
    }
    else {
      0f
    }

  companion object {
    /** Empty / idle state — used as the initial value of [AudioPlayerService.audioState]. */
    val Idle = AudioPlaybackState()
  }
}

/**
 * A track the audio player can play.
 *
 * Kept deliberately minimal — anything richer (album art URLs, codec details) lives
 * on the data-layer model. The service only needs an identifier, where to fetch the
 * audio bytes, and the metadata that drives system media controls (Now Playing on
 * iOS, MediaSession on Android, etc.).
 *
 * @property id Stable identifier for the track (typically the Jellyfin item ID).
 *   Used to correlate progress reporting and to detect "is this the same track
 *   already loaded?" across queue updates.
 * @property streamUrl URL the player should pull audio bytes from. Resolved by the
 *   data layer (direct play / direct stream / transcode) before the track reaches
 *   this service.
 * @property title Track title, surfaced in system media controls.
 * @property artist Primary artist string, surfaced in system media controls.
 * @property album Album name. May be `null` for non-album items (singles, podcast
 *   episodes).
 * @property artworkUrl URL to album/track artwork for system media controls.
 * @property durationMs Known duration of the track from server metadata. Optional
 *   because the player will report its own duration once it's loaded the source.
 * @property playSessionId Server-assigned session ID for progress reporting. Empty
 *   string when the track wasn't created from a server playback-info call (e.g.
 *   tests).
 */
data class AudioTrack(
  val id: String,
  val streamUrl: String,
  val title: String,
  val artist: String,
  val album: String? = null,
  val artworkUrl: String? = null,
  val durationMs: Long? = null,
  val playSessionId: String = "",
)

/** Whether playback should pull from a shuffled order or the canonical queue order. */
enum class ShuffleMode {
  Off,
  On,
}

/**
 * How the player should behave at the boundaries of the queue.
 */
enum class RepeatMode {
  /** Stop on the last track; "previous" at the start of the queue is a no-op. */
  Off,

  /** When a track ends, restart it. Manual skip ([AudioPlayerService.next] /
   *  [AudioPlayerService.previous]) still moves to the adjacent track.
   */
  One,

  /** Wrap from the last track back to the first (and vice-versa for "previous"). */
  All,
}
