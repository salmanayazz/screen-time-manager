package com.example.screentimemanager.data.firebase.usage

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UsageFirebaseDao(private val database: DatabaseReference) {

    private val usageRef = database.child("usages")

    /**
     * Get the usage data for the given user on the specified date.
     * @param email Email of the user
     * @param day The day of the month
     * @param month The month of the year
     * @param year The year
     * @return List of UsageFirebase objects
     */
    suspend fun getUsageData(email: String, day: Int, month: Int, year: Int): List<UsageFirebase> {
        return withContext(Dispatchers.IO) {
            val sanitizedEmail = email.replace("@", "(").replace(".", ")")
            val userUsageRef = usageRef.child(sanitizedEmail)
                .child(year.toString()).child(month.toString()).child(day.toString())
            val userSnapshot = userUsageRef.get().await()

            val usageList = mutableListOf<UsageFirebase>()

            if (userSnapshot.exists()) {
                for (appSnapshot in userSnapshot.children) {
                    val appName = appSnapshot.key
                    val usage = appSnapshot.child("usage").getValue(Long::class.java)
                    if (appName != null && usage != null) {
                        val usageData = UsageFirebase(email, appName, day, month, year, usage)
                        usageList.add(usageData)
                    }
                }
            }

            usageList
        }
    }

    /**
     * Set the usage data for the given user on the specified date.
     * @param appName The app to add to the user's list of apps
     * @param day The day of the month
     * @param month The month of the year
     * @param year The year
     * @param usage The usage time in milliseconds
     */
    suspend fun setUsageData(appName: String, day: Int, month: Int, year: Int, usage: Long) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val email = currentUser?.email ?: DEFAULT_EMAIL
        val sanitizedEmail = email.replace("@", "(").replace(".", ")")
        val userUsageRef = usageRef.child(sanitizedEmail)
            .child("$year/$month/$day")
            .child(appName)

        try {
            userUsageRef.setValue(UsageFirebase(sanitizedEmail, appName, day, month, year, usage))
                .await()
            Log.d(TAG, "Setting usage data for $sanitizedEmail on $day/$month/$year")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting usage data: ${e.message}")
        }
    }

    companion object {
        private const val TAG = "UsageFirebaseDao"
        private const val DEFAULT_EMAIL = "test2@gmail.com"
    }
}
