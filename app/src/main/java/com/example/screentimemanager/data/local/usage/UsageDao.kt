package com.example.screentimemanager.data.local.usage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UsageDao {
    /**
     * @return
     * return list of the user's usage data
     */
    @Query("SELECT * FROM Usage WHERE day = :day AND month = :month AND year = :year")
    fun getUsageData(day: Int, month: Int, year: Int): List<Usage>

    /**
     * sets the usage data for the given app on the given date
     * if the usage data already exists, it will be overwritten
     * @param appName
     * the app to add to the user's list of apps
     */
    @Query("INSERT OR REPLACE INTO Usage (appName, day, month, year, usage) " +
            "VALUES (:appName, :day, :month, :year, :usage)"
    )
    suspend fun setUsageData(appName: String, day: Int, month: Int, year: Int, usage: Long)
}