package com.example.screentimemanager.data.firebase.usage

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.tasks.await

class UsageFirebaseDao(
    private val database: DatabaseReference
) {
    // Define the Firebase reference path for usage data
    private val usageRef = database.child("usages")

    /**
     * Returns usage data for the given user on the given date.
     * @param email Email of the user whose usage data is being retrieved
     * @param day The day of the month
     * @param month The month of the year
     * @param year The year
     * @return Returns a list of the user's usage data for the specified date.
     */
    suspend fun getUsageData(email: String, day: Int, month: Int, year: Int): List<UsageFirebase> {
        val userUsageRef = usageRef.child(email).child("$day/$month/$year")

        // Retrieve the data from Firebase and await its completion
        val snapshot = userUsageRef.get().await()
        val usageList = mutableListOf<UsageFirebase>()

        // Iterate through the data snapshot and convert it to a list of UsageFirebase objects
        for (dataSnapshot in snapshot.children) {
            val usage = dataSnapshot.getValue(UsageFirebase::class.java)
            usage?.let { usageList.add(it) }
        }

        return usageList
    }
    /**
     * Sets the usage data for the given user on the given date.
     * If usage data already exists, it will be replaced.
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
        email?.let {
            // Construct the reference path for the user's usage data
            val userUsageRef = usageRef.child(it).child("$day/$month/$year")

            // Create a new UsageFirebase object with schema properties
            val usageData = UsageFirebase(it, appName, day, month, year, usage)

            // Set the data in Firebase
            userUsageRef.push().setValue(usageData)
        }
    }
}
