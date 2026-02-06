@file:Suppress("BooleanPropertyNaming")

package com.eygraber.jellyfin.sdk.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthenticationResult(
  @SerialName("User") val user: UserDto? = null,
  @SerialName("SessionInfo") val sessionInfo: SessionInfoDto? = null,
  @SerialName("AccessToken") val accessToken: String? = null,
  @SerialName("ServerId") val serverId: String? = null,
)

@Serializable
data class UserDto(
  @SerialName("Name") val name: String? = null,
  @SerialName("ServerId") val serverId: String? = null,
  @SerialName("Id") val id: String? = null,
  @SerialName("HasPassword") val hasPassword: Boolean = false,
  @SerialName("HasConfiguredPassword") val hasConfiguredPassword: Boolean = false,
  @SerialName("HasConfiguredEasyPassword") val hasConfiguredEasyPassword: Boolean = false,
  @SerialName("EnableAutoLogin") val enableAutoLogin: Boolean? = null,
  @SerialName("Policy") val policy: UserPolicy? = null,
  @SerialName("Configuration") val configuration: UserConfiguration? = null,
)

@Serializable
data class UserPolicy(
  @SerialName("IsAdministrator") val isAdministrator: Boolean = false,
  @SerialName("IsHidden") val isHidden: Boolean = false,
  @SerialName("IsDisabled") val isDisabled: Boolean = false,
  @SerialName("EnableUserPreferenceAccess") val enableUserPreferenceAccess: Boolean = true,
  @SerialName("EnableRemoteAccess") val enableRemoteAccess: Boolean = true,
)

@Serializable
data class UserConfiguration(
  @SerialName("PlayDefaultAudioTrack") val playDefaultAudioTrack: Boolean = true,
  @SerialName("SubtitleLanguagePreference") val subtitleLanguagePreference: String? = null,
  @SerialName("DisplayMissingEpisodes") val displayMissingEpisodes: Boolean = false,
  @SerialName("SubtitleMode") val subtitleMode: String? = null,
  @SerialName("EnableLocalPassword") val enableLocalPassword: Boolean = false,
  @SerialName("HidePlayedInLatest") val hidePlayedInLatest: Boolean = true,
  @SerialName("RememberAudioSelections") val rememberAudioSelections: Boolean = true,
  @SerialName("RememberSubtitleSelections") val rememberSubtitleSelections: Boolean = true,
)

@Serializable
data class SessionInfoDto(
  @SerialName("Id") val id: String? = null,
  @SerialName("UserId") val userId: String? = null,
  @SerialName("UserName") val userName: String? = null,
  @SerialName("ServerId") val serverId: String? = null,
)
