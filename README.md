# RouterManager

RouterManager is a simple Android application that opens a WebView pointing to your router's management interface. By default, the app loads `http://10.80.80.1/`.

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

After installing the APK on your device, launch the **RouterManager** application. The WebView will automatically navigate to `http://10.80.80.1/`, allowing you to interact with the router's web interface.

You may also open the project in Android Studio and run it directly from there using the built-in Gradle wrapper support.

## License

This project is licensed under the [MIT License](LICENSE).
