package com.example.screentimemanager.data.firebase.usage

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class UsageFirebaseDao(
    private val database: DatabaseReference
) {
    // use database.child.("usages").child(userEmail).child(appName).child("$day/$month/$year") to query firebase

    /**
     * returns for usage data for the given user on the given date
     * @param email
     * email of the user whose usage data is being retrieved
     * @param day
     * the day of the month
     * @param month
     * the month of the year
     * @param year
     * the year
     * @return  
     * return list of the user's usage data
     */
     fun getUsageData(email: String, day: Int, month: Int, year: Int): List<UsageFirebase> {
        return listOf()
    }

    /**
     * sets the usage data for the given user on the given date
     * if usage data already exists, it will be replaced
     * @param appName
     * the app to add to the user's list of apps
     * @param day
     * the day of the month
     * @param month
     * the month of the year
     * @param year
     * the year
     * @param usage
     * the usage time in milliseconds
     */
    suspend fun setUsageData(appName: String, day: Int, month: Int, year: Int, usage: Long) {
        val email = FirebaseAuth.getInstance().currentUser?.email
    }
}