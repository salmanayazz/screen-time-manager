package com.example.screentimemanager.data.firebase.app

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class AppFirebaseDao(
    private val database: DatabaseReference
) {
    /**
     * @param appName
     * the app to add to the user's list of apps
     */
    suspend fun addApp(appName: String) {
        val email = FirebaseAuth.getInstance().currentUser?.email
    }

    /**
     * gets the time limit for the app
     * @param appName
     * the app to get the limit for
     * @return
     * the time limit in milliseconds
     */
    suspend fun getAppLimit(appName: String): Long {
        return 0
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

    }
}