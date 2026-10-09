# M-U-N Gallery — Your Moments, Beautifully.

A flagship offline Android gallery application built with modern Kotlin and Jetpack Compose, featuring an exquisite dark **Liquid Glass** aesthetic, real device MediaStore indexing, ExoPlayer video playback, albums, persistent favorites, and a built-in photo editor.

---

## 📱 گٹ ہب سے اے پی کے ڈاؤن لوڈ اور انسٹال کرنے کا طریقہ (Urdu Guide)

آپ اس ریپوزٹری کو گٹ ہب پر اپ لوڈ کر کے ڈائریکٹ **`app-debug.apk`** ڈاؤن لوڈ کر سکتے ہیں:

### 1. ریپوزٹری کو گٹ ہب پر اپ لوڈ (Push) کریں:
اپنے کمپیوٹر یا ٹرمینل میں یہ کمانڈز چلائیں:
```bash
git init
git add .
git commit -m "Initial release of M-U-N Gallery"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPOSITORY.git
git push -u origin main
```

### 2. خودکار بلڈ اور ریلیز (Automatic Build & Release):
جیسے ہی آپ `git push` کریں گے:
- گٹ ہب ایکشنز (**GitHub Actions**) خود بخود ایپ کو بلڈ کرے گا۔
- بلڈ ہوتے ہی گٹ ہب پر ایک نیا **Release (`v1.0.0`)** خود بخود بن جائے گا۔
- اس ریلیز میں **`app-debug.apk`** تیار مل جائے گا۔

### 3. اپنے فون پر اے پی کے ڈاؤن لوڈ کریں:
1. اپنے اینڈرائیڈ فون کے براؤزر میں اپنے گٹ ہب پروجیکٹ کا پیج کھولیں۔
2. دائیں طرف **Releases** پر کلک کریں (یا `https://github.com/YOUR_USERNAME/YOUR_REPOSITORY/releases` کھولیں)۔
3. **Assets** کے نیچے آپ کو **`app-debug.apk`** فائل نظر آئے گی۔
4. اس پر کلک کر کے ڈاؤن لوڈ کریں۔
5. ڈاؤن لوڈ ہونے کے بعد فائل اوپن کر کے اپنے فون پر انسٹال کریں!

---

## 🚀 English Quickstart Guide

### How to Build Locally
```bash
# Debug APK:
./gradlew assembleDebug

# Release APK:
./gradlew assembleRelease
```

### Generated APK Locations:
- **Debug APK:** `app/build/outputs/apk/debug/app-debug.apk`
- **Release APK:** `app/build/outputs/apk/release/app-release.apk`

---

## 🛠 Features

- **Real Device MediaStore Access:** Reads and manages photos and videos stored locally on device without cloud uploads or account logins.
- **Dark Liquid Glass UI:** Frosted glass cards, customized reflection highlights, thin borders, and customizable neon glows.
- **Full-Screen Photo Viewer:** Horizontal swipe, pinch-to-zoom, double-tap zoom, file details sheet, favorite, share, edit, and delete.
- **Media3 / ExoPlayer Video Player:** Hardware-accelerated video playback with seekbar, duration timestamps, mute toggle, and auto-hiding glass controls.
- **Device Albums:** Automatically detects local folders (Camera, Screenshots, Downloads, WhatsApp, etc.).
- **Persistent Favorites:** Backed by a local Room database (`mun_gallery_database`).
- **Built-in Photo Editor:** Real adjustments (brightness, contrast, saturation, warmth), transformations (90° rotation, horizontal/vertical flip), and aspect ratio crop (1:1, 4:3, 16:9). Exports copies to `Pictures/M-U-N Gallery/`.
- **Customization Engine:** Interactive sliders for glass transparency, blur intensity, reflection intensity, corner radius, wallpaper picker, and neon lights.

---

## 🔐 Optional Production Keystore Signing
If you wish to sign release builds with your own private key, configure these secrets under **Settings → Secrets and variables → Actions**:
- `RELEASE_KEYSTORE_BASE64`: Base64 encoded `.jks` file
- `STORE_PASSWORD`: Keystore password
- `KEY_PASSWORD`: Key password
