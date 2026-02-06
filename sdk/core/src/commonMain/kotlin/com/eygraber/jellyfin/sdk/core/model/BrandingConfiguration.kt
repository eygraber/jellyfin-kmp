package com.eygraber.jellyfin.sdk.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BrandingConfiguration(
  @SerialName("LoginDisclaimer") val loginDisclaimer: String? = null,
  @SerialName("CustomCss") val customCss: String? = null,
  @SerialName("SplashscreenEnabled") val isSplashscreenEnabled: Boolean = false,
)
