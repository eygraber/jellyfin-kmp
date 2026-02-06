@file:Suppress("BooleanPropertyNaming")

package com.eygraber.jellyfin.sdk.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SystemInfo(
  @SerialName("LocalAddress") val localAddress: String? = null,
  @SerialName("ServerName") val serverName: String? = null,
  @SerialName("Version") val version: String? = null,
  @SerialName("ProductName") val productName: String? = null,
  @SerialName("OperatingSystem") val operatingSystem: String? = null,
  @SerialName("Id") val id: String? = null,
  @SerialName("StartupWizardCompleted") val startupWizardCompleted: Boolean? = null,
  @SerialName("OperatingSystemDisplayName") val operatingSystemDisplayName: String? = null,
  @SerialName("PackageName") val packageName: String? = null,
  @SerialName("HasPendingRestart") val hasPendingRestart: Boolean = false,
  @SerialName("IsShuttingDown") val isShuttingDown: Boolean = false,
  @SerialName("SupportsLibraryMonitor") val supportsLibraryMonitor: Boolean = false,
  @SerialName("WebSocketPortNumber") val webSocketPortNumber: Int? = null,
  @SerialName("CanSelfRestart") val canSelfRestart: Boolean = false,
  @SerialName("CanLaunchWebBrowser") val canLaunchWebBrowser: Boolean = false,
  @SerialName("ProgramDataPath") val programDataPath: String? = null,
  @SerialName("WebPath") val webPath: String? = null,
  @SerialName("ItemsByNamePath") val itemsByNamePath: String? = null,
  @SerialName("CachePath") val cachePath: String? = null,
  @SerialName("LogPath") val logPath: String? = null,
  @SerialName("InternalMetadataPath") val internalMetadataPath: String? = null,
  @SerialName("TranscodingTempPath") val transcodingTempPath: String? = null,
  @SerialName("HasUpdateAvailable") val hasUpdateAvailable: Boolean = false,
)
