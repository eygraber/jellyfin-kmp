# Jellyfin CMP

## Replacing the jellyfin values

Run `./jellyfin_placeholders --project-name <PROJECT_NAME> --project-package <PROJECT_PACKAGE>` to replace
all the jellyfin values with your project's values.

You will get prompted to create a password for the prod keystore.
Pick a strong password and save it in your GitHub Actions secrets.

After it succeeds it will print your internal and prod ejson public keys. You'll probably want to
lookup the corresponding private key under `/opt/ejson/keys/<public key>` and save it in your GitHub Actions secrets.
After doing this it is a good idea to delete the prod key from your local machine, so that only CI can create prod builds.

After this you can delete `jellyfin_placeholders`.

## GitHub Actions Secrets

GitHub Actions needs the following secrets:

1. `EJSON_DEV_BUILD_PRIVATE_KEY`
2. `EJSON_PROD_BUILD_PRIVATE_KEY`

If you make use of the `nightly` workflow (it is disabled by default)
you'll also need to set the following secrets to the value of the prod keystore password:

1. `PROD_KEY_PASSWORD`
2. `PROD_KEYSTORE_PASSWORD`

If you use Firebase App Distribution you'll need to add the following secrets:

1. `FIREBASE_APP_DISTRIBUTION_DEV_KEY`

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
