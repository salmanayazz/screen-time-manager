package com.example.screentimemanager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import com.example.screentimemanager.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.SortedMap
import java.util.Timer
import java.util.TimerTask
import java.util.TreeMap

class AppUsageService : Service() {
    private val usageStatsManager by lazy { getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager }
    private val overlayView by lazy { LayoutInflater.from(this).inflate(R.layout.time_limit_overlay, null) }
    private val windowManager by lazy { getSystemService(Context.WINDOW_SERVICE) as WindowManager }
    private val serviceScope = CoroutineScope(Dispatchers.Main)

    // record previously opened app and when it was opened
    private var previousApp: String = ""
    private var previousAppTimestamp: Long = 0L

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        requestPermissions()
        startAppTracking()

        previousApp = getCurrentAppInUse()
        previousAppTimestamp = System.currentTimeMillis()

        startForeground(
            1001,
            createForegroundNotification().build()
        )
        return super.onStartCommand(intent, flags, startId)
    }
    
    

    /**
     * requests the permissions needed for the service to work
     * includes the usage access permission and the overlay permission
     */
    private fun requestPermissions() {
        if (!isUsageAccessPermissionGranted()) {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    /**
     * tracker that checks if the time limit for the current app is reached continuously
     * if the time limit is reached, it shows the overlay view
     * if the time limit is not reached, it removes the overlay view
     */
    private fun startAppTracking() {
        val timer = Timer()
        val handler = Handler(Looper.getMainLooper())
        
        previousApp = getCurrentAppInUse()
        previousAppTimestamp = System.currentTimeMillis()

        serviceScope.launch {
            // check if the current app limit has been reached every second
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    if (timeLimitIsReached()) {
                        handler.post {
                            showOverlay()
                        }
                    } else {
                        handler.post {
                            removeOverlay()
                        }
                    }
                }
            }, 0, 1000)
        }
    }

    /**
     * checks if the time limit for the current app is reached
     * @return true
     * if the time limit for the current app is reached
     */
    private fun timeLimitIsReached(): Boolean {
        val usageStatsList = getTodaysUsageStats()

        var currentApp = getCurrentAppInUse()
        var appUsage = usageStatsList.find() {
            it.packageName == currentApp
        }

        if (appUsage != null) {
            var totalTime = appUsage.totalTimeInForeground

            // if app was already opened since last poll, use a timer to calculate
            // its current usage since UsageStatsManager only updates its times when a user
            // exits and reenters the app
            if (previousApp == currentApp) {
                totalTime +=  System.currentTimeMillis() - previousAppTimestamp
            } else {
                previousApp = currentApp
                previousAppTimestamp = System.currentTimeMillis()
            }

            Log.i(TAG, "App $currentApp usage is $totalTime")

            // TODO: remove this hardcoded (1000 * 20) value
            if (totalTime > (1000 * 20) && appUsage.packageName != this.packageName) {
                return true;
            }
        } else {
            Log.e(TAG, "App $currentApp does not exist")
        }

        return false;
    }

    /**
     * creates the foreground notification
     * @return
     * the notification builder
     */
    private fun createForegroundNotification(): Notification.Builder {
        val CHANNEL_ID = "Foreground Service Notification"
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_ID,
            NotificationManager.IMPORTANCE_HIGH
        )

        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        return Notification.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name) + " is running")
            .setSmallIcon(R.drawable.ic_launcher_background)
    }
    
    /**
     * queries the UsageStatsManager for the usage statistics of the current day
     * @return
     * a list of UsageStats that contain app usage data
     */
    private fun getTodaysUsageStats(): List<UsageStats> {
        val currentTime = System.currentTimeMillis()
        
        // get time in milliseconds when today started
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTime
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis

        // query the app usage statistics for the specified time range
        return usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, startTime, currentTime
        )
        
    }

    /**
     * queries the UsageStatsManager for the usage statistics of the current day
     * if the list is empty, it expects the the permissions to not be granted
     * @return true
     * if the permissions are granted
     */
    private fun isUsageAccessPermissionGranted(): Boolean {
        // TODO: might not the best way to check if permission is given
        return getTodaysUsageStats().isNotEmpty()
    }

    /**
     * adds the overlay view from the resource 
     * file time_limit_overlay.xml to the window manager
     */
    private fun showOverlay() {
        try {
            if (!overlayView.isAttachedToWindow) {
                val params = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT, // TODO: temporarily set to WRAP_CONTENT
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT
                )
                windowManager.addView(overlayView, params)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * removes the overlay view (time_limit_overlay.xml) from the window manager
     */
    private fun removeOverlay() {
        try {
            if (overlayView.isAttachedToWindow) {
                windowManager.removeView(overlayView)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    /**
     * returns the app currently opened by the user (ex: "com.android.chrome")
     * @return
     * the package name of the app currently opened by the user as a String
     * 
     * reference:
     * https://stackoverflow.com/a/38829083
     */
    private fun getCurrentAppInUse(): String {
        var currentApp: String? = null
        val usageStatsManager = this.getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        val time = System.currentTimeMillis()
        val appList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time)
        if (appList != null && appList.size > 0) {
            val sortedMap: SortedMap<Long, UsageStats> = TreeMap()
            for (usageStats in appList) {
                sortedMap!![usageStats.lastTimeUsed] = usageStats
            }
            if (sortedMap != null && !sortedMap.isEmpty()) {
                currentApp = sortedMap[sortedMap.lastKey()]!!.packageName
            }
        }
        Log.i(TAG, "Current App in foreground is: $currentApp")
        return currentApp!!
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}