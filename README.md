<p align="center">
  <img src="https://github.com/BouglaceMarouane/Aman/blob/8e214bb8f43a1e6d1ea7e22f7d4ee739f93f9498/app/src/main/ic_launcher-playstore.png" width="100%" alt="Aman Hydration Banner"/>
</p>

<div align="center">

### 💧 *A Smart Hydration Tracking Ecosystem built with Kotlin, Firebase & Wear OS*

<em>Aman helps users track daily water intake, stay hydrated with smart reminders, and sync seamlessly between Android & Wear OS — all wrapped in a clean, modern UI.</em>

<br>

![GitHub stars](https://img.shields.io/github/stars/bouglacemarouane/Aman?style=social)
![GitHub forks](https://img.shields.io/github/forks/bouglacemarouane/Aman?style=social)
![GitHub issues](https://img.shields.io/github/issues/bouglacemarouane/Aman)

</div>

---

# ✨ About Aman

**Aman** is a complete **Hydration Tracking Ecosystem** composed of:

- 📱 Android Mobile Application  
- ⌚ Wear OS Companion App  
- ☁️ Firebase Backend Integration  

It allows users to:

- Track daily water intake  
- Visualize weekly hydration statistics  
- Set personalized goals  
- Receive smart reminders  
- Sync data in real time between phone and smartwatch  

All built using **MVVM architecture**, **Room Database**, and **Firebase services**.

---

# 🚀 Core Features

## 📱 Mobile App (Android)

### 💧 Smart Dashboard
- Circular progress indicator
- Real-time hydration tracking
- Daily goal percentage display

### 📊 Weekly Statistics
- Interactive bar charts using **MPAndroidChart**
- Visual comparison of 7-day hydration trends

### 🎯 Custom Goals
- Personalized daily water target
- Goal achievement animation using **Lottie**

### 🔔 Smart Reminders
- Built with **WorkManager**
- Configurable interval (default: 2 hours)
- Survives device reboot

### 👤 User Profile
- Firebase Authentication (Email/Password)
- Firestore cloud synchronization
- Multi-language support (FR 🇫🇷 | EN 🇬🇧 | AR 🇸🇦 RTL)

### 📤 Data Export
- Export hydration history to CSV
- Local file storage

---

## ⌚ Wear OS App

### 🔵 Minimal Circular Interface
- Optimized for small screens
- Clean progress ring design

### ➕ Quick Add Button
- Add **200ml instantly**

### 🎤 Voice Input
- Supports:
  - 🇫🇷 French
  - 🇬🇧 English
  - 🇸🇦 Arabic

### 🔄 Real-Time Sync
- Data Layer API
- Instant sync between watch and mobile

---

# 🧠 Architecture

### 🏗️ Pattern
- MVVM
- Repository Pattern
- Single Source of Truth
- Offline-first with Room

### 🗂️ Modules
Aman/
├── mobile/ → Android app
└── wear/ → Wear OS companion


### 🔥 Firebase Integration
- Authentication
- Firestore
- Cloud Messaging (FCM)
- Analytics

### 💾 Local Persistence
- Room Database
- DataStore Preferences

---

# 📊 Firestore Structure

users/{uid}
├── displayName
├── email
├── dailyGoalMl
├── birthDate
├── locale
└── water_intake/{intakeId}
├── amountMl
└── timestamp


---

# 🎨 Design System

### 🎨 Colors
- Primary: `#4A90E2`
- Secondary: `#7ED321`
- Background: `#F5F7FA`

### ✍️ Typography
- Headers: 24sp Bold
- Body: 16sp Regular
- Caption: 14sp

### 📐 UI Style
- Material Design 3
- Rounded cards (12–16dp)
- Soft shadows
- Smooth animations

---

# 🧮 Hydration Tracking Logic

text
Daily Progress (%) = (Today Intake / Daily Goal) × 100

Example:
Goal = 2000ml
Intake = 1500ml

Progress = 75%
🧰 Technologies Used
<div align="center">








</div>
🔐 Security
🔒 Firestore Rules
match /users/{uid} {
  allow read, write: if request.auth.uid == uid;
}
🧪 Testing
./gradlew testDebugUnitTest
./gradlew connectedAndroidTest
🛠️ Installation
1️⃣ Clone the Repository
git clone https://github.com/<your-username>/Aman.git
2️⃣ Open in Android Studio
File → Open → Select project folder

3️⃣ Sync Gradle
Tools → Sync Project with Gradle Files

4️⃣ Run
Select mobile or wear

Click ▶ Run

🚧 Challenges Faced
During development, several challenges were addressed:

🔄 Real-time synchronization between Wear OS & Mobile

☁️ Managing offline-first architecture with Firebase + Room

🔔 Reliable background reminders using WorkManager

🌍 Proper RTL support for Arabic

📊 Optimizing chart performance

These improvements helped build a stable and scalable hydration ecosystem.

⭐ Why Aman Stands Out
✅ Complete Mobile + Wear Ecosystem
✅ Real-Time Cloud Sync
✅ Offline-first Architecture
✅ Clean Material 3 UI
✅ Multi-language Support
✅ Production-ready MVVM structure

👨‍💻 Author
<div align="center">
Marouane Bouglace
Mobile Application Developer | Kotlin & Wear OS Enthusiast




</div>
<div align="center">
Stay Hydrated. Stay Aman. 💧

</div> <p align="center"> <img src="https://capsule-render.vercel.app/api?type=waving&color=gradient&height=60&section=footer"/> </p>
