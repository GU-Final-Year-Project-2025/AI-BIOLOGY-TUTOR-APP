Open the Project:
Open Android Studio
Select "Open an Existing Project"
Navigate to the [AIBIOTUTOR](c:\Users\SEMUNYU ALOYZIOUS\AndroidStudioProjects\AIBIOTUTOR) folder and select it
Project Configuration:

The project uses:
compileSdk = 35
minSdk = 23
Java Version 11
Gradle with Kotlin DSL (.kts files)
Build the Project:
# On Windows, use:
.\gradlew build

# On Linux/Mac, use:
./gradlew build
Run the Project:
Connect an Android device (API level 23 or higher) or start an emulator
Click the "Run" button (green play icon) in Android Studio
Select your target device and click OK
Note: Make sure you have:

Android Studio installed
JDK 21 installed (as specified in [misc.xml](c:\Users\SEMUNYU ALOYZIOUS\AndroidStudioProjects\AIBIOTUTOR.idea\misc.xml))
Required SDK platforms (API 35) installed through SDK Manager
All the dependencies will be downloaded automatically through Gradle
The main application is defined in the [app](c:\Users\SEMUNYU ALOYZIOUS\AndroidStudioProjects\AIBIOTUTOR\app) module with the package name com.example.aibiotutor.
