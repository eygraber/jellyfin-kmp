package com.eygraber.jellyfin.gradle

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.VariantOutputConfiguration.OutputType
import org.gradle.api.Project
import java.io.BufferedReader

fun ApplicationAndroidComponentsExtension.configureVersionCode(
  project: Project,
) = with(project) {
  onVariants(selector()) { variant ->
    val taskName = "get${variant.nameForTasks}VersionCode"

    val versionCodeFile = objects.fileProperty().convention(
      project.layout.buildDirectory.file("com/eygraber/jellyfin/app/build/${variant.name}/version_code.txt"),
    )

    val getVersionCodeTask = tasks.register(taskName) {
      group = "Versioning"
      description = "Gets the version code from the latest git release tag and writes it to a file"

      val tagsDir = rootProject.file(".git/refs/tags")

      inputs.dir(tagsDir)
      inputs.property("buildType", variant.buildType)
      outputs.file(versionCodeFile)

      doLast {
        val process = ProcessBuilder(
          "sh",
          "-c",
          "git tag -l | grep -E '^v[0-9]+\\.[0-9]+\\.[0-9]+-[0-9]+\$' | sort -Vr | head -n 1",
        ).start()

        val versionCode =
          process
            .inputStream
            .bufferedReader()
            .use(BufferedReader::readText)
            .split("-")
            .lastOrNull()
            ?.trim()
            ?.toIntOrNull()
            ?.let { versionCode ->
              val buildType = inputs.properties["buildType"]
              when(buildType) {
                "release" -> versionCode
                "debug" -> versionCode + 1
                else -> null
              }
            }
            ?.toString()

        versionCodeFile.get().asFile.apply {
          parentFile.mkdirs()
          writeText(
            requireNotNull(versionCode) {
              "Couldn't find the latest version code from a git release tag"
            }.also {
              println("Using versionCode $it")
            },
          )
        }
      }
    }

    val output = variant.outputs.single { it.outputType == OutputType.SINGLE }
    output.versionCode.set(
      getVersionCodeTask.flatMap {
        versionCodeFile.map { file ->
          file.asFile.readText().toInt()
        }
      },
    )
  }
}
