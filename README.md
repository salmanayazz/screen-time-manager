# Screen Time Manager

A native android app that helps you manage your screen time. This app is built using:

- Kotlin
- Room (SQLite)
- Firebase:
  - Authentication
  - Realtime Database
- Testing:
  - JUnit
  - Espresso
  - Mockito

## Features

### Set App Usage Limits

<img src="https://github.com/salmanayazz/my-runs/assets/80385814/b69364f1-55c8-435e-acf6-2ef731a8f0ca" alt="Set app usage limits" align="left" width="200"/>

<img src="https://github.com/salmanayazz/my-runs/assets/80385814/0db4a8c2-fcd4-48b5-b002-12f731e800c1" alt="App usage limit exceeded" align="left" width="200"/>

- You can set app usage limits for each app.
- When you exceed the limit, the app will be locked, and you will not be able to use it until the next day.

<br clear="left"/>

### Authentication

<img src="https://github.com/salmanayazz/my-runs/assets/80385814/9ea53732-7fa4-40dd-8cf1-3f77267c00c2" alt="Authentication" align="left" width="200"/>

- Sign up and log in to the app using your email address and password.
- Credentials are stored securely using Firebase Authentication.

<br clear="left"/>

### Add Friends

<img src="https://github.com/salmanayazz/my-runs/assets/80385814/0de8daf7-1087-4a29-b301-ae391fea5359" alt="Send friend requests" align="left" width="200"/>

<img src="https://github.com/salmanayazz/my-runs/assets/80385814/7c843552-0115-472a-be77-4ed8cc3ffef3" alt="Accept friend requests" align="left" width="200"/>

- You can add friends by searching for their username and sending them a friend request.
- The other user will be able to accept or reject your request.
- Recipient will be notified of the request via a notification.

<br clear="left"/>

### View Your Friends' App Usage

<img src="https://github.com/salmanayazz/my-runs/assets/80385814/5ff59001-c3b6-4e26-8dfe-51946a955278" alt="View your friends' app usage" align="left" width="200"/>

- Once a friend request is accepted, you will be able to view your friends' app usage.
- You can view the app usage for the current day, as well as the previous days.
- Can also view a breakdown of the usage by app for each friend on a specific day.

<br clear="left"/>

## Building the app

### Prerequisites

Before you can start building the app, you need to add a `google-services.json` file to the `app` directory. This file can be obtained by creating a new Firebase project and adding an Android app to it. For more information, see [Add Firebase to your Android project](https://firebase.google.com/docs/android/setup).

The app uses the following Firebase services:

- Authentication
- Realtime Database

### Building

To build the app, run the following command in the root directory of the project:

./gradlew assembleDebug

This will generate an APK file in the `app/build/outputs/apk/debug` directory.

## Testing

### Unit tests

To run the unit tests, run the following command in the root directory of the project:

./gradlew test

### Instrumented tests

To run the instrumented tests, you need to have an Android device connected to your computer or an emulator running. Then, run the following command in the root directory of the project:

./gradlew connectedCheck

## GitHub Actions Workflow

The project uses GitHub Actions to build and run the unit tests on every pull request to the `main` branch. It also builds and publishes a debug APK file to the `releases` page of the repository whenever a new tag is created.

### Required Secrets

- `GOOGLE_SERVICES_JSON`
  - Contents of the `google-services.json` file encoded in base64.
