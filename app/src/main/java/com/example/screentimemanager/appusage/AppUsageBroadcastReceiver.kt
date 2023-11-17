package com.example.screentimemanager.appusage

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AppUsageBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // starts the AppUsageService on device boot
            val serviceIntent = Intent(context, AppUsageService::class.java)
            context.startForegroundService(serviceIntent)
        }
    }
}