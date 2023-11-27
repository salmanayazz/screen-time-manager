package com.example.screentimemanager.data.firebase.usage

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.tasks.await
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class UsageFirebaseDao(
    private val database: DatabaseReference
) {
    val usageRef = database.child("usages")

    private val _usageData = MutableLiveData<List<UsageFirebase>>()
    val usageData: LiveData<List<UsageFirebase>> = _usageData

    /**
     * Fetches usage data for the given user on the given date and updates LiveData.
     * @param email Email of the user whose usage data is being retrieved
     * @param day The day of the month
     * @param month The month of the year
     * @param year The year
     */
    fun getUsageData(email: String, day: Int, month: Int, year: Int) {
        val userUsageRef = usageRef.child(email).child("$day/$month/$year")

        userUsageRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usageList = mutableListOf<UsageFirebase>()
                for (dataSnapshot in snapshot.children) {
                    dataSnapshot.getValue(UsageFirebase::class.java)?.let {
                        usageList.add(it)
                    }
                }
                _usageData.postValue(usageList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error, maybe updating the LiveData with an error state or logging
            }
        })
    }

    /**
     * Sets the usage data for the given user on the given date.
     * @param appName The app to add to the user's list of apps
     * @param day The day of the month
     * @param month The month of the year
     * @param year The year
     * @param usage The usage time in milliseconds
     */
    fun setUsageData(appName: String, day: Int, month: Int, year: Int, usage: Long) {
        val email = FirebaseAuth.getInstance().currentUser?.email
        email?.let {
            val userUsageRef = usageRef.child(it).child("$day/$month/$year")

            val usageData = UsageFirebase(it, appName, day, month, year, usage)

            userUsageRef.push().setValue(usageData)
            // Note: No direct response handling here, consider adding another LiveData for status or using a different approach
        }
    }
}
