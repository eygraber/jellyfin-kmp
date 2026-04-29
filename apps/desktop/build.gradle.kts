import dev.zacsweers.metro.gradle.DiagnosticSeverity

plugins {
  alias(libs.plugins.conventionsComposeMultiplatform)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlin)
  alias(libs.plugins.kotlinJvm)
  alias(libs.plugins.metro)
}

dependencies {
  implementation(projects.apps.shared)

  implementation(compose.desktop.currentOs)

  runtimeOnly(libs.kotlinx.coroutines.swing)
}

compose {
  desktop.application.mainClass = "com.eygraber.jellyfin.app.JellyfinDesktopAppKt"
}

// ideally we'd make this an error so that we can know that we need to clean up the input
// but the key and navigator for screens tend to be unused, and we don't want to remove them
metro {
  unusedGraphInputsSeverity = DiagnosticSeverity.NONE
}
