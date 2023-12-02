package com.example.screentimemanager.ui.friend

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.screentimemanager.R
import com.example.screentimemanager.data.firebase.friend.FriendFirebaseDao
import com.example.screentimemanager.data.firebase.usage.UsageFirebaseDao
import com.example.screentimemanager.data.firebase.user.UserFirebaseDao
import com.example.screentimemanager.data.local.usage.UsageDatabase
import com.example.screentimemanager.data.repository.FriendRepository
import com.example.screentimemanager.data.repository.UsageRepository
import com.example.screentimemanager.data.repository.UserRepository
import com.example.screentimemanager.util.Util
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FriendInfoDialog : DialogFragment() {
    companion object {
        val FRIEND_EMAIL_KEY = "friend-email-key"
        val DAY_KEY = "day-key"
        val MONTH_KEY = "month-key"
        val YEAR_KEY = "year-key"
    }

    private lateinit var friendEmail: String
    private var day = -1
    private var month = -1
    private var year = -1
    private lateinit var usageRepository: UsageRepository
    private lateinit var friendRepository: FriendRepository
    private lateinit var userRepository: UserRepository
    private lateinit var chart: BarChart
    private lateinit var friendInfo: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.dialog_friend_info, container, false)
        friendEmail = arguments?.getString(FRIEND_EMAIL_KEY).toString()
        day = arguments?.getInt(DAY_KEY) ?: -1
        month = arguments?.getInt(MONTH_KEY) ?: -1
        year = arguments?.getInt(YEAR_KEY) ?: -1


        chart = root.findViewById(R.id.chart)

        setupRepo()

        CoroutineScope(IO).launch {
            val chartData = generateBarData()
            withContext(Dispatchers.Main) {
                chart.data = chartData
                chart.invalidate() // refresh the chart
            }
        }

        root.findViewById<Button>(R.id.btn_remove).setOnClickListener() {
            CoroutineScope(IO).launch {
                friendRepository.deleteFriend(friendEmail)
                dialog?.cancel()
            }
        }

        root.findViewById<Button>(R.id.btn_dismiss).setOnClickListener() {
            dialog?.cancel()
        }

        friendInfo = root.findViewById(R.id.friend_info)

        return root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    /**
     * sets up the UsageRepository, FriendRepository and UserRepository
     */
    private fun setupRepo() {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val usageDatabase = UsageDatabase.getInstance(requireActivity())
        val usageFirebaseDao = UsageFirebaseDao(firebaseDatabase.reference)
        val usageDao = usageDatabase.usageDao
        usageRepository = UsageRepository(usageFirebaseDao, usageDao)

        val friendFirebaseDao = FriendFirebaseDao(firebaseDatabase.reference)
        friendRepository = FriendRepository(friendFirebaseDao)

        val userFirebaseDao = UserFirebaseDao(firebaseDatabase.reference)
        userRepository = UserRepository(userFirebaseDao)
    }

    private suspend fun generateBarData(): BarData {
        return withContext(IO) {
            // get usage data from the repository
            val usages = usageRepository.getUsageData(friendEmail, day, month, year)
            val friend = userRepository.getUser(friendEmail)

            val entries = mutableListOf<BarEntry>()
            val labels = mutableListOf<String>()

            for ((index, usage) in usages.withIndex()) {
                // convert usage from millisecs to mins
                val mins = usage.usage.toFloat() / (1000 * 60)
                entries.add(BarEntry(index.toFloat(), mins, usage.appLabel))

                labels.add(usage.appLabel)
            }

            // styling and adding data to the graph
            val dataSet = BarDataSet(entries, "App Usage")
            dataSet.setColors(*ColorTemplate.MATERIAL_COLORS)

            val barData = BarData(dataSet)

            val xAxis = chart.xAxis
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            xAxis.labelRotationAngle = -90f
            xAxis.setDrawGridLines(false)
            xAxis.labelCount = labels.size

            val yAxisLeft = chart.axisLeft
            yAxisLeft.setDrawGridLines(false)

            val yAxisRight = chart.axisRight
            yAxisRight.setDrawGridLines(false)

            dataSet.valueTextSize = 15f
            dataSet.valueTextColor = Color.WHITE
            dataSet.setDrawValues(true)

            chart.legend.isEnabled = false
            chart.description.isEnabled = false

            // setup text view with date and total usage
            var totalUsage =  0L

            for (usage in usages){
                totalUsage += usage.usage
            }

            val (hours, mins) = Util.millisecToHoursAndMins(totalUsage)
            val formattedHours = String.format("%02d", hours)
            val formattedMinutes = String.format("%02d", mins)

            CoroutineScope(Main).launch {
                if (friend != null) {
                    friendInfo.text = "${friend.firstName} ${friend.lastName}'s total usage:\n" +
                            "$formattedHours:$formattedMinutes on $day/$month/$year"
                }
            }

            chart.invalidate()

            return@withContext barData
        }
    }
}
