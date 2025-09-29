# MoodTracker - Technical Documentation

## Architecture Overview

The MoodTracker app follows modern Android development practices with a clean architecture approach, separating concerns across data, domain, and presentation layers.

### Key Architectural Patterns

#### MVVM (Model-View-ViewModel)
- **Model**: Room database entities and repository pattern
- **View**: Jetpack Compose UI components
- **ViewModel**: State management and business logic (implicit through Compose state)

#### Repository Pattern
- `MoodTrackerRepository` acts as a single source of truth
- Abstracts data access from UI components
- Provides clean API for database operations

#### Dependency Injection
- Manual dependency injection for simplicity
- Repository instances passed through Compose navigation
- Could be enhanced with Hilt/Dagger for production

## Data Layer

### Database Schema

#### Questions Table
```sql
CREATE TABLE questions (
    id TEXT PRIMARY KEY NOT NULL,
    text TEXT NOT NULL,
    type TEXT NOT NULL,
    options TEXT,
    isHidden INTEGER NOT NULL DEFAULT 0,
    createdAt INTEGER NOT NULL,
    modifiedAt INTEGER NOT NULL,
    version INTEGER NOT NULL DEFAULT 1
);
```

#### Answers Table
```sql
CREATE TABLE answers (
    id TEXT PRIMARY KEY NOT NULL,
    questionId TEXT NOT NULL,
    answerText TEXT NOT NULL,
    additionalNotes TEXT,
    timestamp INTEGER NOT NULL,
    FOREIGN KEY(questionId) REFERENCES questions(id) ON DELETE CASCADE
);
```

#### Notification Schedules Table
```sql
CREATE TABLE notification_schedules (
    id TEXT PRIMARY KEY NOT NULL,
    timeOfDay TEXT NOT NULL,
    isEnabled INTEGER NOT NULL DEFAULT 1
);
```

### Data Access Objects (DAOs)

#### QuestionDao
- `getAllActiveQuestions()`: Returns non-hidden questions
- `getAllQuestions()`: Returns all questions for configuration
- `insertQuestion()`, `updateQuestion()`, `deleteQuestion()`
- `updateQuestionVisibility()`: Toggle question visibility

#### AnswerDao
- `getAllAnswers()`: Chronological list of all answers
- `getAnswersForQuestion()`: Answers for specific question
- `getLatestAnswerForEachQuestion()`: Most recent answer per question
- `insertAnswer()`, `updateAnswer()`, `deleteAnswer()`

#### NotificationDao
- `getAllSchedules()`, `getEnabledSchedules()`
- `insertSchedule()`, `updateSchedule()`, `deleteSchedule()`
- `updateScheduleEnabled()`: Toggle schedule on/off

### Data Models

#### Question
```kotlin
@Entity(tableName = "questions")
data class Question(
    @PrimaryKey val id: String,
    val text: String,
    val type: QuestionType,
    val options: List<String>? = null,
    val isHidden: Boolean = false,
    val createdAt: Instant,
    val modifiedAt: Instant,
    val version: Int = 1
)
```

#### Answer
```kotlin
@Entity(
    tableName = "answers",
    foreignKeys = [ForeignKey(
        entity = Question::class,
        parentColumns = ["id"],
        childColumns = ["questionId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Answer(
    @PrimaryKey val id: String,
    val questionId: String,
    val answerText: String,
    val additionalNotes: String? = null,
    val timestamp: Instant
)
```

## UI Layer

### Jetpack Compose Structure

#### Navigation
- Single Activity with Compose Navigation
- Bottom navigation bar with three main sections
- Modal navigation for question answering

#### Screen Components

##### MainScreen
- Displays current questions and latest answers
- "Answer Questions" button for manual entry
- Uses `LazyColumn` for question list

##### AnswerQuestionsScreen
- Step-by-step question answering interface
- Support for different question types
- Snooze functionality for delayed responses

##### LogsScreen
- Three view modes: Timeline, Questions, Particular Question
- Configurable number of recent answers per question
- Date formatting with relative time display

##### ConfigScreen
- Question management interface
- Add, edit, delete, and hide questions
- Data consistency warnings for modifications

### State Management

#### Compose State
- `collectAsState()` for observing database flows
- `remember` and `mutableStateOf` for local UI state
- `rememberCoroutineScope()` for launching coroutines

#### Data Flow
```
Database → Repository → Flow → collectAsState() → UI
```

### Theme System

