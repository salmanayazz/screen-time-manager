package com.example.screentimemanager.util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import java.time.LocalDate
import java.util.TimeZone

object Util {


    /**
     * Returns a list of non-system applications installed on the user's phone.
     *
     * @param context The context of the application or activity.
     * @return A list of ApplicationInfo objects representing user-installed applications.
     */
    fun getApplicationsList (context: Context): List<ApplicationInfo>{
        //TODO : this function is time consuming , it needs to be done inside a coroutines NOT UI threads
        val appsList = context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val mutableList:MutableList<ApplicationInfo>  = mutableListOf()
        for (app in appsList){
            if (app.flags and  ApplicationInfo.FLAG_SYSTEM == 0) {
                val appName = app.loadLabel(context.packageManager).toString()
                if(appName.trim().isNotEmpty()){
                    mutableList.add(app)
                }
            }
        }
        return mutableList
    }

    /**
     * gets the current date
     * @return
     * a triple containing the day, month, and year
     */
    fun getCurrentDate(): Triple<Int, Int, Int> {
        // Get the user's time zone
        val userTimeZone = TimeZone.getDefault().toZoneId()

        // Get the current date in the user's time zone
        val currentDate = LocalDate.now(userTimeZone)

        // Extract day, month, and year
        val day = currentDate.dayOfMonth
        val month = currentDate.monthValue
        val year = currentDate.year

        return Triple(day, month, year)
    }
}