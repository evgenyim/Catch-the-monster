# Android

The Android app runs the browser game completely offline in Android System WebView. The package retains the original application ID, `com.example.user.catchthemonster`.

## Build

Requirements: JDK 17, Android SDK platform 36, SDK build tools 35.0.0, and `ANDROID_HOME` pointing to your SDK. Android Studio can install these SDK components. The checked-in Gradle wrapper downloads Gradle 8.11.1 and verifies its SHA-256 checksum.

```sh
cd android
./gradlew :app:assembleDebug :app:lintDebug
```

On Windows, use `gradlew.bat` instead of `./gradlew`.

The installable development APK is `app/build/outputs/apk/debug/app-debug.apk`. With a connected device and USB debugging enabled:

```sh
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

The app supports Android 8.0 (API 26) and later and needs an enabled, up-to-date Android System WebView. Android Gradle Plugin 8.10.1, compile/target SDK 36 and Java 17 are pinned. `syncWebAssets` packages the current `../web/` folder automatically; no npm step, external server or duplicate game sources are required for the Android build.

`./gradlew :app:assembleRelease` creates an **unsigned** release APK. For distribution, sign it using your own Android signing key. No signing keys or credentials belong in this repository. Updating an installation of the original app requires the original signing key; a debug APK cannot replace an app signed by a different key.

## Runtime

- All assets load from the intercepted local origin `https://appassets.androidplatform.net/game/`. No network, storage or advertising permissions are requested.
- Non-local navigation and resource requests are blocked. File/content access and native JavaScript bridges are disabled.
- Local progress is stored by the shared browser game in WebView local storage. Removing the app or clearing its data removes progress.
- `appvisibility` events carry `{ hidden: true | false }` when the activity pauses or resumes. WebView rendering timers also pause in the background.
- Android Back calls `window.gameBack()`. Return `true` when the game handles navigation; otherwise Android closes the activity.
- The native container handles system bars and display cutouts, including edge-to-edge on Android 15/16. Rotation keeps the existing game view.
- Debug builds allow WebView inspection; release builds disable it. Service workers are unnecessary and blocked in this host.

GitHub Actions builds the browser distribution, checks the game tests, runs Android lint, and uploads a debug APK. It does not publish the game or use release signing credentials.

Tool compatibility: [Android Gradle Plugin 8.10 release notes](https://developer.android.com/build/releases/agp-8-10-0-release-notes), [Gradle wrapper verification](https://docs.gradle.org/8.11.1/userguide/gradle_wrapper.html#sec:verification).
