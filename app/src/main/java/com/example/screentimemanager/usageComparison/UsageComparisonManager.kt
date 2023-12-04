package com.example.screentimemanager.usageComparison

import com.example.screentimemanager.data.firebase.usage.UsageFirebase
import com.example.screentimemanager.data.firebase.usage.UsageFirebaseDao

class UsageComparisonManager(private val usageFirebaseDao: UsageFirebaseDao) {

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



}