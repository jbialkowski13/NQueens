# NQueens Application

## App Demo
[App demo](/assets/app_run.mp4)

## How to Test, Assemble, and Run

### Test

To run unit tests, execute the following command:
    ```bash
    ./gradlew test

### Assemble
To assemble the debug APK, use:
    ```bash
    ./gradlew assembleDebug

### Assemble and run
To build and install the debug APK on a connected device, run:
    ```bash
    ./gradlew installDebug

Alternatively, open the project in Android Studio, select a device/emulator, and click the "Run" button.

## Architecture decisions

### Single-Module Design
The application is designed as a single module to maintain simplicity and a lean structure.
However, the packaging structure is modular, allowing for easy extraction of separate modules in the future.
Separate modules in future would simplify the approach for creating Fakes for some of the classes in the project. 
This could be achieved by utilising [text fixtures](https://docs.gradle.org/current/userguide/java_testing.html#sec:java_test_fixtures).

### MVVM Pattern
The app follows the Model-View-ViewModel (MVVM) architecture to ensure a clear separation of concerns.

### Jetpack Compose
The UI is built entirely with Jetpack Compose, enabling deep integration with Kotlin Coroutines and other Android components. Compose's live preview feature allows for rapid UI development without running the app.

#### State Manangement in ViewModel
ViewModels expose MutableState<T> directly instead of StateFlow. This decision avoids issues with TextField state management, as described in the following references:
- https://developer.android.com/develop/ui/compose/text/user-input#state-practices
- https://medium.com/androiddevelopers/effective-state-management-for-textfield-in-compose-d6e5b070fbe5

While the app currently does not use TextField, this approach ensures consistency and avoids future confusion.

### Unidirectional Data Flow (UDF)
ViewModels follow a Unidirectional Data Flow pattern by exposing immutable state to the UI and providing methods like onBackClick() for user interactions.

### Dependency Injection
The app uses Dagger with Hilt for dependency injection, leveraging its seamless integration with Android components such as Activity, ViewModel, and Navigation.

### Navigation
The app utilizes Jetpack Compose Navigation with type safety. A custom Navigator wrapper enables ViewModels to interact with navigation and simplifies testing. This approach is scalable for future modularization, as each module can contribute routes to a shared Hilt graph.

### Room
The app uses Room to persist game scores.

### Testing
The app has comprehensive unit test coverage using JUnit5, Turbine, and AssertK. Tests cover ViewModels, game logic, formatters, and utilities to ensure a high-quality user experience.
