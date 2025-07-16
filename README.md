# RouterManager

**RouterManager** is a lightweight Android application that wraps your router’s web management portal inside a native mobile interface using WebView. It automatically detects your router's IP address and provides quick, repeatable access - removing the need to open a browser and manually enter an IP each time.

---

[![Buy Me A Coffee](https://img.shields.io/badge/☕-Buy%20me%20a%20coffee-ffdd00?style=for-the-badge&logo=buymeacoffee&logoColor=black)](https://github.com/sponsors/amiralhafiz)

---

## 🧠 AI-Built: Powered by ChatGPT Codex

This project was built with approximately **80% of the code written using ChatGPT Codex**. More than just a code assistant, the AI acted as a **coding collaborator and QA engine**, helping validate architecture, fix issues in real time, and streamline repetitive development tasks.

> ❗ This project is not designed to teach programming. Its purpose is to demonstrate how modern AI tools can meaningfully **accelerate real-world software development** - even for production-ready mobile apps.

---

## 🔧 Why a Router Manager?

Not all routers come with official mobile apps. Most rely on outdated web portals that require manual IP entry in a browser. This app doesn't attempt to replace the router’s UI - instead, it **augments it** by offering:

- 🔍 Automatic router IP detection
- 🌐 Native WebView integration
- 🔁 Persistent, one-tap access to your router’s web admin panel
- 📱 A mobile-optimized, app-like experience

---

## ✨ Features

- ✅ Auto-scans local network for router IP
- ✅ Saves IP for future launches
- ✅ Loads the web interface in a secure WebView
- ✅ "Off" button to reset stored IP
- ✅ Simple and intuitive interface

---

## ⚙️ Prerequisites

Before building or running the project, ensure the following are installed:

- **Android Studio** (Electric Eel or newer recommended)
- **Android SDK**
- **JDK 17** (required for Android Gradle Plugin 8.x)
- **Git** (for automatic versioning - fallback supported if missing)

### Configure Android SDK path

Create a `local.properties` file in the project root containing:

```ini
sdk.dir=/path/to/Android/Sdk
```

Alternatively, set the `ANDROID_SDK_ROOT` environment variable.

`local.properties` is already listed in `.gitignore` and should not be committed.

---

## 🛠️ Building the App

Use the Gradle wrapper provided in the project.

### On macOS/Linux:

```bash
./gradlew assembleDebug
./gradlew installDebug
```

### On Windows:

```cmd
gradlew.bat assembleDebug
gradlew.bat installDebug
```

This will build and optionally install the debug APK on a connected Android device or emulator.

---

## 🚦 Versioning

This app uses Git-based versioning built into the Gradle build script.

- `git rev-list --count HEAD` is used as `versionCode`
- `git rev-parse --short HEAD` is used as part of `versionName`

Fallback logic applies if Git is unavailable:

- `versionCode = 1`
- `versionName = v0.00.01-dev`

**Versioning format:**

```
v<major>.<minor>.<patch>-<sha>
```

Example:

```
v0.00.01-abc123
v0.00.10-def456
v0.01.01-ghi789
```

---
## 🔒 ProGuard & Release Builds

Release builds can be minified with ProGuard. The rules in `app/proguard-rules.pro` keep WebView classes, Kotlin coroutines and Material dynamic color utilities while stripping debug logs.
Minification is disabled by default so `assembleRelease` works out of the box.


## ✅ Running Instrumentation Tests

To run Android UI tests:

```bash
./gradlew connectedAndroidTest
```

Ensure a device or emulator is connected.

---

## 📱 How to Use

1. Install the app on your Android device.
2. Launch **RouterManager**.
3. On first run, the app scans your local network and auto-detects your router’s IP.
4. The router IP is saved for future launches.
5. Tap **Access** to open the WebView.
6. Use the **Off** button to reset the saved address and re-trigger detection.

By default the app **permits cleartext (HTTP) traffic** so it can communicate with routers that don't support HTTPS. If you'd rather restrict cleartext access, edit `app/src/main/res/xml/network_security_config.xml` to allow only local networks:

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="false" />
    <domain-config cleartextTrafficPermitted="true">
        <!-- Allow local routers -->
        <domain includeSubdomains="true">10.0.0.0/8</domain>
        <domain includeSubdomains="true">172.16.0.0/12</domain>
        <domain includeSubdomains="true">192.168.0.0/16</domain>
    </domain-config>
</network-security-config>
```

This restricts HTTP access to devices with addresses starting with `10.*`,
`172.16.*`–`172.31.*`, or `192.168.*`.

---

## 🔒 Permissions

RouterManager requires the following permissions to detect your router's gateway IP and access the web interface:

- `INTERNET`
- `ACCESS_WIFI_STATE`
- `ACCESS_NETWORK_STATE`

No personal data is collected or transmitted.

---

## 👤 Author

**Amir Al Hafiz**

> Maintainer and creator of RouterManager  
> Built using ChatGPT Codex - AI-powered Android development

---

## 📄 License

This project is licensed under the [Personal Use License](LICENSE).
You are free to use and modify the code for non-commercial purposes with proper attribution.

---

## 📢 Final Notes

RouterManager is not intended as a replacement for official router apps or UIs. Instead, it offers a convenient, mobile-optimized wrapper around what already exists - built as a personal-use utility and a **demonstration of AI-enhanced software development**.

Feel free to clone, run, and explore the code. Feedback is welcome!
