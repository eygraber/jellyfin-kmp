package com.eygraber.jellyfin.gradle

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

fun KotlinMultiplatformExtension.createCmpGroup() {
  @OptIn(ExperimentalKotlinGradlePluginApi::class)
  applyDefaultHierarchyTemplate {
    common {
      group("cmp") {
        withIos()
        withJvm()
        withWasmJs()
      }
    }
  }
}
