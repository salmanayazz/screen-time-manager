package com.example.screentimemanager.usageComparisonService

import android.util.Log
import com.example.screentimemanager.data.firebase.usage.UsageFirebase
import com.example.screentimemanager.data.firebase.usage.UsageFirebaseDao
import com.example.screentimemanager.data.firebase.user.UserFirebaseDao

class UsageComparisonManager(private val usageFirebaseDao: UsageFirebaseDao,
                             private val userFirebaseDao: UserFirebaseDao,
) {

    suspend fun fetchUserUsageData(userEmail: String, day: Int, month: Int, year: Int): List<UsageFirebase>
    {
        return usageFirebaseDao.getUsageData(userEmail, day, month, year)
    }

    suspend fun fetchFriendsUsageData(userEmail: String, day: Int, month: Int, year: Int): Map<String, List<UsageFirebase>> {
        val friendsUsageData = mutableMapOf<String, List<UsageFirebase>>()

        // Fetch friends' emails
        val friendsEmails = usageFirebaseDao.getFriendsEmails(userEmail)

        // Fetch usage data for each friend
        for (friendEmail in friendsEmails) {
            val friendUsage = usageFirebaseDao.getUserUsageDataOnDate(friendEmail, day, month, year)
            friendsUsageData[friendEmail] = friendUsage
        }

        return friendsUsageData
    }


    fun aggregateUsageData(usageData: Map<String, List<UsageFirebase>>): Map<String, Long> {
        val aggregatedData = mutableMapOf<String, Long>()

        usageData.forEach { (email, usageList) ->
            val totalUsage = usageList.sumOf { it.usage }
            aggregatedData[email] = totalUsage
        }

        return aggregatedData
    }


    suspend fun compileAndAggregateUsageData(userEmail: String, day: Int, month: Int, year: Int): Map<String, Long> {
        val usageData = mutableMapOf<String, List<UsageFirebase>>()

        // Fetch user's usage data
        val userUsageData = fetchUserUsageData(userEmail, day, month, year)
        usageData[userEmail] = userUsageData

        // Fetch and add friends' usage data
        val friendsUsageData = fetchFriendsUsageData(userEmail, day, month, year)
        usageData.putAll(friendsUsageData)

        // Aggregate the data
        val aggregatedData = aggregateUsageData(usageData)

        // Print the aggregated data for debugging
        println("Aggregated Usage Data for $userEmail and friends on $day/$month/$year: $aggregatedData")

        return aggregatedData
    }



    suspend fun findLowestUsageUserAndFormatMessage(userEmail: String, day: Int, month: Int, year: Int): String {
        // Fetch and aggregate usage data
        val aggregatedData = compileAndAggregateUsageData(userEmail, day, month, year)

        // Log all the inputs
        Log.d("UsageComparisonManager", "Notification Inputs for findLowestUsageUserAndFormat $userEmail, $day, $month, $year");
        if (aggregatedData.isEmpty()) {
            return "No usage data available for today."
        }
        val (lowestUsageUserEmail, usageTime) = aggregatedData.minByOrNull { it.value } ?: return "No usage data available."

        Log.d("UsageComparisonManager", "Notification lowestUsageUserEmail: $lowestUsageUserEmail and usageTime: $usageTime");
        // Fetch the first name of the user with the lowest usage
        val user = userFirebaseDao.getUser(lowestUsageUserEmail)
        val firstName = user?.firstName ?: "User"
        
        // Convert usage time to hours and minutes
        val hours = usageTime / (60 * 60 * 1000)
        val minutes = (usageTime % (60 * 60 * 1000)) / (60 * 1000)
        val finalMessage = if (hours > 0) {
            "$firstName got the lowest usage time today by only using $hours hours and $minutes minutes."
        } else {
            "$firstName got the lowest usage time today by only using $minutes minutes."
        }
        Log.d("UsageComparisonManager", "Notification finalMessage: $finalMessage");
        // Format the message based on the usage time
        return finalMessage
    }


}