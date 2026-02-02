# Environment Setup

Setup for Template development.

## Git LFS

Binary files (screenshots) stored in Git LFS.

```bash
sudo apt install git-lfs  # or brew install git-lfs
git lfs install --local
git lfs pull
```

## Git Tags

Version calculated from tags. Fetch them:

```bash
git fetch --tags
```

## JDK

Project uses latest JDK supported by AGP/Kotlin/Gradle.

Version defined in `.github/.java-version`. Recommended: Azul/zulu distribution.

**Installation**:
- macOS: [Homebrew](https://brew.sh/)
- Linux: [SDKMan](https://sdkman.io/)
- Windows: Use WSL (native Windows has degraded performance)

Java target: 17 (controlled by `jvmTarget` in `gradle/libs.versions.toml`)

## Android SDK

If SDK not found, install manually:

```bash
wget -q https://dl.google.com/android/repository/commandlinetools-linux-13114758_latest.zip -O /tmp/tools.zip
unzip /tmp/tools.zip -d /tmp/tools
mkdir -p ~/Android/sdk/cmdline-tools/latest
mv /tmp/tools/cmdline-tools/* ~/Android/sdk/cmdline-tools/latest
rm -rf /tmp/tools /tmp/tools.zip

export ANDROID_SDK_ROOT="$HOME/Android/sdk"
export PATH="$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin"
export PATH="$PATH:$ANDROID_SDK_ROOT/platform-tools"

sdkmanager "platform-tools" "platforms;android-35" "build-tools;35.0.1"
yes | sdkmanager --licenses
```

## eJSON (Secrets)

Secrets managed with [eJSON](https://github.com/Shopify/ejson).

Get dev keypair from project maintainer. Store in `/opt/ejson/keys` or location specified by `EJSON_KEYDIR`.

Secrets in `secrets.ejson` files.

## Android Studio

Use [JetBrains Toolbox](https://www.jetbrains.com/toolbox-app/) for installation.

**Ubuntu prerequisites**:
```bash
sudo apt-get install libc6:i386 libncurses5:i386 libstdc++6:i386 lib32z1 libbz2-1.0:i386
```

**Configure**: Set Gradle JDK to `JAVA_HOME` in `Build, Execution, Deployment -> Build Tools -> Gradle`.

## AGP Compatibility

If you see "incompatible AGP version" error, update Android Studio. See [compatibility matrix](https://developer.android.com/build/releases/gradle-plugin#android_gradle_plugin_and_android_studio_compatibility).

## Version Override

For worktrees without `.git/refs/tags`, use [plugin override mechanisms](https://github.com/eygraber/release-tag-version-plugin?tab=readme-ov-file#overrides) to set versionCode/versionName.
