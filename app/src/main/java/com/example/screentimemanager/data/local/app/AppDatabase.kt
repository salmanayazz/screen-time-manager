package com.example.screentimemanager.data.local.app

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

abstract class AppDatabase: RoomDatabase() {
    abstract val appDao: AppDao

    companion object {
        @Volatile
        private var DATABASE_INSTANCE: AppDatabase? = null

        /**
         * returns the singleton instance of the database
         * @param context
         * the context of the application
         * @return AppDatabase
         * the singleton instance of the database
         */
        fun getInstance(context: Context) : AppDatabase {
            synchronized(this){
                var databaseInstance = DATABASE_INSTANCE
                if (databaseInstance == null) {
                    databaseInstance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "App"
                    ).build()
                    DATABASE_INSTANCE = databaseInstance
                }
                return databaseInstance
            }
        }
    }
}