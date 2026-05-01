---
paths:
  - "**/*.gradle.kts"
  - "gradle/**"
---

Use Gradle build system with Kotlin DSL (build.gradle.kts)
Use existing convention plugins to configure the plugins that are used
Dependencies are defined in gradle/libs.versions.toml
Clean project: ./gradlew clean
Build debug APK: ./gradlew :app:assembleDebug
Run unit tests: ./gradlew testDebugUnitTest
Run snapshot tests: ./gradlew verifyPaparazziDebug
Format code: ./format
Run detekt: ./detekt
Run Android lint: ./gradlew :app:lintRelease
Check dependency usage: ./gradlew buildHealth
Check licenses: ./gradlew licenseeAndroidRelease
Run full checks: ./check
If Compose Resources are used in a module, Android resources needs to be enabled (see .claude/rules/examples/android-resources.gradle.kts for an example)

### Documentation Reference
For complete patterns: .docs/workflow/builds.md and .docs/workflow/quality.md
