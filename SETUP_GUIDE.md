# Aman - Quick Setup Guide

## 📋 Prerequisites Checklist

Before starting, ensure you have:

- [ ] Android Studio (Arctic Fox or newer)
- [ ] JDK 17 installed
- [ ] Android SDK (API Level 26 or higher)
- [ ] At least 4GB free disk space
- [ ] Stable internet connection (for first build)

## 🚀 5-Minute Quick Start

### Step 1: Extract & Open (2 minutes)
```bash
# Extract the Aman folder to your desired location
# Open Android Studio
# Click: File → Open → Select the Aman folder
```

### Step 2: Initial Sync (2 minutes)
Android Studio will automatically:
1. Detect the project structure
2. Download Gradle wrapper
3. Sync dependencies
4. Index files

**Wait for "Gradle Sync Finished" message at bottom**

### Step 3: Build (1 minute)
```
Build → Make Project (Ctrl/Cmd + F9)
```

Wait for "BUILD SUCCESSFUL" in Build tab.

## 📱 Running the App

### Mobile App
1. **Connect Device** or **Start Emulator**
    - Physical device: Enable USB Debugging
    - Emulator: Create AVD (API 26+, Google Play enabled)

2. **Select Configuration**
    - Dropdown at top: Select "mobile"

3. **Run**
    - Click green play button (Shift + F10)
    - App will install and launch

### Wear OS App (Optional)
1. **Setup Wear Emulator**
    - Tools → AVD Manager → Create Virtual Device
    - Choose Wear OS device
    - System Image: API 26+ with Google APIs

2. **Run Wear App**
    - Select "wear" configuration
    - Click Run

## 🔐 First Launch Experience

### 1. Splash Screen (2 seconds)
- Beautiful gradient with logo
- Lottie loading animation

### 2. Onboarding (Skip or complete)
- 3 pages explaining features
- Click "Passer" to skip
- Click "Commencer" to finish

### 3. Authentication
Choose one of:

#### Option A: Create Account
1. Click "Pas de compte? Inscrivez-vous"
2. Fill in details:
    - Display Name
    - Email
    - Password (6+ characters)
    - Birth Date (18+ years)
3. Click "Inscription"

#### Option B: Triple-Click Skip (Testing)
1. Click "Connexion" button 3 times quickly (within 1 second)
2. Automatically enters app without auth

### 4. Main Dashboard
You're in! 🎉

## 🎯 Key Features to Try

### 1. Add Water Intake
- Click blue FAB (+) button at bottom-right
- Select amount: 50ml, 100ml, 200ml, 250ml, or 500ml
- Watch progress update instantly

### 2. View Weekly Stats
- Scroll down dashboard
- See bar chart with last 7 days
- Blue bars show daily intake

### 3. Set Daily Goal
- Tap "Objectifs" tab
- Enter your target (ml)
- Click "Enregistrer"

### 4. Enable Reminders
- Go to "Mon compte" tab
- Enable notifications
- Set reminder interval (hours)

### 5. Wear OS Sync (if connected)
- Open Wear app
- View synced progress
- Add 200ml from watch
- Use voice: "Add 200 ml"

## 🔧 Troubleshooting

### Build Errors

#### "SDK location not found"
```bash
# Create/Edit local.properties
echo "sdk.dir=/path/to/your/Android/SDK" > local.properties
```

#### "Could not resolve dependencies"
1. Check internet connection
2. File → Invalidate Caches → Invalidate and Restart
3. Retry Gradle sync

#### "Manifest merger failed"
- Check AndroidManifest.xml for errors
- Ensure package name is "com.example.aman"

### Runtime Errors

#### "App crashes on launch"
1. Check Logcat (bottom panel)
2. Look for red error messages
3. Common issues:
    - Firebase not initialized
    - Permissions not granted
    - Database migration issue

#### "No internet connection"
- Ensure device/emulator has internet
- Check Firebase project is active
- Verify google-services.json is correct

#### "Notifications not showing"
- Grant notification permission
- Check battery optimization settings
- Verify WorkManager is running

### Firebase Issues

#### "Authentication failed"
- Verify email format
- Check password length (6+ chars)
- Ensure Firebase Auth is enabled in console

#### "Firestore permission denied"
- Check security rules in Firebase Console
- Default rules allow authenticated users only

## 📊 Testing the App

### Test Scenario 1: Basic Flow
1. Launch app → Skip onboarding
2. Triple-click to skip auth
3. Add water: 200ml + 250ml + 500ml
4. Check progress: Should show 950ml
5. View weekly chart

### Test Scenario 2: Goal Achievement
1. Set daily goal to 1000ml
2. Add 500ml twice
3. Goal reached dialog appears with animation 🎉

### Test Scenario 3: Wear Sync (if available)
1. Launch mobile app → Add 200ml
2. Open Wear app → Check updated progress
3. From Wear → Add 200ml
4. Mobile app updates automatically

## 🎨 Customization Tips

### Change Colors
Edit: `mobile/src/main/res/values/colors.xml`
```xml
<color name="primary">#YOUR_COLOR</color>
```

### Change Daily Goal Default
Edit: `mobile/src/main/java/com/example/aman/data/entities/UserProfile.kt`
```kotlin
val dailyGoalMl: Int = 2500  // Change from 2000
```

### Change Reminder Interval
Edit: `mobile/src/main/java/com/example/aman/ui/fragments/ProfileFragment.kt`

## 📱 Device Requirements

### Minimum
- Android 8.0 (API 26)
- 50MB storage
- Internet connection

### Recommended
- Android 10.0+ (API 29+)
- 100MB storage
- Google Play Services
- Wear OS device (optional)

## 🆘 Getting Help

### Check Logs
```bash
# View real-time logs
adb logcat -s AmanApp
```

### Database Inspection
1. Tools → App Inspection
2. Select "Aman" process
3. Click "Database Inspector"
4. View tables: water_intake, user_profile

### Firebase Debug
1. Open Firebase Console
2. Check Authentication users
3. View Firestore data
4. Check FCM messages

## ✅ Verification Checklist

After setup, verify:
- [ ] App launches without errors
- [ ] Can add water intake
- [ ] Progress updates correctly
- [ ] Chart displays data
- [ ] Can navigate between tabs
- [ ] Notifications appear (if enabled)
- [ ] Data persists after app restart

## 🎓 Next Steps

1. **Explore all features** in each tab
2. **Test reminders** by enabling in settings
3. **Try voice input** on Wear OS
4. **Export data** to CSV
5. **Customize** colors and goals

## 📚 Additional Resources

- [Android Documentation](https://developer.android.com)
- [Firebase Console](https://console.firebase.google.com)
- [Material Design](https://material.io/design)
- [Kotlin Guide](https://kotlinlang.org/docs/home.html)

---

**Ready to track your hydration! 💧**

For detailed technical documentation, see README.md