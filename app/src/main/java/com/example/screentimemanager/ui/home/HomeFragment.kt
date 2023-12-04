package com.example.screentimemanager.ui.home

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.screentimemanager.R
import com.example.screentimemanager.data.local.usage.UsageDao
import com.example.screentimemanager.data.local.usage.UsageDatabase
import com.example.screentimemanager.databinding.FragmentHomeBinding
import com.example.screentimemanager.ui.appsettings.AppSettingsActivity
import com.example.screentimemanager.util.UserApplicationsAdapter
import com.example.screentimemanager.util.Util
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar


class HomeFragment : Fragment() {

    var flags = PackageManager.GET_META_DATA or
            PackageManager.GET_SHARED_LIBRARY_FILES

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var applicationsAdapter: UserApplicationsAdapter
    private lateinit var root: View
    private lateinit var chart: BarChart
    private val calendar: Calendar = Calendar.getInstance()
    private var day = calendar.get(Calendar.DAY_OF_MONTH)
    private var month = calendar.get(Calendar.MONTH)
    private var year = calendar.get(Calendar.YEAR)
    private lateinit var usageDao: UsageDao


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        root = binding.root

        /* getting all users applications installed on their phone
        and show all the users apps using listview and custom adapter
         */
        val apps = Util.getApplicationsList(requireContext())
        var appListView = root.findViewById<ListView>(R.id.app_lists)
        applicationsAdapter = UserApplicationsAdapter(requireContext(),apps)
        appListView.adapter = applicationsAdapter

        /*
        setting the clicker for each item in application listview, which
        opens the appSetting activity for the clicked application.
         */
        appListView.setOnItemClickListener(){_, _, position, _ ->

            val selectedApplication: ApplicationInfo = applicationsAdapter.getItem(position)
            val intent: Intent = Intent(activity, AppSettingsActivity::class.java)
                .putExtra(AppSettingsActivity.APPLICATION_INFO, selectedApplication)
            startActivity(intent)
        }

        /*
        setting the functionality of the search widget to filter applications
         */
        var searchView: SearchView = root.findViewById(R.id.search_text)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                // Handle text changes here if needed
                applicationsAdapter.filter.filter(newText)
                return false
            }
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView.clearFocus()
                return false
            }
        })

        setupRepo()
        setupChartListeners()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupChartListeners() {
        chart = root.findViewById(R.id.lcv_histogram)
        val displayDate = root.findViewById<TextView>(R.id.tv_date)
        displayDate.text = "$day-${month+1}-$year"

        //When clicking the left arrow, the date will change to the day before
        root.findViewById<TextView>(R.id.tv_leftArrow)
        .setOnClickListener{
            calendar.add(Calendar.DATE, -1)
            day = calendar.get(Calendar.DAY_OF_MONTH)
            month = calendar.get(Calendar.MONTH)
            year = calendar.get(Calendar.YEAR)
            displayDate.text = "$day-${month+1}-$year"
            loadChart()
        }

        //When clicking the right arrow, the date will change to the day after
        root.findViewById<TextView>(R.id.tv_rightArrow).setOnClickListener{
            calendar.add(Calendar.DATE, 1)
            day = calendar.get(Calendar.DAY_OF_MONTH)
            month = calendar.get(Calendar.MONTH)
            year = calendar.get(Calendar.YEAR)
            displayDate.text = "$day-${month+1}-$year"
            loadChart()
        }

        loadChart()
    }

    private fun setupRepo() {
        val usageDatabase = UsageDatabase.getInstance(requireActivity())
        usageDao = usageDatabase.usageDao
    }

    private fun loadChart() {
        CoroutineScope(Dispatchers.IO).launch {
            val chartData = generateBarData()
            withContext(Dispatchers.Main) {
                chart.data = chartData
                chart.invalidate() // refresh the chart
            }
        }
    }

    private suspend fun generateBarData(): BarData {
        var chart = root.findViewById<BarChart>(R.id.lcv_histogram)
        var usageText = root.findViewById<TextView>(R.id.tv_textUsage)
        return withContext(Dispatchers.IO) {
            // get usage data from the repository
            val usages = usageDao.getUsageData(day, month+1, year)

            val entries = mutableListOf<BarEntry>()
            val labels = mutableListOf<String>()

            for ((index, usage) in usages.withIndex()) {
                // convert usage from millisecs to mins
                val mins = usage.usage.toFloat() / (1000 * 60)
                val appName = getAppNameFromPackage(requireActivity(), usage.appName)

                if (appName != null) {
                    entries.add(BarEntry(index.toFloat(), mins, appName))
                    labels.add(appName)
                }
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

            CoroutineScope(Dispatchers.Main).launch {
                var text = " Total usage:\n"

                text += "$hours hour"
                if (hours != 1) { text += "s"}
                text += ", $mins minute"
                if (mins != 1) { text += "s"}
                usageText.text = text
            }


            chart.invalidate()

            return@withContext barData
        }
    }

    fun getAppNameFromPackage(context: Context, packageName: String?): String? {
        val packageManager: PackageManager = context.packageManager
        var appName: String? = null
        try {
            val applicationInfo = packageManager.getApplicationInfo(
                packageName!!, 0
            )
            appName = packageManager.getApplicationLabel(applicationInfo) as String
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return appName
    }

    override fun onResume() {
        super.onResume()
        loadChart()
    }
}