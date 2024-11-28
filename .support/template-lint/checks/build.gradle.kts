plugins {
  alias(libs.plugins.kotlinJvm)
  alias(libs.plugins.androidLint)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlin)
  alias(libs.plugins.dependencyAnalysis)
}

dependencies {
  compileOnly(libs.android.lint.api)
  testImplementation(libs.android.lint.api)
  testImplementation(libs.android.lint.test)
  testImplementation(libs.test.junit)
}

detekt {
  config.setFrom(files("../../../detekt.yml"))
}
