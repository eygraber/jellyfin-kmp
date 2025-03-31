plugins {
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlin)
  alias(libs.plugins.kotlinJvm)
}

dependencies {
  testImplementation(libs.kotlinInject.anvilRuntime)
  testImplementation(libs.kotlinInject.anvilRuntimeOptional)
  testImplementation(libs.kotlinInject.runtime)

  testImplementation(libs.test.junit5.api)
  testImplementation(platform(libs.test.junit5.bom))
  testRuntimeOnly(libs.test.junit5.engine)
  testRuntimeOnly(libs.test.junit5.launcher)
  testImplementation(libs.test.konsist)
  testImplementation(libs.test.kotest.assertions.core)
  testImplementation(libs.test.kotest.assertions.shared)
}

tasks.withType<Test> {
  useJUnitPlatform()
  outputs.upToDateWhen { false }
}
