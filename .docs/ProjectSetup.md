# Template Android Setup

## Git LFS

We store some binary files such as screenshot test images in [Git LFS].
After installing Git LFS and cloning this repo, run `git lfs install --local`, then `git lfs pull`.

## JDK

The JDK needs to be present at the `JAVA_HOME` environment variable.

The project aims to use the latest version of the JDK that is supported by the Android Gradle Plugin (AGP), Kotlin,
and Gradle. This will usually be the latest stable version of the JDK, but there could be a delay between when it is
available and when the tools support it.

The version used for building the app is defined in the `.java-version` file. The Azul / zulu distribution is preferred.
If your local JDK is a lower version than that or uses a different distribution, Gradle will download and use a JDK
that matches the requirements. Gradle itself will be run using your local JDK, so it is always better to make sure that
you have a local version >= to the one defined in `.java-version`.

[Homebrew] is a good option for managing JDK versions on macos.
[SDKMan] is a good option for Linux.

It is not recommended to use Windows for development due to degraded performance of the build tools.
Using WSL can help with that though.

The Java compatibility target is set to 17, which currently works best for Android. It is controlled by the
`jvmTarget` version in the version catalog (`gradle/libs.versions.toml`).

## Android Studio

The easiest way to get the project used is to install Android Studio, and the easiest way to install it is to use
[Jetbrains Toolbox]. It handles downloading, installing, and updating.
It also allows you to install different versions of Android Studio side by side (e.g. stable, beta, and alpha).

Follow the setup steps, open this project, and you should be good to go.

If you're running on Ubuntu you may need to install the following libraries before installing Android Studio:

```bash
sudo apt-get install libc6:i386 libncurses5:i386 libstdc++6:i386 lib32z1 libbz2-1.0:i386
```

The JDK that Android Studio uses to build the app is configured via `Gradle JDK` in the Android Studio settings
(`Build, Execution, Deployment -> Build Tools -> Gradle`). It is a very good idea to switch it to `JAVA_HOME`.

### Android Gradle Plugin (AGP)

AGP and Android Studio try to maintain compatibility in a way that newer versions of Android Studio
work with older versions of AGP, but newer versions of AGP don't work with older versions of Android Studio.

If you get an error that looks like:

> The project is using an incompatible version (AGP X.X.X) of the Android Gradle plugin.
> Latest supported version is AGP Y.Y.Y

it is likely that you need to use a newer version of Android Studio. Google [publishes a matrix]
showing which versions of AGP work with specific versions of Android Studio.

## eJSON

[eJSON] is being used to manage repo secrets so that they can't get accidentally leaked.

You can get the dev keypair from Eli. Make sure that it is stored in /opt/ejson/keys
(or a specific location using the EJSON_KEYDIR environment variable). Prod can't be built locally,
so there is no keypair for that.

Secrets are stored in `secrets.ejson` files.

[eJSON]: https://github.com/Shopify/ejson
[Git LFS]: https://git-lfs.com/
[Homebrew]: https://brew.sh/
[Jetbrains Toolbox]: https://www.jetbrains.com/toolbox-app/
[publishes a matrix]: https://developer.android.com/build/releases/gradle-plugin#android_gradle_plugin_and_android_studio_compatibility
[SDKMan]: https://sdkman.io/
