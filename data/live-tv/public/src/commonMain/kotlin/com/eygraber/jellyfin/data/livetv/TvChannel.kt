package com.eygraber.jellyfin.data.livetv

/**
 * A live TV channel exposed by the Jellyfin server.
 *
 * Channels typically come from a tuner (HDHomeRun, Hauppauge, etc.) configured
 * on the server.
 */
data class TvChannel(
  val id: String,
  val name: String,
  /**
   * Channel number as displayed by the source (e.g. "5.1", "201"). Optional
   * because some sources don't expose channel numbers.
   */
  val number: String?,
  /**
   * Channel type as reported by the server (e.g. "Tv", "Radio").
   */
  val type: String?,
  /**
   * Image tag for the channel logo, if available. Combine with the server's
   * image URL builder to fetch the actual image.
   */
  val primaryImageTag: String?,
  /**
   * The currently airing program on this channel, if known.
   */
  val currentProgram: TvProgram? = null,
)
