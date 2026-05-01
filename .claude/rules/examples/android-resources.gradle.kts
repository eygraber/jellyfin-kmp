plugins {
  alias(libs.plugins.conventionsAndroidKmpLibrary)
  alias(libs.plugins.conventionsComposeMultiplatform)
}

val pkg = "com.eygraber.jellyfin.eample"

compose {
  resources {
    packageOfResClass = pkg
  }
}

kotlin {
  defaultKmpTargets(
    project = project,
    androidNamespace = pkg,
  )

  android {
    androidResources.enable = true
  }
}
