package com.example.screentimemanager.data.local.usage

import androidx.room.Entity
import androidx.room.ForeignKey
import com.example.screentimemanager.data.local.app.App

/**
 * the user's usage data for a given date
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
@Entity(
    tableName = "Usage",
    primaryKeys = ["appName", "day", "month", "year"],
    foreignKeys = [
        ForeignKey(
            entity = App::class,
            parentColumns = ["appName"],
            childColumns = ["appName"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Usage(
    val appName: String,
    val day: Int,
    val month: Int,
    val year: Int,
    val usage: Long
)
