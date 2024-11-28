package template.gradle

import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

fun Project.generateKotlinInjectCompanionExtensions() {
  extensions.configure<KspExtension> {
    arg("me.tatarka.inject.generateCompanionExtensions", "true")
  }
}
