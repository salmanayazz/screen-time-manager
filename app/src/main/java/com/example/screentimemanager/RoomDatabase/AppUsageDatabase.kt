package com.example.screentimemanager.RoomDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//Implemented the ExerciseDatabase by referring to the Professor's code
@Database(entities = [AppUsageSchema::class], version = 1)
abstract class AppUsageDatabase : RoomDatabase() {
    abstract val appUsageDatabaseDao: AppUsageDatabaseDao

    companion object{
        @Volatile
        private var INSTANCE: AppUsageDatabase? = null

        fun getInstance(context: Context) : AppUsageDatabase{
            synchronized(this){
                var instance = INSTANCE
                if(instance == null){
                    instance = Room.databaseBuilder(context.applicationContext,
                        AppUsageDatabase::class.java, "appUsage").build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}