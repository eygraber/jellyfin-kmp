plugins {
  alias(libs.plugins.conventionsAndroidLibrary)
  alias(libs.plugins.conventionsComposeMultiplatform)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlinMultiplatform)
  alias(libs.plugins.conventionsProjectCommon)
  alias(libs.plugins.dependencyAnalysis)
}

android {
  namespace = "template.ui.material"
}

kotlin {
  defaultKmpTargets(
    project = project,
  )

  sourceSets {
    androidMain.dependencies {
      implementation(libs.androidx.annotation)
      implementation(libs.compose.edgeToEdgePreview)
    }

    commonMain.dependencies {
      implementation(projects.composePreview)
      implementation(projects.ui.icons)

      implementation(compose.animation)
      implementation(compose.foundation)
      // needed until CMP 1.9.1 is released
      implementation("org.jetbrains.compose.material3:material3:1.9.0-beta06")
      api(compose.runtime)
      implementation(compose.ui)

      implementation(libs.vice.nav)
    }
  }
}
