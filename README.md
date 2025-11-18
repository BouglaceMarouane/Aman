# Aman - Hydration Tracking App

## 🚀 Complete Android + Wear OS Hydration Tracking Ecosystem

A full-featured hydration tracking application with mobile and Wear OS companion apps, featuring Firebase integration, real-time synchronization, smart reminders, and beautiful visualizations.

## 📱 Features

### Mobile App (Android)
- ✨ **Dashboard** with circular progress indicator
- 📊 **Weekly Statistics** with MPAndroidChart bar charts
- 🎯 **Custom Goals** setting and tracking
- 📝 **Hydration Articles** with sharing capability
- 👤 **User Profile** management
- 🔔 **Smart Reminders** using WorkManager
- 🎨 **Lottie Animations** for goal achievements
- 🌍 **Multi-language Support** (FR, EN, AR with RTL)
- 💾 **Room Database** for local persistence
- ☁️ **Firebase Integration** (Auth, Firestore, FCM)
- 📤 **CSV Export** of hydration data
- ⚡ **Triple-Click Auth Skip** for testing

### Wear OS App
- ⌚ **Minimalist Circular UI**
- 📊 **Real-time Progress Ring**
- 🔘 **Quick Add 200ml** button
- 🎤 **Voice Input** support (French, English, Arabic)
- 🔄 **Data Layer Sync** with mobile app

## 🏗️ Architecture

### Project Structure
```
Aman/
├── mobile/                       # Android Mobile App
│   ├── build.gradle
│   ├── google-services.json
│   └── src/main/
│       ├── java/com/example/aman/
│       │   ├── data/
│       │   │   ├── dao/          # Room DAOs
│       │   │   ├── entities/     # Data entities
│       │   │   ├── db/           # Database
│       │   │   └── repository/   # Repository pattern
│       │   ├── ui/
│       │   │   ├── activities/   # Activities
│       │   │   ├── fragments/    # Fragments
│       │   │   ├── adapters/     # RecyclerView adapters
│       │   │   └── viewmodels/   # ViewModels
│       │   ├── worker/           # WorkManager workers
│       │   ├── firebase/         # Firebase services
│       │   ├── notif/            # Notifications
│       │   ├── utils/            # Utilities
│       │   ├── receivers/        # Broadcast receivers
│       │   └── AmanApplication.kt
│       ├── res/                  # Resources
│       └── AndroidManifest.xml
│
└── wear/                         # Wear OS App
    ├── build.gradle
    └── src/main/
        ├── java/com/example/aman/
        │   ├── ui/               # UI components
        │   ├── receiver/         # Data layer listener
        │   └── viewmodels/       # ViewModels
        ├── res/                  # Resources
        └── AndroidManifest.xml
```

### Tech Stack

#### Mobile
- **Language**: Kotlin 1.9.22
- **UI**: Material Design 3, ViewBinding
- **Architecture**: MVVM with Repository Pattern
- **Database**: Room 2.6.1
- **Firebase**: Auth, Firestore, FCM, Analytics
- **Background**: WorkManager 2.9.0
- **Charts**: MPAndroidChart 3.1.0
- **Animations**: Lottie 6.3.0
- **Wearable**: Play Services Wearable 18.1.0
- **Preferences**: DataStore 1.0.0

#### Wear OS
- **Language**: Kotlin 1.9.22
- **UI**: Wear UI Components
- **Sync**: Data Layer API
- **Voice**: RecognizerIntent

## 🚀 Setup Instructions

### Prerequisites
1. **Android Studio** (Arctic Fox or newer)
2. **JDK 17**
3. **Firebase Project** (already configured)
4. **Android SDK** (API Level 26+)

### Step 1: Open in Android Studio
```bash
# Clone or extract the project
cd Aman

# Open in Android Studio
# File → Open → Select Aman folder
```

### Step 2: Firebase Configuration
The project already includes `google-services.json` with the following configuration:
- **Project ID**: aman-c60bf
- **App ID**: 1:448780002194:android:f1edc193816408ed1f174b
- **Package**: com.example.aman

**Firebase services enabled:**
- Authentication (Email/Password)
- Cloud Firestore
- Cloud Messaging
- Analytics

### Step 3: Sync Gradle
```
Tools → Gradle → Sync Project with Gradle Files
```

Wait for dependencies to download (may take 5-10 minutes on first sync).

### Step 4: Build the Project
```
Build → Make Project (Ctrl/Cmd + F9)
```

### Step 5: Run the Apps

#### Running Mobile App
1. Connect Android device or start emulator (API 26+)
2. Select `mobile` configuration
3. Click Run (Shift + F10)

#### Running Wear OS App
1. Pair Wear OS emulator or device
2. Select `wear` configuration
3. Click Run

### Step 6: Configure Reminders
The app uses WorkManager for periodic reminders:
- Default interval: 2 hours
- Configurable in Profile settings
- Survives app restarts (BootReceiver)

## 🔧 Key Components

### Database Schema

#### WaterIntake Table
```kotlin
@Entity(tableName = "water_intake")
data class WaterIntake(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amountMl: Int,
    val timestamp: Long
)
```

#### UserProfile Table
```kotlin
@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val uid: String,
    val displayName: String,
    val email: String,
    val dailyGoalMl: Int = 2000,
    val birthDate: Long,
    val locale: String = "fr"
)
```

