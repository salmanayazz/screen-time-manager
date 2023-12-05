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

        // Check if the user has friends
        if (aggregatedData.size <= 1) {
            return "No friends to compare."
        }

        val (lowestUsageUserEmail, usageTime) = aggregatedData.minByOrNull { it.value } ?: return "No usage data available."

        // Fetch the user details
        val user = userFirebaseDao.getUser(lowestUsageUserEmail)
        val fullName = "${user?.firstName ?: ""} ${user?.lastName ?: ""}".trim()

        // Check if the current user has the lowest usage time
        if (lowestUsageUserEmail == userEmail) {
            return "Congrats! You had the lowest screen time among your friends on ${formatDate(day, month, year)}."
        }

        // Format the date
        val formattedDate = formatDate(day, month, year)

        // Convert usage time to hours and minutes
        val hours = usageTime / (60 * 60 * 1000)
        val minutes = (usageTime % (60 * 60 * 1000)) / (60 * 1000)

        return if (hours > 0) {
            "Your friend $fullName had the lowest usage time among your friends on $formattedDate by only using $hours hours and $minutes minutes."
        } else {
            "Your friend $fullName had the lowest usage time among your friends on $formattedDate by only using $minutes minutes."
        }
    }

    private fun formatDate(day: Int, month: Int, year: Int): String {
        // Format the date as needed, for example "April 5, 2023"
        // Note: Month is 0-indexed, so you may need to add 1.
        val monthName = getMonthName(month)
        Log.d("UsageComparisonManager", "Month name: $monthName")
        return "$monthName $day, $year"
    }

    private fun getMonthName(monthIndex: Int): String {
        // Convert month index to month name
        // This is a simplistic approach; consider using DateFormat for localization
        val months = arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
        return months.getOrElse(monthIndex-1) { "Unknown" }
    }



}