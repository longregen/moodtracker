# MoodTracker Android App

A comprehensive Android application that prompts users 4-5 times daily with customizable questions to track mood, habits, and personal insights. Built with modern Android development practices using Jetpack Compose and Material Design 3.

## Features

### Core Functionality
- **Customizable Questions**: Create, edit, and manage different types of questions
- **Multiple Answer Types**: Support for text, yes/no, number, and multiple-choice responses
- **Smart Notifications**: Configurable daily notifications (4-5 times per day)
- **Comprehensive Logging**: Three view modes for analyzing your responses
- **Modern UI**: Material Design 3 with dark/light mode support

### Question Types
1. **Text Input**: Free-form text responses
2. **Yes/No**: Simple binary choices with optional additional notes
3. **Number**: Numeric input for quantifiable metrics
4. **Multiple Choice**: Pre-defined options with additional text logging

### Notification System
- **Scheduled Notifications**: Set up to 5 daily notification times
- **Snooze Functionality**: Delay notifications when not convenient
- **Background Processing**: Uses WorkManager for reliable scheduling
- **Boot Persistence**: Notifications resume after device restart

### Logging & Analytics
- **Timeline View**: Chronological list of all responses
- **Questions View**: Grouped responses by question type
- **Particular Question View**: Deep dive into specific question history
- **Data Versioning**: Track question modifications over time

### Configuration
- **Question Management**: Add, edit, delete, and hide questions
- **Notification Settings**: Customize notification times
- **Data Consistency**: Warnings when modifying questions with existing data

## Technical Architecture

### Technology Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material Design 3
- **Database**: Room (SQLite)
- **Background Tasks**: WorkManager
- **Notifications**: Android Notification API
- **Date/Time**: kotlinx-datetime
- **Architecture**: MVVM with Repository pattern

### Project Structure
```
app/src/main/java/com/moodtracker/
├── data/
│   ├── database/          # Room database, DAOs
│   ├── models/           # Data classes
│   └── repository/       # Data access layer
├── services/             # Background services, notifications
├── ui/
│   ├── answer/          # Question answering interface
│   ├── config/          # Configuration screens
│   ├── logs/            # Logging and analytics
│   ├── main/            # Main dashboard
│   └── theme/           # UI theming
└── utils/               # Utility functions
```

### Database Schema
- **Questions**: Store question definitions, types, and options
- **Answers**: Store user responses with timestamps
- **NotificationSchedule**: Manage notification timing

## Installation

### Prerequisites
- Android device running API level 24+ (Android 7.0)
- Approximately 15MB of storage space

### Installation Steps
1. Download the `app-debug.apk` file
2. Enable "Install from Unknown Sources" in Android settings
3. Open the APK file and follow installation prompts
4. Grant necessary permissions when prompted

### Required Permissions
- **POST_NOTIFICATIONS**: Display notification prompts
- **SCHEDULE_EXACT_ALARM**: Precise notification timing
- **RECEIVE_BOOT_COMPLETED**: Resume notifications after restart
- **WAKE_LOCK**: Ensure notifications work when device is sleeping

## Usage Guide

### Initial Setup
1. **Launch the app** and review the welcome screen
2. **Configure questions** by tapping the configuration tab
3. **Set notification times** in the notification settings
4. **Answer your first question** to test the system

### Daily Usage
1. **Respond to notifications** as they appear throughout the day
2. **Use the snooze feature** if you need to delay a response
3. **Review your progress** in the logs section
4. **Adjust questions** as your tracking needs evolve

### Question Management
- **Add new questions**: Use the "+" button in configuration
- **Edit existing questions**: Tap any question to modify
- **Hide questions**: Temporarily disable without losing data
- **Delete questions**: Permanently remove (with data warning)

### Viewing Your Data
- **Timeline**: See all responses in chronological order
- **By Question**: Group responses to analyze patterns
- **Individual Questions**: Deep dive into specific metrics

## Customization

### Question Examples
- **Mood Tracking**: "How is your mood?" (Multiple choice: Great, Good, Okay, Poor, Terrible)
- **Exercise**: "Have you exercised today yet?" (Yes/No with notes)
- **Screen Time**: "How many hours were you sitting at the computer?" (Number)
- **Reflection**: "Any reminders you would like for the future?" (Text)

### Notification Timing
- **Morning Check-in**: 9:00 AM
- **Midday Pulse**: 1:00 PM
- **Afternoon Reflection**: 4:00 PM
- **Evening Review**: 7:00 PM
- **Night Thoughts**: 9:00 PM

## Development

### Building from Source
```bash
# Clone the repository
git clone <repository-url>
cd MoodTracker

# Set up Android SDK
export ANDROID_HOME=/path/to/android-sdk
export JAVA_HOME=/path/to/java-17

# Build the app
./gradlew assembleDebug
```

### Key Dependencies
- `androidx.compose.material3:material3:1.2.0`
- `androidx.room:room-runtime:2.6.1`
- `androidx.work:work-runtime-ktx:2.9.0`
- `org.jetbrains.kotlinx:kotlinx-datetime:0.5.0`

### Architecture Decisions
- **Jetpack Compose**: Modern, declarative UI framework
- **Room Database**: Type-safe SQLite abstraction
- **WorkManager**: Reliable background task execution
- **Material Design 3**: Consistent, accessible design system

## Privacy & Data

### Data Storage
- All data is stored locally on your device
- No data is transmitted to external servers
- Database is encrypted using Android's built-in security

### Data Export
- Currently, data export is not implemented
- Future versions may include CSV/JSON export functionality

## Troubleshooting

### Common Issues
1. **Notifications not appearing**: Check notification permissions and battery optimization settings
2. **App crashes on startup**: Ensure Android version compatibility (API 24+)
3. **Questions not saving**: Check available storage space

### Performance Tips
- **Regular cleanup**: Delete old questions you no longer need
- **Optimize notifications**: Avoid too many daily notifications
- **Battery settings**: Exclude app from battery optimization for reliable notifications

## Future Enhancements

### Planned Features
- **Data Export**: CSV/JSON export functionality
- **Analytics Dashboard**: Visual charts and trends
- **Reminder Customization**: More flexible notification options
- **Backup & Sync**: Cloud backup capabilities
- **Widget Support**: Home screen widgets for quick responses

### Contributing
This is a demonstration project. For production use, consider:
- Adding comprehensive unit tests
- Implementing data encryption
- Adding accessibility improvements
- Optimizing for different screen sizes
- Adding localization support

## License

This project is created as a demonstration of Android development capabilities. Feel free to use and modify as needed.

## Support

For technical questions or issues, please refer to the troubleshooting section above or consult Android development documentation for similar implementations.

---

**Version**: 1.0.0  
**Build Date**: September 28, 2025  
**Target SDK**: Android 14 (API 34)  
**Minimum SDK**: Android 7.0 (API 24)  
**APK Size**: ~11MB
