@file:Suppress("UnusedImports")

package template.gradle

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.eygraber.ejson.gradle.EjsonDecryptTask
import com.google.gms.googleservices.GoogleServicesTask
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import java.io.File
import java.util.Locale

fun ApplicationAndroidComponentsExtension.decryptGoogleServicesJson(
  project: Project,
) = with(project) {
  onVariants { variant ->
    val flavorName = variant.flavorName
    val buildTypeName = variant.buildType?.replaceFirstChar { it.uppercase(Locale.ROOT) }
    if(flavorName != null && buildTypeName != null) {
      val taskName = "copy${variant.nameForTasks}GoogleServicesJson"
      val copyGoogleServicesJsonTask = tasks.register<EjsonDecryptTask>(taskName) {
        secretsFile = layout.projectDirectory.file("src/$flavorName/secrets.ejson")
        userSuppliedPrivateKey = if(flavorName == "prod") {
          System.getenv("EJSON_PROD_BUILD_PRIVATE_KEY")
        }
        else {
          System.getenv("EJSON_DEV_BUILD_PRIVATE_KEY")
        }
        outputKey = "google_services"
        outputFilename = "google-services.json"
        output = layout.buildDirectory.dir("generated/template/$flavorName$buildTypeName")
      }

      tasks.withType<GoogleServicesTask>().configureEach {
        if(variant.nameForTasks in name) {
          googleServicesJsonFiles = copyGoogleServicesJsonTask.flatMap { t ->
            t.output.zip(t.outputFilename) { directory, filename ->
              File(directory.asFile, filename)
            }.map(::listOf)
          }
        }
      }
    }
  }
}
