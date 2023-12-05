package com.example.screentimemanager.usageComparisonService

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.screentimemanager.workers.UsageNotificationWorker
import java.util.concurrent.TimeUnit
import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import java.util.Calendar

class UsageComparisonNotificationScheduler(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)

    fun scheduleUsageNotificationWorker(userEmail: String) {
        // Get the current date
        val currentDate = getCurrentDate()

        // Check if the notification was already scheduled for today
        val lastScheduledDate = sharedPreferences.getString("lastScheduledDate", null)
        if (currentDate == lastScheduledDate) {
            Log.d("UsageComparisonNotificationScheduler", "Notification already scheduled for today");
            Log.d("UsageComparisonNotificationScheduler", "Current date: $lastScheduledDate")
            return
        }

        // Create a WorkRequest for the UsageNotificationWorker
        val notificationWorkRequest = OneTimeWorkRequestBuilder<UsageNotificationWorker>()
            .setInitialDelay(5, TimeUnit.SECONDS)
            .setInputData(workDataOf("userEmail" to userEmail))
            .build()

        // Use a unique name for your work request
        val uniqueWorkName = "daily_usage_notification"

        // Enqueue the WorkRequest as unique work
        WorkManager.getInstance(context)
            .enqueueUniqueWork(uniqueWorkName, ExistingWorkPolicy.KEEP, notificationWorkRequest)

        // Update the last scheduled date
        with(sharedPreferences.edit()) {
            putString("lastScheduledDate", currentDate)
            apply()
        }
    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        return "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}"
    }
}