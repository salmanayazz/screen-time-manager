package com.example.screentimemanager.data.local.app

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AppDao {
    /**
     * @param appName
     * the app to add to the user's list of apps
     */
    @Query("INSERT INTO App (appName) VALUES (:appName)")
    suspend fun addApp(appName: String)

    /**
     * gets the time limit for the app
     * @param appName
     * the app to get the limit for
     * @return
     * the time limit in milliseconds
     */
    @Query("SELECT timeLimit FROM App WHERE appName = :appName")
    fun getAppLimit(appName: String): Long?

    /**
     * enables/disables time limiting and sets the amount.
     * if app entry does not exist, it will be created
     * @param app
     * the app to change the limit for
     * @param hasLimit
     * indicates whether the user has time limiting enabled
     * @param timeLimit
     * app time limit per day in milliseconds
     */
    @Query("INSERT OR REPLACE INTO App (appName, hasLimit, timeLimit) VALUES (:appName, :hasLimit, :timeLimit)")
    suspend fun setAppLimit(appName: String, hasLimit: Boolean, timeLimit: Long)
}