package com.example.screentimemanager.ui.friend

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.screentimemanager.R
import com.example.screentimemanager.data.firebase.friend.FriendFirebaseDao
import com.example.screentimemanager.data.firebase.usage.UsageFirebaseDao
import com.example.screentimemanager.data.firebase.user.UserFirebaseDao
import com.example.screentimemanager.data.local.usage.UsageDao
import com.example.screentimemanager.data.local.usage.UsageDatabase
import com.example.screentimemanager.data.repository.FriendRepository
import com.example.screentimemanager.data.repository.UsageRepository
import com.example.screentimemanager.data.repository.UserRepository
import com.example.screentimemanager.ui.authentication.Login
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class FriendFragment : Fragment() {
    private lateinit var btnAddFriend: FloatingActionButton
    private lateinit var friendList: ListView
    private lateinit var friends: ArrayList<String>
    private lateinit var leftArrow: TextView
    private lateinit var rightArrow: TextView
    private lateinit var displayDate: TextView

    private lateinit var chart: BarChart

    private lateinit var usageDatabase: UsageDatabase
    private lateinit var usageDao: UsageDao

    private lateinit var firebaseDatabaseRef: DatabaseReference
    private lateinit var friendDao: FriendFirebaseDao
    private lateinit var usageFirebaseDao: UsageFirebaseDao
    private lateinit var userRepository: UserRepository

    private lateinit var friendRepo: FriendRepository
    private lateinit var usageRepo: UsageRepository

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var calendar: Calendar
    private var day = 1
    private var month = 1
    private var year = 2023
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        friends = ArrayList()
        val adapter = FriendListAdapter(requireActivity(), R.layout.layout_friend_list, friends)
        adapter.setOnItemClickListener { friend ->
            val bundle = Bundle()
            bundle.putString(FriendInfoDialog.FRIEND_EMAIL_KEY, friend.email)
            bundle.putInt(FriendInfoDialog.DAY_KEY, day)
            bundle.putInt(FriendInfoDialog.MONTH_KEY, month+1)
            bundle.putInt(FriendInfoDialog.YEAR_KEY, year)

            // create and show the DialogFragment
            val dialogFragment = FriendInfoDialog()
            dialogFragment.arguments = bundle
            dialogFragment.show(childFragmentManager, "FriendInfoDialog")
        }

        val ret = inflater.inflate(R.layout.fragment_friend, container, false)
        btnAddFriend = ret.findViewById(R.id.fab_addFriend)
        friendList = ret.findViewById(R.id.lv_friendList)
        leftArrow = ret.findViewById(R.id.tv_leftArrow)
        rightArrow = ret.findViewById(R.id.tv_rightArrow)
        displayDate = ret.findViewById(R.id.tv_date)
        chart = ret.findViewById(R.id.lcv_histogram)
        calendar = Calendar.getInstance()
        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)
        year = calendar.get(Calendar.YEAR)

        displayDate.text = "$day-${month+1}-$year"

        usageDatabase = UsageDatabase.getInstance(requireActivity())
        usageDao = usageDatabase.usageDao
        firebaseDatabaseRef = FirebaseDatabase.getInstance().reference
        usageFirebaseDao = UsageFirebaseDao(firebaseDatabaseRef)
        friendDao = FriendFirebaseDao(firebaseDatabaseRef)
        usageRepo = UsageRepository(usageFirebaseDao, usageDao)
        friendRepo = FriendRepository(friendDao)

        val firebaseDatabase = FirebaseDatabase.getInstance()
        val userFirebaseDao = UserFirebaseDao(firebaseDatabase.reference)
        userRepository = UserRepository(userFirebaseDao)

        friendRepo.getFriendList()

        loadChart()

        //friendList is to show the list view to list out friends
        friendList.adapter = adapter

        friendRepo.friendList.observe(requireActivity()){
            friends = it as ArrayList<String>
            adapter.clear()
            adapter.addAll(friends)
            adapter.notifyDataSetChanged()
            loadChart()
        }

        //When clicking the left arrow, the date will change to the day before
        leftArrow.setOnClickListener{
            calendar.add(Calendar.DATE, -1)
            day = calendar.get(Calendar.DAY_OF_MONTH)
            month = calendar.get(Calendar.MONTH)
            year = calendar.get(Calendar.YEAR)
            displayDate.text = "$day-${month+1}-$year"
            loadChart()
        }

        //When clicking the right arrow, the date will change to the day after
        rightArrow.setOnClickListener{
            calendar.add(Calendar.DATE, 1)
            day = calendar.get(Calendar.DAY_OF_MONTH)
            month = calendar.get(Calendar.MONTH)
            year = calendar.get(Calendar.YEAR)
            displayDate.text = "$day-${month+1}-$year"
            loadChart()
        }

        //When clicked add friend button, the user will be brought to the AddFriendsActivity
        btnAddFriend.setOnClickListener{
            if (currentUser == null) {
                // if the user has not logged in
                // then show the dialog to sign in
                val dialog = AlertDialog.Builder(requireActivity())
                dialog.setTitle(getString(R.string.sign_in_required))
                dialog.setMessage(getString(R.string.sign_in_required_message))
                dialog.setPositiveButton(getString(R.string.sign_in)) { _, _ ->
                    startActivity(Intent(requireContext(), Login::class.java))
                }
                dialog.setNegativeButton(getString(R.string.cancel)) { _, _ ->
                }
                dialog.setCancelable(false)
                dialog.show()
            } else {
                // if they were logged already, then show the AddFriendsActivity
                val intent = Intent(requireActivity(), AddFriendsActivity::class.java)
                startActivity(intent)
            }
        }
        return ret
    }

    private fun loadChart() {
        CoroutineScope(IO).launch {
            val chartData = generateBarData()
            withContext(Dispatchers.Main) {
                chart.data = chartData
                chart.invalidate() // refresh the chart
            }
        }
    }

    //Generate histogram data for the graph
    private suspend fun generateBarData(): BarData {
        return withContext(IO) {
            val numColumns = friends.size
            val entries = mutableListOf<BarEntry>()
            val labels = mutableListOf<String>()

            for(i in 0 until numColumns){
                val usages = usageFirebaseDao.getUsageData(friends[i], day, month + 1, year)
                val user = userRepository.getUser(friends[i])

                var totalUsage: Long = 0
                for (usage in usages){
                    totalUsage += usage.usage
                }
                // convert usage from millisecs to mins
                val mins = totalUsage.toFloat() / (1000 * 60)
                entries.add(BarEntry(i.toFloat(), mins, friends[i]))
                labels.add("${user?.firstName} ${user?.lastName}")
            }

            val dataSet = BarDataSet(entries, "User Usage")
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