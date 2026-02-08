package com.eygraber.jellyfin.data.playback

/**
 * Represents a playback session with all information needed
 * to start and manage playback of a media item.
 *
 * @property itemId The ID of the item being played.
 * @property playSessionId The server-assigned session ID for progress reporting.
 * @property mediaSource The selected media source for playback.
 * @property streamUrl The URL to stream the media from.
 * @property playMethod How the media will be played (DirectPlay, DirectStream, or Transcode).
 */
data class PlaybackSession(
  val itemId: String,
  val playSessionId: String,
  val mediaSource: PlaybackMediaSource,
  val streamUrl: String,
  val playMethod: PlayMethod,
)

/**
 * Represents a media source available for an item.
 *
 * @property id The media source ID.
 * @property name Display name for the media source.
 * @property container The container format (e.g., "mkv", "mp4").
 * @property bitrate The total bitrate in bits per second.
 * @property runtimeTicks The duration in ticks (1 tick = 100 nanoseconds).
 * @property canDirectPlay Whether the source supports direct play.
 * @property canDirectStream Whether the source supports direct streaming.
 * @property canTranscode Whether the source supports transcoding.
 * @property transcodingUrl Server-provided transcoding URL if applicable.
 * @property videoStreams Available video streams.
 * @property audioStreams Available audio streams.
 * @property subtitleStreams Available subtitle streams.
 */
data class PlaybackMediaSource(
  val id: String,
  val name: String?,
  val container: String?,
  val bitrate: Int?,
  val runtimeTicks: Long?,
  val canDirectPlay: Boolean,
  val canDirectStream: Boolean,
  val canTranscode: Boolean,
  val transcodingUrl: String?,
  val videoStreams: List<PlaybackMediaStream>,
  val audioStreams: List<PlaybackMediaStream>,
  val subtitleStreams: List<PlaybackMediaStream>,
)

/**
 * Represents a single media stream (video, audio, or subtitle track).
 *
 * @property index The stream index within the media source.
 * @property codec The codec used (e.g., "h264", "aac", "srt").
 * @property displayTitle Human-readable display title.
 * @property language Language code (e.g., "eng", "spa").
 * @property isDefault Whether this is the default stream for its type.
 * @property isExternal Whether this is an external stream (e.g., external subtitle file).
 * @property isForced Whether this stream is marked as forced.
 * @property width Video width in pixels (video streams only).
 * @property height Video height in pixels (video streams only).
 * @property bitRate Bitrate in bits per second.
 * @property channels Number of audio channels (audio streams only).
 * @property deliveryUrl URL for external subtitle delivery.
 */
data class PlaybackMediaStream(
  val index: Int,
  val codec: String?,
  val displayTitle: String?,
  val language: String?,
  val isDefault: Boolean,
  val isExternal: Boolean,
  val isForced: Boolean,
  val width: Int? = null,
  val height: Int? = null,
  val bitRate: Int? = null,
  val channels: Int? = null,
  val deliveryUrl: String? = null,
)

/**
 * How the media will be delivered to the player.
 */
enum class PlayMethod {
  /** Media is played directly from the original file without any processing. */
  DirectPlay,

  /** Media is streamed directly but may be remuxed (container changed). */
  DirectStream,

  /** Media is transcoded on the server before delivery. */
  Transcode,
}
