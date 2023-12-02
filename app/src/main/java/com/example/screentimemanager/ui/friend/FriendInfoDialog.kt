package com.example.screentimemanager.ui.friend

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.screentimemanager.R
import com.example.screentimemanager.data.firebase.usage.UsageFirebaseDao
import com.example.screentimemanager.data.local.usage.UsageDatabase
import com.example.screentimemanager.data.repository.UsageRepository
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lecho.lib.hellocharts.model.Axis
import lecho.lib.hellocharts.model.AxisValue
import lecho.lib.hellocharts.model.Column
import lecho.lib.hellocharts.model.ColumnChartData
import lecho.lib.hellocharts.model.SubcolumnValue
import lecho.lib.hellocharts.view.ColumnChartView
import java.util.concurrent.TimeUnit

class FriendInfoDialog : DialogFragment() {
    companion object {
        val FRIEND_EMAIL_KEY = "friend-email-key"
    }

    private lateinit var friendEmail: String
    private lateinit var usageRepository: UsageRepository
    private lateinit var usageFirebaseDao: UsageFirebaseDao
    private lateinit var chart: BarChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.dialog_friend_info, container, false)
        friendEmail = arguments?.getString(FRIEND_EMAIL_KEY).toString()

        chart = root.findViewById(R.id.chart)

        setupRepo()

        CoroutineScope(IO).launch {
            val chartData = generateBarData()
            withContext(Dispatchers.Main) {
                chart.data = chartData
                chart.invalidate() // refresh the chart
            }
        }

        return root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun setupRepo() {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val usageDatabase = UsageDatabase.getInstance(requireActivity())
        usageFirebaseDao = UsageFirebaseDao(firebaseDatabase.reference)
        val usageDao = usageDatabase.usageDao
        usageRepository = UsageRepository(usageFirebaseDao, usageDao)
    }

    private suspend fun generateBarData(): BarData {
        return withContext(IO) {
            val (day, month, year) = Util.getCurrentDate()

            // get usage data from the repository
            val usages = usageFirebaseDao.getUsageData(friendEmail, day, month, year)

            val entries = mutableListOf<BarEntry>()
            val labels = mutableListOf<String>()

            for ((index, usage) in usages.withIndex()) {
                // convert usage from millisecs to mins
                val mins = usage.usage.toFloat() / (1000 * 60)
                entries.add(BarEntry(index.toFloat(), mins, usage.appLabel))

                labels.add(usage.appLabel)
            }

            val dataSet = BarDataSet(entries, "App Usage")
            dataSet.setColors(*ColorTemplate.MATERIAL_COLORS)

            val barData = BarData(dataSet)

            val xAxis = chart.xAxis
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            xAxis.labelRotationAngle = -90f
            xAxis.setDrawGridLines(false)

            val yAxisLeft = chart.axisLeft
            yAxisLeft.setDrawGridLines(false)

            val yAxisRight = chart.axisRight
            yAxisRight.setDrawGridLines(false)

            dataSet.valueTextSize = 15f
            dataSet.valueTextColor = Color.WHITE
            dataSet.setDrawValues(true)

            chart.legend.isEnabled = false
            chart.description.isEnabled = false

            return@withContext barData
        }
    }




}
