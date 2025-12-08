# 📱 MoodTracker

**Android app for tracking mood, habits, and personal metrics through scheduled daily prompts**

[![Android](https://img.shields.io/badge/Android-7.0%2B-green?logo=android)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-purple?logo=kotlin)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material3-blue)](https://developer.android.com/jetpack/compose)

---

## 🌟 Features

- **Scheduled notifications** — Configurable daily prompts at times you choose
- **Multiple question types** — Text, yes/no, numeric, and multiple-choice
- **Local storage** — All data stays on your device
- **Dark/light mode** — Material Design 3 theming
- **Persistent** — Notifications survive restarts and battery optimization
- **Log views** — Timeline, by-question, and per-question history

---

## ℹ️ Overview

MoodTracker prompts you with questions throughout the day and stores your responses locally. You define the questions, answer types, and notification schedule. The app provides several views for reviewing your logged data over time.

---

## 🚀 Quick Start

### Installation

1. Download `app-release.apk` from [Releases](../../releases)
2. Enable "Install from Unknown Sources" in Android settings
3. Install and grant notification permissions

**Requirements:** Android 7.0+ (API 24) • ~11MB storage

### Setup

1. Open the configuration tab and add questions
2. Set notification times in settings
3. Respond to prompts as they appear
4. Review responses in the logs section

---

## 🎯 Question Examples

| Question | Type | Use Case |
|----------|------|----------|
| "How is your mood?" | Multiple choice | Mood tracking |
| "Did you exercise today?" | Yes/No | Habit tracking |
| "Hours at the computer?" | Number | Time tracking |
| "Notes for the day" | Text | Journaling |

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

- `POST_NOTIFICATIONS` — Display notifications
- `SCHEDULE_EXACT_ALARM` — Precise timing
- `RECEIVE_BOOT_COMPLETED` — Resume after restart
- `WAKE_LOCK` — Background reliability

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

- All data stored locally on device
- No external servers or analytics
- No account required

---

## 🗺️ Roadmap

- [ ] Data export (CSV/JSON)
- [ ] Analytics dashboard with charts
- [ ] Cloud backup & sync
- [ ] Home screen widgets
- [ ] Localization

---

## 💭 Contributing

- **Issues** — Report bugs or request features via [GitHub Issues](../../issues)
- **Pull Requests** — Contributions welcome

---

## 📄 License

Open source. Free to use and modify.

---

<p align="center">
  <strong>Version 1.0.0</strong> • Android 7.0+ • ~11MB
</p>
