<p align="center">
  <img src="https://raw.githubusercontent.com/BouglaceMarouane/Aman/8e214bb8f43a1e6d1ea7e22f7d4ee739f93f9498/app/src/main/ic_launcher-playstore.png" width="30%" alt="Aman Hydration Banner"/>
</p>

<div align="center">

### 💧 *A Smart Hydration Tracking Ecosystem built with Kotlin, Firebase & Wear OS*

<em>Aman helps users track daily water intake, stay hydrated with smart reminders, and sync seamlessly between Android & Wear OS — all wrapped in a clean, modern UI.</em>

<br>

[![GitHub stars](https://img.shields.io/github/stars/BouglaceMarouane/Aman?style=social)](https://github.com/BouglaceMarouane/Aman)
[![GitHub forks](https://img.shields.io/github/forks/BouglaceMarouane/Aman?style=social)](https://github.com/BouglaceMarouane/Aman)
[![GitHub issues](https://img.shields.io/github/issues/BouglaceMarouane/Aman)](https://github.com/BouglaceMarouane/Aman/issues)

</div>

---

## ✨ About Aman

**Aman** is a smart **Android & Wear OS hydration tracking ecosystem** designed to help users monitor daily water intake and build healthy hydration habits.  

It combines **real-time progress tracking**, **cloud synchronization with Firebase**, and **smart wearable integration** — all wrapped in a clean, modern Material Design 3 interface.

<div align="center"> 
  <img src="https://github.com/BouglaceMarouane/Aman/blob/9b92b74775da5fd85145629756942f5a37af8a41/Screenshot%202026-02-11%20220930.png" width="100%" alt="Taxi Animation"/><br> 
  <a href="https://www.canva.com/design/DAG5LIqDJWA/-oYasxmsLVyYpKsYOUlIZw/edit?utm_content=DAG5LIqDJWA&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton" target="_blank"> 
    <img src="https://img.shields.io/badge/📊%20View%20the%20Presentation-00C4CC?style=for-the-badge&logo=canva&logoColor=white" alt="View Presentation"/> 
  </a> 
</div> 

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
~~~
Aman/
├── mobile/ → Android app
└── wear/ → Wear OS companion
~~~
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

~~~
users/{uid}
├── displayName
├── email
├── dailyGoalMl
├── birthDate
├── locale
└── water_intake/{intakeId}
├── amountMl
└── timestamp
~~~

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
~~~
Daily Progress (%) = (Today Intake / Daily Goal) × 100

Example:
Goal = 2000ml
Intake = 1500ml

Progress = 75%
~~~

## 🧰 Technologies Used

<div align="center">

### 📱 Mobile App

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-7F52FF?logo=kotlin&logoColor=white)
![Material 3](https://img.shields.io/badge/Material%20Design-3-3DDC84?logo=android&logoColor=white)
![MVVM](https://img.shields.io/badge/Architecture-MVVM-blue)
![Repository](https://img.shields.io/badge/Pattern-Repository-lightgrey)
![Room](https://img.shields.io/badge/Room-2.6.1-42A5F5?logo=sqlite&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-Auth%20%7C%20Firestore%20%7C%20FCM-FFCA28?logo=firebase&logoColor=black)
![WorkManager](https://img.shields.io/badge/WorkManager-2.9.0-blue)
![MPAndroidChart](https://img.shields.io/badge/MPAndroidChart-3.1.0-orange)
![Lottie](https://img.shields.io/badge/Lottie-6.3.0-00DDB3?logo=lottiefiles&logoColor=white)
![Wearable](https://img.shields.io/badge/Play%20Services-Wearable%2018.1.0-4285F4?logo=googleplay&logoColor=white)
![DataStore](https://img.shields.io/badge/DataStore-1.0.0-purple)

### ⌚ Wear OS App

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-7F52FF?logo=kotlin&logoColor=white)
![Wear UI](https://img.shields.io/badge/Wear%20UI-Components-4285F4?logo=google&logoColor=white)
![Data Layer](https://img.shields.io/badge/Data%20Layer-API-success)
![Voice](https://img.shields.io/badge/Voice-RecognizerIntent-lightgrey)

</div>

## 🔐 Security

### 🔒 Firestore Rules
~~~
match /users/{uid} {
  allow read, write: if request.auth.uid == uid;
}
~~~
### 🧪 Testing
~~~
./gradlew testDebugUnitTest
./gradlew connectedAndroidTest
~~~

## 🛠️ Installation

### 1️⃣ Clone the Repository
~~~
git clone https://github.com/<your-username>/Aman.git
~~~

### 2️⃣ Open in Android Studio
  - File → Open → Select project folder

### 3️⃣ Sync Gradle
  - Tools → Sync Project with Gradle Files

### 4️⃣ Run
  - Select mobile or wear
  - Click ▶ Run

## 🚧 Challenges Faced

**During development, several challenges were addressed:**

  - 🔄 Real-time synchronization between Wear OS & Mobile

  - ☁️ Managing offline-first architecture with Firebase + Room

  - 🔔 Reliable background reminders using WorkManager

  - 🌍 Proper RTL support for Arabic

  - 📊 Optimizing chart performance

**These improvements helped build a stable and scalable hydration ecosystem.**

## ⭐ Why Aman Stands Out

✅ Complete Mobile + Wear Ecosystem

✅ Real-Time Cloud Sync

✅ Offline-first Architecture

✅ Clean Material 3 UI

✅ Multi-language Support

✅ Production-ready MVVM structure

## 🤝 Contributing

Contributions to this project are welcome! If you have suggestions, improvements, or bug fixes, please submit a pull request. Make sure to follow coding conventions and maintain consistent styles.

If you encounter issues or want to request a new feature, please open an issue in the repository with as much detail as possible.

### Ways to Contribute
- 🐛 **Report Bugs** - Found an issue? Let us know!
- 💡 **Suggest Features** - Have ideas? We'd love to hear them!
- 🔧 **Submit Pull Requests** - Code contributions are welcome
- 📖 **Improve Documentation** - Help make our docs better

### Getting Started
1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## ⭐ Support

If you like this project, don't forget to leave a ⭐ on GitHub. Thank you and happy coding! 🚀

---

## 📬 Stay in Touch

<div align="center">

### 👨‍💻 **Marouane Bouglace** - *Project Creator*

[![Email](https://img.shields.io/badge/Email-bouglacemarouane@gmail.com-red?style=for-the-badge&logo=gmail&logoColor=white)](mailto:bouglacemarouane@gmail.com)
[![GitHub](https://img.shields.io/badge/GitHub-bouglacemarouane-black?style=for-the-badge&logo=github&logoColor=white)](https://github.com/bouglacemarouane)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Marouane%20Bouglace-blue?style=for-the-badge&logo=linkedin&logoColor=white)](https://linkedin.com/in/marouane-bouglace)

</div>

---

<div align="center">

**Thank you for visiting Aman! 💬✨**

</div>

<p align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&color=gradient&height=60&section=footer"/>
</p>
