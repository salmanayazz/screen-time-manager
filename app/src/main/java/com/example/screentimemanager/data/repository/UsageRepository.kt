package com.example.screentimemanager.data.repository

import com.example.screentimemanager.data.firebase.usage.UsageFirebase
import com.example.screentimemanager.data.firebase.usage.UsageFirebaseDao
import com.google.firebase.auth.FirebaseAuth

class UsageRepository(
    private val usageDao: UsageFirebaseDao
) {
    /**
     * @param email
     * email of the user whose usage data is being retrieved
     * @return
     * return list of the user's usage data
     */
    suspend fun getUsageData(email: String): List<UsageFirebase> {
        return usageDao.getUsageData(email)
    }

    /**
     * @param appName
     * the app to add to the user's list of apps
     */
    suspend fun setUsageData(appName: String, date: String, usage: Long) {
        return usageDao.setUsageData(appName, date, usage)
    }
}