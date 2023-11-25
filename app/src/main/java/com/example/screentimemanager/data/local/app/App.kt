package com.example.screentimemanager.data.local.app

import androidx.room.Entity

/**
 * @param appName
 * name of the app, for example: "com.android.chrome"
 * @param hasLimit
 * set to true when time limit feature is enabled
 * @param timeLimit
 * total time limit in for the app in milliseconds
 */
@Entity(
    tableName = "App",
    primaryKeys = ["appName"]
)
data class App(
    val appName: String,
    val hasLimit: Boolean = false,
    val timeLimit: Long = 0
)
