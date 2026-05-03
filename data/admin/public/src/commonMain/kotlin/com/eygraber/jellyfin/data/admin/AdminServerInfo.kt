@file:Suppress("BooleanPropertyNaming")

package com.eygraber.jellyfin.data.admin

/**
 * Detailed server information available to admins.
 */
data class AdminServerInfo(
  val id: String,
  val serverName: String,
  val version: String,
  val operatingSystem: String?,
  val productName: String?,
  val hasPendingRestart: Boolean,
  val canSelfRestart: Boolean,
  val isShuttingDown: Boolean,
  val cachePath: String?,
  val logPath: String?,
)

/**
 * The slice of server configuration that admin UIs commonly edit.
 *
 * Many fields on the underlying server configuration DTO are legacy or
 * irrelevant to a KMP client, so this exposes only a curated subset.
 * Round-tripping unknown fields is the responsibility of the impl layer.
 */
data class AdminServerConfiguration(
  val serverName: String?,
  val preferredMetadataLanguage: String?,
  val metadataCountryCode: String?,
  val cachePath: String?,
  val metadataPath: String?,
  val minResumePct: Int,
  val maxResumePct: Int,
  val minResumeDurationSeconds: Int,
  val libraryMonitorDelay: Int,
  val quickConnectAvailable: Boolean,
  val enableMetrics: Boolean,
)
