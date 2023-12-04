package com.example.screentimemanager.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.screentimemanager.R
import com.example.screentimemanager.data.firebase.friend.FriendFirebaseDao
import com.example.screentimemanager.data.firebase.usage.UsageFirebaseDao
import com.example.screentimemanager.data.local.usage.Usage
import com.example.screentimemanager.data.local.usage.UsageDao
import com.example.screentimemanager.data.local.usage.UsageDatabase
import com.example.screentimemanager.databinding.FragmentDashboardBinding
import com.example.screentimemanager.ui.profileSetting.ProfileSetting
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lecho.lib.hellocharts.model.Axis
import lecho.lib.hellocharts.model.AxisValue
import lecho.lib.hellocharts.model.Column
import lecho.lib.hellocharts.model.ColumnChartData
import lecho.lib.hellocharts.model.SubcolumnValue
import lecho.lib.hellocharts.model.Viewport
import lecho.lib.hellocharts.view.ColumnChartView
import java.util.Calendar


class DashboardFragment : Fragment(){

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var logInBtn: Button

    private lateinit var usageDatabase: UsageDatabase
    private lateinit var usageDao: UsageDao

    private lateinit var firebaseDatabaseRef: DatabaseReference
    private lateinit var friendDao: FriendFirebaseDao
    private lateinit var usageFirebaseDao: UsageFirebaseDao

    private lateinit var calendar: Calendar
    private var day = 1
    private var month = 1
    private var year = 2023

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }
    private fun getData() {
        var usages: List<Usage>? = null
        print("Hello patola")

        CoroutineScope(Dispatchers.IO).launch {
            // Fetch data and assign it to the 'usages' variable
            usages = usageDao.getUsageData(day, month, year)
            usages?.let { list ->
                var totalUsage: Long = 0
                for (usage in list) {
                    totalUsage += usage.usage
                }
                print("Here it is $usages")
            }
        }
        usages?.let{
            print("Usage is here: $usages")

        }


    }


    private fun generateColumnData(): ColumnChartData {
        val numColumns = 7
        val columns = mutableListOf<Column>()

        // Generate dummy data (replace with your actual data)
        for (i in 0 until numColumns) {
            val values = mutableListOf<SubcolumnValue>()
            values.add(SubcolumnValue(25f + i * 10, resources.getColor(R.color.purple_500)))
            val column = Column(values)
            columns.add(column.setHasLabels(true))
        }
        return ColumnChartData(columns)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

