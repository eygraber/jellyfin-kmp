import com.eygraber.conventions.Env
import template.gradle.configureVersionCode
import template.gradle.disableProdLocally
import template.gradle.getInternalKeystorePassword

plugins {
  alias(libs.plugins.androidApp)
  alias(libs.plugins.androidCacheFix)
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.conventionsCompose)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlin)
  alias(libs.plugins.conventionsProjectCommon)
  // alias(libs.plugins.firebase.appDistribution)
  // alias(libs.plugins.firebase.crashlytics)
  // alias(libs.plugins.googleServices)
  alias(libs.plugins.ksp)
  // alias(libs.plugins.playStoreApi)
}

val internalKeystorePassword = getInternalKeystorePassword()

android {
  namespace = "template.app"

  compileSdk = libs.versions.android.sdk.compile.get().toInt()

  defaultConfig {
    applicationId = "template.app"
    targetSdk = libs.versions.android.sdk.target.get().toInt()
    minSdk = libs.versions.android.sdk.min.get().toInt()

    // versionCode is set by a task later in the file
    versionName = "0.0.1"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    multiDexEnabled = true
  }

  signingConfigs {
    named("debug") {
      storeFile = file("debug.keystore")
    }

    register("internal") {
      keyAlias = "template-internal"
      keyPassword = internalKeystorePassword
      storeFile = file("template-internal.keystore")
      storePassword = internalKeystorePassword
    }

    register("prod") {
      keyAlias = "template-prod"
      keyPassword = System.getenv("PROD_KEY_PASSWORD")
      storeFile = file("template.keystore")
      storePassword = System.getenv("PROD_KEYSTORE_PASSWORD")
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles.clear()
      proguardFiles += project.file("proguard-rules.pro")
    }

    debug {
      // overwrite the default signingConfig for debug builds (https://stackoverflow.com/a/29618981/691639)
      signingConfig = signingConfigs.named("internal").get()

      // always gets applied after the flavor's applicationIdSuffix
      applicationIdSuffix = ".debug"

      versionNameSuffix = "-DEBUG"

      isMinifyEnabled = false

      // (this as ExtensionAware).configure<CrashlyticsExtension> {
      //   mappingFileUploadEnabled = false
      // }
    }
  }

  flavorDimensions += "environment"

  productFlavors {
    register("dev") {
      dimension = "environment"

      applicationIdSuffix = ".dev"

      signingConfig = signingConfigs.named("internal").get()

      // com.google.firebase.appdistribution.gradle.firebaseAppDistribution {
      //   serviceCredentialsFile = rootProject.file("tmp/firebase_app_distribution_dev_cred").absolutePath
      //   artifactType = "APK"
      // }
    }

    register("prod") {
      dimension = "environment"

      signingConfig = signingConfigs.named("prod").get()
    }
  }

  lint {
    checkDependencies = true
    checkReleaseBuilds = false
  }

  buildFeatures {
    buildConfig = true
  }

  compileOptions {
    isCoreLibraryDesugaringEnabled = true

    val javaCompat = JavaVersion.toVersion(libs.versions.jvmTarget.get().toInt())
    sourceCompatibility = javaCompat
    targetCompatibility = javaCompat
  }

  packaging {
    resources {
      pickFirsts += "META-INF/*"
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }

  dependencies {
    coreLibraryDesugaring(libs.android.desugar)
  }
}

androidComponents {
  disableProdLocally(isCI = Env.isCI)
  configureVersionCode(project)
  // decryptGoogleServicesJson(project)
}

dependencies {
  implementation(projects.apps.shared)

  implementation(libs.androidx.activity)
  implementation(libs.androidx.activityCompose)
  implementation(libs.androidx.appCompat)

  implementation(libs.androidx.startup)

  implementation(libs.kotlinx.coroutines.core)
  runtimeOnly(libs.kotlinx.coroutines.android)

  ksp(libs.kotlinInject.anvilCompiler)
  ksp(libs.kotlinInject.compiler)
}

// credentials are set with the ANDROID_PUBLISHER_CREDENTIALS env var in nightly.yml
// play {
//   releaseStatus = ReleaseStatus.COMPLETED
//   track = "internal"
//   defaultToAppBundles = true
// }
