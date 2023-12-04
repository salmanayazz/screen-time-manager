package com.example.screentimemanager.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.screentimemanager.usageComparisonService.UsageComparisonManager
import com.example.screentimemanager.data.firebase.usage.UsageFirebaseDao
import com.example.screentimemanager.data.firebase.user.UserFirebaseDao
import com.example.screentimemanager.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar
import java.util.Date

class UsageNotificationWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
        val usageFirebaseDao = UsageFirebaseDao(databaseReference)
        val userFirebaseDao = UserFirebaseDao(databaseReference)
        val usageComparisonManager = UsageComparisonManager(usageFirebaseDao, userFirebaseDao)
        // Try to get userEmail from inputData first
        val inputDataEmail = inputData.getString("userEmail")

        // Fallback to the currently logged-in user's email if inputDataEmail is not available
        val userEmail = inputDataEmail ?:"abc123@gmail.com"

        // If userEmail is still null, return failure
        if (userEmail == null) {
            Log.d("UsageNotificationWorker", "Notification In the worker class, userEmail $userEmail is null. Returning failure")
            return Result.failure()
        }

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val message = userEmail?.let {
            usageComparisonManager.findLowestUsageUserAndFormatMessage(
                it,
                day,
                month,
                year
            )
        }
        Log.d("UsageNotificationWorker", "In the worker class, after setting, message. Sending notification: $message")
        if (message != null) {

            createAndSendNotification(message)
        }

        return Result.success()
    }

    private fun createAndSendNotification(message: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create Notification Channel for API 26+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel("usage_stats_channel", "Usage Stats", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Notification channel for usage stats"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification
        val notification = NotificationCompat.Builder(applicationContext, "usage_stats_channel")
            .setContentTitle("Daily Usage Summary")
            .setContentText(message)
            .setSmallIcon(R.drawable.baseline_email_24)
            .build()

        // Send the notification
        notificationManager.notify(7721, notification)
        Log.d("UsageNotificationWorker", "In the worker class, after sending message. Sending notification: $message")
    }

}
