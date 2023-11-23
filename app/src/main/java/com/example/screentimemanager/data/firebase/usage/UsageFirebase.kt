package com.example.screentimemanager.data.firebase.usage

/**
 * (PK = userEmail + appName + date)
 * a user's usage data for a given date
 * @param userEmail
 * reference to User table 
 * (FK to User table)
 * @param appName
 * the name of the app 
 * (FK to App table)
 * @param day
 * the day of the month
 * @param month
 * the month of the year
 * @param year
 * the year
 * @param usage
 * the number of milliseconds the app was used on the given date
 */
data class UsageFirebase (
    val userEmail: String,
    val appName: String,
    val day: Int,
    val month: Int,
    val year: Int,
    val usage: Long
)
