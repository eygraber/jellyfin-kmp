# Jellyfin KMP

## Building the Android app

There are two app flavors that are buildable in local environments;
`devDebug` and `devRelease` (prod builds can only be made on CI).

To install the `devDebug` flavor, you need to run `./gradlew :apps:android:installDevDebug`,
and to install the `devRelease` flavor you need to run `./gradlew :apps:android:installDevRelease`.

Note that it will be much faster to make a `devDebug` flavor, but some parts of the app may
appear to not run smoothly. This will not happen on release builds.

## Building the Desktop app

To run the desktop app locally:

```bash
./gradlew :apps:desktop:run
```

To build a distributable desktop app:

```bash
./gradlew :apps:desktop:installDist
```

To build and run a release distribution:

```bash
./gradlew :apps:desktop:runRelease
```

## Building the iOS app

To build the shared framework for iOS:

```bash
./gradlew :apps:ios:assembleJellyfinKtXCFramework
```

Note: The actual iOS app is built using Xcode. The above command builds the Kotlin framework that is used by the iOS project.

## Building the Web app

To build the web app for development:

```bash
./gradlew :apps:web:wasmJsBrowserDevelopmentWebpack
```

To build the web app for production:

```bash
./gradlew :apps:web:wasmJsBrowserProductionWebpack
```

To run the web app in development mode with a webpack dev server:

```bash
./gradlew :apps:web:wasmJsBrowserDevelopmentRun
```

## Documentation

All technical documentation is in the [.docs](.docs) directory:

| Topic                | Location                                         |
|----------------------|--------------------------------------------------|
| Architecture         | [.docs/architecture/](/.docs/architecture)       |
| Compose              | [.docs/compose/](/.docs/compose)                 |
| Testing              | [.docs/testing/](/.docs/testing)                 |
| Dependency Injection | [.docs/di/](/.docs/di)                           |
| Data Layer           | [.docs/data/](/.docs/data)                       |
| Domain Layer         | [.docs/domain/](/.docs/domain)                   |
| Domain Concepts      | [.docs/domain-concepts/](/.docs/domain-concepts) |
| Development Workflow | [.docs/workflow/](/.docs/workflow)               |

Additional standalone docs:
- [Project Layout](/.docs/ProjectLayout.md) - Module dependency graph
- [Publishing](/.docs/Publishing.md) - App publishing process (todo)

## AI Agent Resources

- [.claude/](/.claude) - AI rules, agents, and skills
- [firebender.json](/firebender.json) - Project conventions and documentation for AI coding agents
- [AGENTS.md](/AGENTS.md) - Generic entry point for AI coding agents
- [.docs/](/.docs) - Technical documentation
- [.gemini/styleguide.md](/.gemini/styleguide.md) - Styleguide for Gemini code reviews on GitHub

## Misc

### Build Config

Generating BuildConfig is disabled by default. If a module needs it, add the following to its `build.gradle.kts` file to enable it (see the `app` module as an example):

```
buildFeatures {
  buildConfig = true
}
```
