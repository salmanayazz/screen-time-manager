package com.example.screentimemanager.data.firebase.usage

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class UsageFirebaseDao(
    private val database: DatabaseReference
) {
    // use database.child.("usages").child(userEmail).child(appName).child("$day/$month/$year") to query firebase

    /**
     * @param email
     * email of the user whose usage data is being retrieved
     * @return  
     * return list of the user's usage data
     */
    suspend fun getUsageData(email: String): List<UsageFirebase> {
        return listOf()
    }

    /**
     * sets the usage data for the given app on the given date
     * if the usage data already exists, it will be overwritten
     * @param appName
     * the app to add to the user's list of apps
     */
    suspend fun setUsageData(appName: String, date: String, usage: Long) {
        val email = FirebaseAuth.getInstance().currentUser?.email
    }
}