### Firebase Firestore Structure
```
users/ (collection)
  ├── {uid}/ (document)
  │   ├── displayName: string
  │   ├── email: string
  │   ├── dailyGoalMl: number
  │   ├── birthDate: number
  │   ├── locale: string
  │   └── water_intake/ (subcollection)
  │       └── {intakeId}/ (document)
  │           ├── amountMl: number
  │           └── timestamp: number
```

### Data Layer Sync

#### Mobile → Wear
Path: `/hydration_data`
```kotlin
dataMap.putInt("dailyGoal", dailyGoal)
dataMap.putFloat("todayProgress", progress)
dataMap.putInt("todayIntake", intake)
```

#### Wear → Mobile
Path: `/add_intake`
```kotlin
val bytes = ByteBuffer.allocate(4).putInt(amountMl).array()
messageClient.sendMessage(nodeId, "/add_intake", bytes)
```

## 🎨 Design System

### Color Palette
- **Primary**: #4A90E2 (Blue)
- **Primary Light**: #A4D7E1 (Light Blue)
- **Secondary**: #7ED321 (Green)
- **Background**: #F5F7FA
- **Text Primary**: #333333
- **Text Secondary**: #666666

### Typography
- **Headers**: 24sp, Bold
- **Body**: 16sp, Regular
- **Captions**: 14sp, Regular

### Spacing
- **Cards**: 16dp padding, 12-16dp radius
- **Screen margins**: 16dp
- **Elements**: 8-16dp spacing

## 🔐 Authentication

### Email/Password
```kotlin
FirebaseAuth.getInstance()
    .createUserWithEmailAndPassword(email, password)
```

### Triple-Click Skip
Click the "Sign In" button 3 times within 1 second to skip authentication for testing:
```kotlin
if (currentTime - lastClickTime < 1000 && clickCount >= 2) {
    viewModel.skipAuth()
}
```

## 🔔 Notifications

### Reminder Notifications
- **Channel**: `hydration_reminder`
- **Priority**: HIGH
- **Actions**: "+200ml" quick action
- **Frequency**: User-configurable (default 2 hours)

### Motivation Notifications (FCM)
Example payload:
```json
{
  "notification": {
    "title": "Goal en vue!",
    "body": "Vous avez atteint 80% de votre objectif"
  },
  "data": {
    "type": "motivation",
    "progress": "80"
  }
}
```

## 📊 Charts & Visualizations

### MPAndroidChart Implementation
```kotlin
val entries = data.mapIndexed { index, pair ->
    BarEntry(index.toFloat(), pair.second.toFloat())
}
val dataSet = BarDataSet(entries, "Hydratation")
barChart.data = BarData(dataSet)
```

### Progress Indicators
- Circular progress for daily goal
- Bar chart for weekly trends
- Percentage display

## 🌍 Internationalization

Supported languages:
- **French (FR)** - Default
- **English (EN)**
- **Arabic (AR)** - with RTL support

Add translations in:
- `values/strings.xml` (FR)
- `values-fr/strings.xml` (FR explicit)
- `values-en/strings.xml` (EN)
- `values-ar/strings.xml` (AR)

## 📤 CSV Export

Export hydration history:
```kotlin
val csvContent = buildString {
    append("Date,Time,Amount (ml)\n")
    intakes.forEach { intake ->
        val date = DateUtils.formatDate(intake.timestamp)
        val time = DateUtils.formatTime(intake.timestamp)
        append("$date,$time,${intake.amountMl}\n")
    }
}
```

File saved to: `Android/data/com.example.aman/files/hydration_export.csv`

## 🐛 Debugging

### Enable Debug Logging
```kotlin
FirebaseFirestore.setLoggingEnabled(true)
```

### View Database
Use Android Studio Database Inspector:
```
View → Tool Windows → App Inspection → Database Inspector
```

### Check WorkManager
```
adb shell dumpsys jobscheduler
```

## 🔒 Security

### ProGuard Rules
Configured for:
- Room
- Firebase
- Lottie
- MPAndroidChart
- Wearable APIs

### Firestore Rules
```javascript
match /users/{uid} {
  allow read, write: if request.auth.uid == uid;
}
```

## 📝 Testing

### Unit Tests
```bash
./gradlew testDebugUnitTest
```

### Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

## 🚀 Build & Release

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

Output: `mobile/build/outputs/apk/release/mobile-release.apk`

## 📦 Dependencies

See `mobile/build.gradle` and `wear/build.gradle` for complete dependency list.

## 🤝 Contributing

This is an academic project. For modifications:
1. Follow existing code structure
2. Maintain MVVM architecture
3. Add unit tests for new features
4. Update documentation

## 📄 License

This project is created for academic purposes.

## 👤 Author

Created for academic career advancement.

## 📞 Support

For issues or questions:
1. Check Firebase Console for backend issues
2. Verify google-services.json is correct
3. Ensure all permissions are granted
4. Check Logcat for detailed error messages

## 🎉 Features to Explore

1. **Dashboard**: View today's progress and weekly stats
2. **Quick Add**: Use FAB to add water in preset amounts
3. **Goals**: Customize your daily hydration target
4. **Reminders**: Enable periodic notifications
5. **Wear Sync**: Add water from your smartwatch
6. **Voice**: Use voice commands on Wear OS
7. **Export**: Download your hydration history
8. **Achievements**: Celebrate reaching daily goals with animations

---

**Built with ❤️ using Kotlin, Firebase, and Material Design**