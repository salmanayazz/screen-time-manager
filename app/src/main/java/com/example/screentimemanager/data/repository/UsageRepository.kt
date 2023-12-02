package com.example.screentimemanager.data.repository

import android.content.pm.PackageManager
import androidx.lifecycle.LiveData
import com.example.screentimemanager.data.firebase.usage.UsageFirebase
import com.example.screentimemanager.data.firebase.usage.UsageFirebaseDao
import com.example.screentimemanager.data.local.usage.Usage
import com.example.screentimemanager.data.local.usage.UsageDao
import kotlinx.coroutines.tasks.await

open class UsageRepository(
    private val usageFirebaseDao: UsageFirebaseDao,
    private val usageDao: UsageDao
) {

    /**
     * returns for usage data for the given user on the given date
     * @param day
     * the day of the month
     * @param month
     * the month of the year
     * @param year
     * the year
     * @return
     * return list of the user's usage data
     */
    fun getUsageData(day: Int, month: Int, year: Int) : List<Usage> {
        return usageDao.getUsageData(day, month, year)
    }


    /**
     * returns for usage data for the given user on the given date
     * @param email
     * email of the user whose usage data is being retrieved
     * @param day
     * the day of the month
     * @param month
     * the month of the year
     * @param year
     * the year
     * @return
     * return list of the user's usage data
     */
    suspend fun getUsageData(email: String, day: Int, month: Int, year: Int): List<UsageFirebase> {
        return usageFirebaseDao.getUsageData(email, day, month, year)
    }

    /**
     * sets the usage data for the given user on the given date
     * if usage data already exists, it will be replaced
     * @param appName
     * the app to add to the user's list of apps
     * @param appNameLabel
     * the readable application name ex "Chrome"
     * @param day
     * the day of the month
     * @param month
     * the month of the year
     * @param year
     * the year
     * @param usage
     * the usage time in milliseconds
     */
    suspend fun setUsageData(appName: String, appNameLabel: String, day: Int, month: Int, year: Int, usage: Long) {
        // Update local database
        usageDao.setUsageData(appName, day, month, year, usage)
        // Update Firebase data
        usageFirebaseDao.setUsageData(appName, appNameLabel, day, month, year, usage)
    }
}