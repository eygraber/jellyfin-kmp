# Development Workflow

Workflow documentation for Template development.

## Contents

- [setup.md](setup.md) - Environment setup guide
- [builds.md](builds.md) - Build commands and variants
- [quality.md](quality.md) - Code quality tools and checks
- [git.md](git.md) - Git workflow and conventions

## Quick Reference

| Task               | Command                                           |
|--------------------|---------------------------------------------------|
| Build debug APK    | `./gradlew :apps:android:assembleDevDebug`        |
| Run all tests      | `./gradlew testDebugUnitTest`                     |
| Run all checks     | `./check`                                         |
| Format code        | `./format`                                        |
| Run detekt         | `./detekt`                                        |
| Screenshot tests   | `./gradlew recordPaparazziDebug` / `verifyPaparazziDebug` |

## Entry Points

- **New developer?** Start with [setup.md](setup.md)
- **Building?** See [builds.md](builds.md)
- **PR checks failing?** Check [quality.md](quality.md)