#### Material Design 3
- Dynamic color support for Android 12+
- Light and dark theme variants
- Custom color scheme with brand colors

#### Typography
- Material 3 typography scale
- Consistent text styles across the app
- Proper contrast ratios for accessibility

## Background Services

### Notification System

#### NotificationService
- Creates notification channels
- Builds and displays notifications
- Handles notification actions

#### NotificationReceiver
- BroadcastReceiver for scheduled notifications
- Handles boot completed events
- Triggers notification display

#### NotificationScheduler
- Manages AlarmManager for precise timing
- Schedules and cancels notifications
- Handles snooze functionality

#### NotificationWorker
- WorkManager worker for periodic scheduling
- Ensures notifications persist across app updates
- Handles background constraints

### Background Processing Flow
```
WorkManager → NotificationWorker → NotificationScheduler → AlarmManager → NotificationReceiver → NotificationService
```

## Utilities

### DataUtils
- ID generation using UUID
- Current timestamp utilities
- Date formatting functions
- Time zone handling

### Date/Time Handling
- `kotlinx-datetime` for cross-platform compatibility
- `Instant` for UTC timestamps
- `LocalDateTime` for display formatting
- Proper time zone conversion

## Build Configuration

### Gradle Setup
- Kotlin DSL for build scripts
- Version catalogs for dependency management
- ProGuard rules for release builds

### Dependencies
```kotlin
// Core Android
implementation("androidx.core:core-ktx:1.12.0")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
implementation("androidx.activity:activity-compose:1.8.2")

// Compose
implementation(platform("androidx.compose:compose-bom:2024.02.00"))
implementation("androidx.compose.material3:material3:1.2.0")
implementation("androidx.navigation:navigation-compose:2.7.6")

// Database
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
kapt("androidx.room:room-compiler:2.6.1")

// Background Work
implementation("androidx.work:work-runtime-ktx:2.9.0")

// Date/Time
implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
```

## Performance Considerations

### Database Optimization
- Proper indexing on foreign keys
- Efficient queries with Flow for reactive updates
- Cascade deletes for data consistency

### UI Performance
- LazyColumn for large lists
- Stable keys for list items
- Minimal recomposition through proper state management

### Memory Management
- Proper lifecycle awareness
- Coroutine scope management
- Resource cleanup in services

## Security Considerations

### Data Protection
- Local SQLite database with Android encryption
- No network communication (offline-first)
- Proper permission handling

### Privacy
- All data stored locally
- No analytics or tracking
- User control over data retention

## Testing Strategy

### Unit Testing
- Repository layer testing with Room in-memory database
- ViewModel testing with test coroutines
- Utility function testing

### Integration Testing
- Database migration testing
- Notification system testing
- Background work testing

### UI Testing
- Compose UI testing with test rules
- Navigation testing
- Accessibility testing

## Deployment

### Debug Build
- Includes debugging symbols
- Allows installation from unknown sources
- Suitable for development and testing

### Release Build
- ProGuard obfuscation and optimization
- Signed APK for distribution
- Optimized for production use

### APK Analysis
- Current size: ~11MB
- Main contributors: Compose runtime, Room database
- Optimization opportunities: R8 shrinking, unused resource removal

## Future Improvements

### Code Quality
- Add comprehensive unit tests
- Implement UI tests
- Add static analysis tools (detekt, ktlint)

### Architecture Enhancements
- Implement proper dependency injection (Hilt)
- Add use cases layer for complex business logic
- Implement proper error handling and loading states

### Performance Optimizations
- Implement database pagination
- Add image caching for future media support
- Optimize notification scheduling

### Feature Additions
- Data export/import functionality
- Analytics and insights
- Backup and sync capabilities
- Widget support

## Troubleshooting

### Common Development Issues

#### Build Errors
- Ensure correct Kotlin version compatibility
- Check Android SDK installation
- Verify Gradle wrapper configuration

#### Runtime Issues
- Check notification permissions
- Verify database migrations
- Monitor background task execution

#### Performance Issues
- Profile with Android Studio profiler
- Check for memory leaks
- Optimize database queries

### Debugging Tools
- Android Studio debugger
- Database Inspector for Room
- Layout Inspector for Compose
- Network Inspector (not applicable for this offline app)

---

This technical documentation provides a comprehensive overview of the MoodTracker app's architecture, implementation details, and development considerations. It serves as a reference for developers working on the project or similar applications.
