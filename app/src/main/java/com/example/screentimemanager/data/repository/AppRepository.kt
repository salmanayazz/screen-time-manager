package com.example.screentimemanager.data.repository

import com.example.screentimemanager.data.firebase.app.AppFirebaseDao
import com.example.screentimemanager.data.local.app.AppDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppRepository (
    private val appFirebaseDao: AppFirebaseDao,
    private val appDao: AppDao
) {
    /**
     * @param appName
     * the app to add to the user's list of apps
     */
    suspend fun addApp(appName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            appDao.addApp(appName)
            appFirebaseDao.addApp(appName)
        }
    }

    /**
     * gets the time limit for the app
     * @param appName
     * the app to get the limit for
     * @return
     * the time limit in milliseconds
     */
    suspend fun getAppLimit(appName: String): Long {
        return appDao.getAppLimit(appName)
    }

    /**
     * enables/disables time limiting and sets the amount
     * @param app
     * the app to change the limit for
     * @param hasLimit
     * indicates whether the user has time limiting enabled
     * @param timeLimit
     * app time limit per day in milliseconds
     */
    suspend fun setAppLimit(appName: String, hasLimit: Boolean, timeLimit: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            appDao.setAppLimit(appName, hasLimit, timeLimit)
            appFirebaseDao.setAppLimit(appName, hasLimit, timeLimit)
        }
    }
}