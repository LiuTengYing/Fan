0# Fan Control Application

This is an Android-based device fan and amplifier control application that provides intelligent temperature monitoring and device control functions, helping users better manage their device cooling systems.

## Core Features

### Smart Temperature Control System
- **Auto/Manual Mode Switch**:
  - Automatically controls fan based on CPU temperature in auto mode
  - Allows full device control in manual mode
  - One-click mode switching for flexible scenario adaptation

### Temperature Monitoring and Display
- **Real-time Temperature Monitoring**:
  - Continuous CPU temperature monitoring
  - Supports Celsius/Fahrenheit unit switching
  - Large font display for clear visibility

### Visual Interface
- **Dynamic Status Display**:
  - Real-time fan operation animation
  - LED indicators showing working status
  - Intuitive control switches

### Device Control
- **Dual Control Switches**:
  - Left switch controls the fan
  - Right switch controls the amplifier
  - Supports independent control of each device

### Custom Settings
- **Temperature Threshold Settings**: Customizable temperature trigger points
- **Unit Switching**: Freedom to switch temperature units
- **Status Memory**: Remembers user preferences

## Technical Specifications

- **Development Language**: Kotlin 1.9.0
- **UI Framework**: Jetpack Compose
- **Minimum Android Version**: Android 8.1 (API 27)
- **Target Android Version**: Android 14 (API 34)
- **Build Tool**: Gradle 8.7

## System Requirements

- Android 8.1 or higher
- Device must support the following permissions:
  - Read external storage
  - Access system settings
  - Access temperature service

## Version Information

Current Version: 2.1.3

## Project Structure

```
├── app/                    # Main application module
│   ├── src/               # Source code
│   ├── libs/              # Local dependency libraries
│   └── build.gradle.kts   # App-level build configuration
├── gradle/                # Gradle configuration
└── build.gradle.kts       # Project-level build configuration
```

## Dependencies

- AndroidX Core KTX
- Jetpack Compose
- Material3
- ConstraintLayout Compose
- Custom amplifier control modules (auto2.jar, final.jar)

## Development Environment Setup

1. Clone the project locally
2. Open project in Android Studio
3. Sync Gradle files
4. Run the application

## License

[Add license information]

## Contribution Guidelines

Issues and Pull Requests are welcome to help improve the project.

## Contact Information

Email: Ltydiwyzh@163.com