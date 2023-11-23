package com.example.screentimemanager.data.repository

import com.example.screentimemanager.data.firebase.app.AppFirebaseDao

class AppRepository (
    private val appDao: AppFirebaseDao
) {
    /**
     * @param appName
     * the app to add to the user's list of apps
     */
    suspend fun addApp(appName: String) {
        appDao.addApp(appName)
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
    suspend fun setAppLimit(app: String, hasLimit: Boolean, timeLimit: Long) {
        appDao.setAppLimit(app, hasLimit, timeLimit)
    }
}