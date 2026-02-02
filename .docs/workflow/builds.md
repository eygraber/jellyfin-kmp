# Build Commands

Gradle commands for Template.

## Build Variants

| Variant       | Purpose                        |
|---------------|--------------------------------|
| `devDebug`    | Development with debug symbols |
| `devRelease`  | Development optimized          |
| `prodRelease` | Production release             |

## Common Commands

### Building

```bash
# Debug APK
./gradlew :apps:android:assembleDevDebug

# Release APK
./gradlew :apps:android:assembleDevRelease

# Install on device
./gradlew :apps:android:installDevDebug

# Desktop app
./gradlew :apps:desktop:run

# Web app (WasmJs)
./gradlew :apps:web:wasmJsBrowserDevelopmentRun
```

### Testing

```bash
# All unit tests
./gradlew testDebugUnitTest

# Module-specific tests
./gradlew :screens:welcome:testDebugUnitTest

# Screenshot tests - verify
./gradlew verifyPaparazziDebug

# Screenshot tests - record new
./gradlew recordPaparazziDebug
```

### Quality

```bash
# Android lint
./gradlew :apps:android:lintDevRelease

# Detekt
./detekt

# Konsist
./gradlew :konsist:test

# Dependency analysis
./gradlew buildHealth

# All checks
./check
./check --lite  # Subset
```

### Formatting

```bash
# Auto-format
./format

# Check only (no changes)
./format --no-format
```

### Cleaning

```bash
./gradlew clean
```

## Module Creation

```bash
.scripts/generate_module --feature=<FeatureName>
```

Creates complete screen module with VICE, DI, nav, tests.

## IDE

Android Studio recommended. Use [JetBrains Toolbox](https://www.jetbrains.com/toolbox-app/) for installation.

Configure Gradle JDK to `JAVA_HOME` in settings.
