package com.example.screen_time_manager

import android.app.ActivityManager
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
import java.util.Calendar
import java.util.SortedMap
import java.util.Timer
import java.util.TimerTask
import java.util.TreeMap


class AppUsageService : Service() {
    private lateinit var usageStatsManager: UsageStatsManager
    private val overlayView by lazy { LayoutInflater.from(this).inflate(R.layout.time_limit_reached_overlay, null) }
    private val windowManager by lazy { getSystemService(Context.WINDOW_SERVICE) as WindowManager }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        // request usage access permission if not granted
        if (!isUsageAccessPermissionGranted()) {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        // get draw on top permissions
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        val timer = Timer()
        val handler = Handler(Looper.getMainLooper())

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

        return START_STICKY
    }

    /**
     * returns true if the current open app's time limit has been reached, otherwise false
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
            if (appFound.totalTimeInForeground > (1000) && appFound.packageName != "com.example.screen_time_manager") {
                return true;
            }
        } else {
            println("timeLimitIsReached: app does not exist")
        }

        return false;
    }

    /**
     * returns when today started in milisecs
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
     * queries the app usage statistics to see if permission is given
     * if permission is given, returns true, otherwise false
     */
    private fun isUsageAccessPermissionGranted(): Boolean {
        val currentTime = System.currentTimeMillis()
        val startTime = getStartOfDay(currentTime)


        val usageStatsList = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, startTime, currentTime
        )
        // check if the list is empty
        // TODO: not the best way to check if permission is given
        return usageStatsList.isNotEmpty()

    }

    /**
     * displays the time limit reached overlay over the current app
     */
    private fun showOverlay() {


        if (!overlayView.isAttachedToWindow) {
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )

            windowManager.addView(overlayView, params)
        }

    }

    private fun removeOverlay() {
        if (overlayView.isAttachedToWindow) {
            windowManager.removeView(overlayView)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    /**
     * returns the app currently opened by the user
     * 
     * reference:
     * https://stackoverflow.com/a/38829083
     */
    private fun getCurrentAppInUse(): String {
        return if (VERSION.SDK_INT >= 21) {
            var currentApp: String? = null
            val usm = this.getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
            val time = System.currentTimeMillis()
            val applist =
                usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time)
            if (applist != null && applist.size > 0) {
                val mySortedMap: SortedMap<Long, UsageStats> = TreeMap()
                for (usageStats in applist) {
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
}