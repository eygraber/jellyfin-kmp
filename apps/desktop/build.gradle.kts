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
  desktop.application.mainClass = "template.app.TemplateDesktopAppKt"
}
