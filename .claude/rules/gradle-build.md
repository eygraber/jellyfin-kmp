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

### Documentation Reference
For complete patterns: .docs/workflow/builds.md and .docs/workflow/quality.md
