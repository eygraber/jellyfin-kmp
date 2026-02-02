# Code Quality

Tools and checks for code quality.

## Quick Commands

```bash
./check           # Full suite
./check --lite    # Subset
./format          # Auto-format code
./detekt          # Static analysis
```

## Android Lint

Run on app module (transitively runs on all modules):

```bash
./gradlew :apps:android:lintDevRelease
```

### Custom Lint Rules

Located in `template-lint` included build.

## Detekt

Static code analyzer. Two modes:

### No Type Resolution
```bash
./gradlew detekt
```

### With Type Resolution
```bash
./gradlew detektDebugSourceSet
```

### Combined Script
```bash
./detekt
./detekt --continue  # Report all issues
```

## ktlint

Code formatting.

```bash
./format          # Fix violations
./format --no-format  # Check only
```

Configured via `.editorconfig`. IDE settings at `.idea/codeStyles`.

## Konsist

Architectural rules enforcement.

```bash
./gradlew :konsist:test
```

Tests in `:konsist` module enforce patterns like:
- Package conventions
- Naming rules
- Dependency rules

## Dependency Analysis

[DAGP](https://github.com/autonomousapps/dependency-analysis-gradle-plugin) checks:

```bash
./gradlew buildHealth
```

Verifies:
- Correct configurations (`implementation` vs `api`)
- No unused dependencies
- No transitive API leaks

## Pre-commit

Consider running before commits:

```bash
./format && ./detekt && ./gradlew testDebugUnitTest
```

Or use the full check:

```bash
./check
```
