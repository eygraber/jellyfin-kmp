plugins {
  alias(libs.plugins.conventionsComposeMultiplatform)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlin)
  alias(libs.plugins.kotlinJvm)
  alias(libs.plugins.ksp)
}

dependencies {
  implementation(projects.apps.shared)

  implementation(compose.desktop.currentOs)

  runtimeOnly(libs.kotlinx.coroutines.swing)

  ksp(libs.kotlinInject.anvilCompiler)
  ksp(libs.kotlinInject.compiler)
}

compose {
  desktop.application.mainClass = "template.app.TemplateDesktopAppKt"
}
