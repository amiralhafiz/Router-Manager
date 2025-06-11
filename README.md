# RouterManager

RouterManager is a simple Android application that opens a WebView pointing to your router's management interface. On first launch the app automatically scans your network and populates the router URL. This address is stored for future launches and can be reset later using the **Off** button.

## Prerequisites

- **Android SDK** – Ensure the Android SDK is installed and that you have an Android device or emulator available.
- **JDK 17** – The project uses Android Gradle Plugin 8.x which requires JDK 17.

## Building with the Gradle Wrapper

Use the provided Gradle wrapper scripts to build or install the app. On Unix systems run `./gradlew`; on Windows use `gradlew.bat`.

```bash
# Build the debug APK
./gradlew assembleDebug

# Install on a connected device or emulator
./gradlew installDebug
```

## Versioning

The build script derives version information from Git. It uses
`git rev-list --count HEAD` for the commit count and
`git rev-parse --short HEAD` to obtain the short commit hash. The commit
count becomes the `versionCode` and is split into `major`, `minor` and
`patch` values.

Version names follow the `v<major>.<minor>.<patch>-<git hash>` scheme.
`patch` increases with each commit and cycles from `01` through `10`. When it
wraps back to `01`, `minor` increments. If `minor` reaches `10` it resets to
`00` and `major` increments.

Example sequence:

```
v0.00.01-<sha>
...
v0.00.10-<sha>
v0.01.01-<sha>
```

## Running Instrumentation Tests

Ensure a device or emulator is connected and run:

```bash
./gradlew connectedAndroidTest
```

## Launching the WebView App

After installing the APK on your device, launch the **RouterManager** application. On the first run you'll see the setup screen while the app automatically scans for your router. The URL field is read-only and will be populated once the gateway is detected. Tap **Access** to continue. The WebView automatically loads this saved address on future launches.

You may also open the project in Android Studio and run it directly from there using the built-in Gradle wrapper support.

## Author

RouterManager is maintained by Amir Al Hafiz.

## License

This project is licensed under the [MIT License](LICENSE).
