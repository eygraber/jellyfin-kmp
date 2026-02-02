# Template

Kotlin Multiplatform (KMP) / Compose Multiplatform (CMP) app template.

## Quick Reference

### Key Commands

| Command                                     | Purpose                                 |
|---------------------------------------------|-----------------------------------------|
| `./check`                                   | Run all PR checks (lint, detekt, tests) |
| `./format`                                  | Auto-fix formatting with ktlint         |
| `./detekt`                                  | Run static analysis                     |
| `./gradlew testDebugUnitTest`               | Run unit tests                          |
| `./gradlew verifyPaparazziDebug`            | Run screenshot tests                    |
| `./gradlew recordPaparazziDebug`            | Record new screenshot baselines         |
| `./gradlew :apps:android:assembleDevDebug`  | Build debug APK                         |
| `.scripts/generate_module --feature=<Name>` | Generate new screen module              |

### Architecture

**Pattern**: MVI/VICE (ViewState, Intent, Compositor, Effects)

**Data Flow**: `Compositor -> ViewState -> View -> Intent -> Compositor`

**Module Structure**:
- `screens/<feature>/` - Screen modules with VICE components
- `data/<feature>/` - Data layer (public/impl/fake submodules)
- `domain/<feature>/` - Domain layer (public/impl submodules)
- `ui/<component>/` - Shared UI components
- `services/<service>/` - External library integrations

### Supported Platforms

- Android (primary)
- iOS
- Desktop (JVM)
- Web (WasmJs)

### Tech Stack

- **UI**: Compose Multiplatform (Material 3)
- **DI**: Metro
- **Async**: Kotlin Coroutines + Flow
- **Testing**: JUnit, Kotest assertions, Turbine, Paparazzi

## Self-Correction

If you do something undesirable or make a mistake during a session, add a rule to prevent it from happening again:
- General rules: Add to this file (`.claude/CLAUDE.md`)
- Topic-specific rules: Add to or create a file in `.claude/rules/`

## Documentation

Detailed documentation is in [.docs/](/.docs):
- [Architecture](/.docs/architecture) - VICE pattern, layers, navigation
- [Compose](/.docs/compose) - UI conventions, state management
- [Testing](/.docs/testing) - Testing strategies
- [Data](/.docs/data) - Repository pattern
- [DI](/.docs/di) - Metro scopes and modules
- [Workflow](/.docs/workflow) - Git, builds, quality tools
