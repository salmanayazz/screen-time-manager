package com.example.screentimemanager

import android.app.ActivityManager
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
import android.os.Build.VERSION
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import kotlinx.coroutines.GlobalScope
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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        requestPermissions()
        startAppLimitTimer()


        startForeground(
            1001,
            createForegroundNotification().build()
        )
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * requests the permissions needed for the service to work
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
     * timer that checks if the time limit for the current app is reached continuously
     * if the time limit is reached, it shows the overlay view
     * if the time limit is not reached, it removes the overlay view
     */
    private fun startAppLimitTimer() {  
        val timer = Timer()
        val handler = Handler(Looper.getMainLooper())

        GlobalScope.launch {
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
        val currentTime = System.currentTimeMillis()
        val startTime = getStartOfDay(currentTime)

        // query the app usage statistics for the specified time range
        val usageStatsList = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, startTime, currentTime
        )

        var curApp = getCurrentAppInUse()
        var appFound = usageStatsList.find() {
            it.packageName == curApp
        }

        if (appFound != null) {
            println("timeLimitIsReached: app usage ${appFound.totalTimeInForeground}")
            // return true if open longer than 1 sec and not this app itself
            // TODO: change this to use app specific limit
            if (appFound.totalTimeInForeground > (1000) && appFound.packageName != this.packageName) {
                return true;
            }
        } else {
            println("timeLimitIsReached: app does not exist")
        }

        return false;
    }

    /**
     * @param timeMillis
     * the time in milliseconds
     * @return 
     * the start of the day in milliseconds of the provided time
     */
    private fun getStartOfDay(timeMillis: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeMillis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    /**
     * queries the UsageStatsManager for the usage statistics of the current day
     * if the list is empty, it expects the the permissions to not be granted
     * @return true
     * if the permissions are granted
     */
    private fun isUsageAccessPermissionGranted(): Boolean {
        val currentTime = System.currentTimeMillis()
        val startTime = getStartOfDay(currentTime)


        val usageStatsList = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, startTime, currentTime
        )

        // TODO: might not the best way to check if permission is given
        return usageStatsList.isNotEmpty()

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
        return if (VERSION.SDK_INT >= 21) {
            var currentApp: String? = null
            val usm = this.getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
            val time = System.currentTimeMillis()
            val appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time)
            if (appList != null && appList.size > 0) {
                val mySortedMap: SortedMap<Long, UsageStats> = TreeMap()
                for (usageStats in appList) {
                    mySortedMap!![usageStats.lastTimeUsed] = usageStats
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap[mySortedMap.lastKey()]!!.packageName
                }
            }
            Log.e(TAG, "Current App in foreground is: $currentApp")
            currentApp!!
        } else {
            val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
            val mm = manager.getRunningTasks(1)[0].topActivity!!.packageName
            Log.e(TAG, "Current App in foreground is: $mm")
            mm
        }
    }

    override fun onDestroy() {
        // restarts the service if destroyed
        val intent = Intent(this, AppUsageService::class.java)
        startForegroundService(intent)
        super.onDestroy()
    }
}