package com.example.screentimemanager.data.repository

import com.example.screentimemanager.data.firebase.usage.UsageFirebase
import com.example.screentimemanager.data.firebase.usage.UsageFirebaseDao
import com.example.screentimemanager.data.local.usage.Usage
import com.example.screentimemanager.data.local.usage.UsageDao
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UsageRepository(
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
    suspend fun getUsageData(day: Int, month: Int, year: Int) : List<Usage> {
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
     * @param appName
     * the app to add to the user's list of apps
     */
    suspend fun setUsageData(appName: String, date: String, usage: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            usageFirebaseDao.setUsageData(appName, date, usage)
        }
    }
}