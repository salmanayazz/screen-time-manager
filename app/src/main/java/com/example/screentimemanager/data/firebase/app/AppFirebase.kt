package com.example.screentimemanager.data.firebase.app

/**
 * (PK = userEmail + appName)
 * an app associated with a user and its time limit
 * @param userEmail
 * the email of the user associated with the app
 * @param appName
 * name of the app, for example: "com.android.chrome" (user + appName are PK)
 * @param hasLimit
 * set to true when time limit feature is enabled
 * @param timeLimit
 * total time limit in for the app in milliseconds
 */
data class AppFirebase (
    val userEmail: String,
    val appName: String,
    val hasLimit: Boolean,
    val timeLimit: Long
)
