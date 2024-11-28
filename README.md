# Template Android

## Replacing the template values

Run `./template_placeholders --project-name <PROJECT_NAME> --project-package <PROJECT_PACKAGE>` to replace
all the template values with your project's values.

You will get prompted to create a password for the prod keystore.
Pick a strong password and save it in your GitHub Actions secrets.

After it succeeds it will print your internal and prod ejson public keys. You'll probably want to
lookup the corresponding private key under `/opt/ejson/keys/<public key>` and save it in your GitHub Actions secrets.
After doing this it is a good idea to delete the prod key from your local machine, so that only CI can create prod builds.

After this you can delete `template_placeholders`.

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

## Building the app

There are two app flavors that are buildable in local environments;
`devDebug` and `devRelease` (prod builds can only be made on CI).

To install the `devDebug` flavor, you need to run `./gradlew :app:installDevDebug`,
and to install the `devRelease` flavor you need to run `./gradlew :app:installDevRelease`.

Note that it will be much faster to make a `devDebug` flavor, but some parts of the app may
appear to not run smoothly. This will not happen on release builds.

## Docs

[Arch](.docs/Arch.md)

[Code Quality](.docs/CodeQuality.md)

[Data](.docs/Data.md)

[Design](.docs/Design.md)

[Development](.docs/Development.md)

[DI](.docs/DI.md)

[Domain](.docs/Domain.md)

[Project Layout](.docs/ProjectLayout.md)

[Project Setup](.docs/ProjectSetup.md)

[Publishing](.docs/Publishing.md)

[Testing](.docs/Testing.md)

[UI](.docs/UI.md)

## Firebase

If you want to use Firebase you'll need to add your google-services.json content to both:

1. `app/src/dev/secrets.ejson`
2. `app/src/prod/secrets.ejson`

After doing so you'll need to run:

```shell
ejson encrypt app/src/dev/secrets.json
ejson encrypt app/src/prod/secrets.json
```

In `app/build.gradle.kts` there are several lines related to Firebase that are commented out. They need to be uncommented:

```kotlin
  // alias(libs.plugins.firebase.appDistribution)
  // alias(libs.plugins.firebase.crashlytics)
  // alias(libs.plugins.googleServices)

  // (this as ExtensionAware).configure<CrashlyticsExtension> {
  //   mappingFileUploadEnabled = false
  // }

  // firebaseAppDistribution {
  //   serviceCredentialsFile = rootProject.file("tmp/firebase_app_distribution_dev_cred").absolutePath
  //   artifactType = "APK"
  // }

  // decryptGoogleServicesJson(project)
```

## Misc

### Build Config

Generating BuildConfig is disabled by default. If a module needs it, add the following to its `build.gradle.kts` file to enable it (see the `app` module as an example):

```
buildFeatures {
  buildConfig = true
}
```
