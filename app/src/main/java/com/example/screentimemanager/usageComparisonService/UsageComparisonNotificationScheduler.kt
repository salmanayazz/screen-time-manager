package com.example.screentimemanager.usageComparisonService

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.screentimemanager.workers.UsageNotificationWorker
import java.util.concurrent.TimeUnit
import android.content.Context

class UsageComparisonNotificationScheduler(private val context: Context) {
    fun scheduleUsageNotificationWorker(userEmail: String) {
        // Create a WorkRequest for the UsageNotificationWorker
        val notificationWorkRequest = OneTimeWorkRequestBuilder<UsageNotificationWorker>()
            .setInitialDelay(5, TimeUnit.MINUTES) // Schedule for 5 minutes later
            .setInputData(workDataOf("userEmail" to userEmail)) // Pass userEmail to the Worker
            .build()

        // Enqueue the WorkRequest
        WorkManager.getInstance(context).enqueue(notificationWorkRequest)
    }
}