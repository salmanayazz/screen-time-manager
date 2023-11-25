package com.example.screentimemanager.data.local.usage

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

abstract class UsageDatabase: RoomDatabase() {
    abstract val usageDao: UsageDao

    companion object {
        @Volatile
        private var DATABASE_INSTANCE: UsageDatabase? = null

        /**
         * returns the singleton instance of the database
         * @param context
         * the context of the application
         * @return UsageDatabase
         * the singleton instance of the database
         */
        fun getInstance(context: Context) : UsageDatabase {
            synchronized(this){
                var databaseInstance = DATABASE_INSTANCE
                if (databaseInstance == null) {
                    databaseInstance = Room.databaseBuilder(
                        context.applicationContext,
                        UsageDatabase::class.java,
                        "Usage"
                    ).build()
                    DATABASE_INSTANCE = databaseInstance
                }
                return databaseInstance
            }
        }
    }
}