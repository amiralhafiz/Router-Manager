# RouterManager

RouterManager is a simple Android application that opens a WebView pointing to your router's management interface. When the app runs for the first time it shows an initial setup screen prompting for the router URL. The address you enter is stored for future launches and can be reset later using the **Off** button. If nothing is provided the app falls back to `https://10.80.80.1/`.

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

## Launching the WebView App

After installing the APK on your device, launch the **RouterManager** application. On the first run you'll see the setup screen where you can enter your router's URL and tap **Access**. The WebView automatically loads this saved address on future launches, defaulting to `https://10.80.80.1/` if no value was provided.

You may also open the project in Android Studio and run it directly from there using the built-in Gradle wrapper support.

## License

This project is licensed under the [MIT License](LICENSE).
