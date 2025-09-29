# MoodTracker Android App Architecture

## Overview
MoodTracker is an Android application that prompts users 4-5 times daily with customizable questions about their mood, activities, and well-being. The app supports different answer types, comprehensive logging, and modern UI design.

## Core Features

### 1. Question Types
- **Multiple Choice**: Pre-selection of N texts with additional text logging and snooze option
- **Yes/No**: Simple binary choice with snooze option  
- **Number Input**: Numeric answers with snooze option
- **Text Input**: Free-form text responses with snooze option

### 2. Navigation Structure
- **Main View**: Current questions, last answers, timestamps
- **Logs View**: Three modes (Timeline, Questions, Particular-Question)
- **Configuration**: Question management interface
- **Answer Questions**: Notification-triggered answering interface

### 3. Data Models

#### Question Model
```kotlin
data class Question(
    val id: String,
    val text: String,
    val type: QuestionType,
    val options: List<String>? = null, // For multiple choice
    val isHidden: Boolean = false,
    val createdAt: Long,
    val modifiedAt: Long,
    val version: Int = 1
)

enum class QuestionType {
    MULTIPLE_CHOICE,
    YES_NO,
    NUMBER,
    TEXT
}
```

#### Answer Model
```kotlin
data class Answer(
    val id: String,
    val questionId: String,
    val questionVersion: Int,
    val answerText: String,
    val additionalNotes: String? = null,
    val timestamp: Long,
    val wasSnooze: Boolean = false
)
```

#### Notification Schedule Model
```kotlin
data class NotificationSchedule(
    val id: String,
    val timeOfDay: String, // HH:mm format
    val isEnabled: Boolean = true
)
```

## Technical Architecture

### 1. Database Layer
- **Room Database** for local storage
- **Entities**: Question, Answer, NotificationSchedule
- **DAOs**: QuestionDao, AnswerDao, NotificationDao
- **Repository Pattern** for data access abstraction

### 2. UI Layer
- **Jetpack Compose** for modern UI
- **Material 3 Design System**
- **Navigation Component** for tab navigation
- **ViewModel** for state management
- **Dark/Light theme** following system settings

### 3. Background Services
- **WorkManager** for notification scheduling
- **AlarmManager** for precise timing
- **Notification channels** for different priority levels

### 4. Key Components

#### MainActivity
- Hosts bottom navigation
- Manages theme switching
- Handles deep links from notifications

#### MainScreen
- Displays current questions
- Shows last answers and timestamps
- Quick access to answer pending questions

#### LogsScreen
- Timeline view: Chronological list of all answers
- Questions view: Grouped by question with last N answers
- Particular question view: All answers for specific question

#### ConfigurationScreen
- Add/edit/delete questions
- Toggle question visibility
- Manage notification schedules
- Data consistency warnings

#### AnswerQuestionsScreen
- One-by-one question presentation
- Answer input based on question type
- Snooze functionality
- Progress indicator

### 5. Notification System
- **Daily scheduling**: 4-5 configurable times
- **Smart snoozing**: 15min, 1hr, 3hr options
- **Question batching**: Group unanswered questions
- **Persistent notifications** until answered or snoozed

## File Structure
```
app/
├── src/main/
│   ├── java/com/moodtracker/
│   │   ├── data/
│   │   │   ├── database/
│   │   │   ├── repository/
│   │   │   └── models/
│   │   ├── ui/
│   │   │   ├── main/
│   │   │   ├── logs/
│   │   │   ├── config/
│   │   │   ├── answer/
│   │   │   └── theme/
│   │   ├── services/
│   │   ├── utils/
│   │   └── MainActivity.kt
│   ├── res/
│   │   ├── values/
│   │   ├── values-night/
│   │   └── drawable/
│   └── AndroidManifest.xml
├── build.gradle.kts
└── proguard-rules.pro
```

## Development Phases
1. ✅ Architecture planning and environment setup
2. 🔄 Data models and database schema
3. ⏳ Core UI components and navigation
4. ⏳ Question answering interface and notifications
5. ⏳ Logs view implementation
6. ⏳ Configuration interface
7. ⏳ Notification scheduling and background services
8. ⏳ Modern UI design and theming
9. ⏳ Testing and APK generation
10. ⏳ Final delivery and documentation
