# 📱 MoodTracker

**A mindful Android companion for tracking your daily mood, habits, and personal insights**

[![Android](https://img.shields.io/badge/Android-7.0%2B-green?logo=android)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-purple?logo=kotlin)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material3-blue)](https://developer.android.com/jetpack/compose)

---

## 🌟 Highlights

- **Gentle reminders** — 4-5 customizable daily prompts to check in with yourself
- **Flexible questions** — Text, yes/no, numeric, or multiple-choice answer types
- **Privacy-first** — All data stays on your device, never uploaded anywhere
- **Beautiful UI** — Modern Material Design 3 with dark/light mode support
- **Reliable** — Notifications persist through restarts and battery optimization
- **Insightful** — Multiple views to analyze patterns and track progress over time

---

## ℹ️ Overview

MoodTracker is designed to help you build self-awareness through brief, consistent check-ins throughout your day. Whether you're tracking mood, exercise, sleep quality, or any personal metric, the app provides a simple yet powerful framework for reflection.

Rather than overwhelming you with complex analytics, MoodTracker focuses on making it easy to capture moments and review them later—helping you notice patterns you might otherwise miss.

---

## 🚀 Quick Start

### Installation

1. Download the latest `app-release.apk` from [Releases](../../releases)
2. Enable "Install from Unknown Sources" in your Android settings
3. Open the APK and follow the prompts
4. Grant notification permissions when asked

**Requirements:** Android 7.0+ (API 24) • ~11MB storage

### First Steps

1. **Add your questions** — Tap the configuration tab and create questions that matter to you
2. **Set your schedule** — Choose up to 5 notification times throughout your day
3. **Respond & reflect** — Answer prompts as they come, snooze if you're busy
4. **Review your journey** — Explore the logs to discover patterns

---

## 📸 Screenshots

<!-- Add screenshots here -->
*Coming soon: Screenshots showcasing the main interface, question types, and analytics views*

---

## 🎯 Question Examples

Get started with questions like these:

| Question | Type | Purpose |
|----------|------|---------|
| "How is your mood right now?" | Multiple choice | Track emotional patterns |
| "Did you exercise today?" | Yes/No | Build habit awareness |
| "Hours at the computer?" | Number | Monitor screen time |
| "What are you grateful for?" | Text | Practice gratitude |
| "Energy level (1-10)?" | Number | Track vitality |

---

## 🔔 Notification Schedule

Suggested check-in times for balanced self-reflection:

- **9:00 AM** — Morning intention
- **1:00 PM** — Midday pulse
- **4:00 PM** — Afternoon reflection
- **7:00 PM** — Evening review
- **9:00 PM** — Night thoughts

*Customize these times in Settings to match your routine.*

---

## 🛠️ Technical Details

<details>
<summary><strong>Technology Stack</strong></summary>

| Component | Technology |
|-----------|------------|
| Language | Kotlin |
| UI Framework | Jetpack Compose + Material Design 3 |
| Database | Room (SQLite) |
| Background Tasks | WorkManager |
| Date/Time | kotlinx-datetime |
| Architecture | MVVM with Repository pattern |

</details>

<details>
<summary><strong>Project Structure</strong></summary>

```
app/src/main/java/com/moodtracker/
├── data/
│   ├── database/          # Room database, DAOs
│   ├── models/            # Data classes
│   └── repository/        # Data access layer
├── services/              # Background services, notifications
├── ui/
│   ├── answer/            # Question answering interface
│   ├── config/            # Configuration screens
│   ├── logs/              # Logging and analytics
│   ├── main/              # Main dashboard
│   └── theme/             # UI theming
└── utils/                 # Utility functions
```

</details>

<details>
<summary><strong>Required Permissions</strong></summary>

- `POST_NOTIFICATIONS` — Display notification prompts
- `SCHEDULE_EXACT_ALARM` — Precise notification timing
- `RECEIVE_BOOT_COMPLETED` — Resume after restart
- `WAKE_LOCK` — Ensure reliability when device sleeps

</details>

<details>
<summary><strong>Building from Source</strong></summary>

```bash
git clone https://github.com/your-username/moodtracker.git
cd moodtracker

# Requires Android SDK and JDK 17+
./gradlew assembleDebug
```

</details>

---

## 🔒 Privacy

Your data belongs to you:

- ✅ All data stored locally on your device
- ✅ No external servers or analytics
- ✅ No account required
- ✅ Database secured by Android's built-in encryption

---

## 🗺️ Roadmap

- [ ] Data export (CSV/JSON)
- [ ] Visual analytics dashboard with charts
- [ ] Cloud backup & sync (optional)
- [ ] Home screen widgets
- [ ] Localization support

---

## 💭 Feedback & Contributing

Found a bug? Have an idea? Contributions are welcome!

- **Issues** — Report bugs or request features via [GitHub Issues](../../issues)
- **Pull Requests** — See our contribution guidelines before submitting
- **Discussions** — Share how you use MoodTracker and what works for you

---

## 📖 Troubleshooting

<details>
<summary><strong>Notifications not appearing?</strong></summary>

1. Check notification permissions in Settings > Apps > MoodTracker
2. Disable battery optimization for the app
3. Ensure "Do Not Disturb" isn't blocking alerts

</details>

<details>
<summary><strong>App crashes on startup?</strong></summary>

Ensure your device runs Android 7.0 or higher. If issues persist, try clearing app data and reinstalling.

</details>

---

## 📄 License

This project is open source. Feel free to use, modify, and share.

---

<p align="center">
  <strong>Version 1.0.0</strong> • Android 7.0+ • ~11MB
</p>
