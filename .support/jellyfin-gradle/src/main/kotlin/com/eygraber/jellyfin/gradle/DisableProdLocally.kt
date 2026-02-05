package com.eygraber.jellyfin.gradle

import com.android.build.api.variant.ApplicationAndroidComponentsExtension

fun ApplicationAndroidComponentsExtension.disableProdLocally(
  isCI: Boolean,
) {
  beforeVariants(selector().withFlavor("environment" to "prod")) { variant ->
    variant.enable = variant.buildType != "debug" && isCI
  }
}
