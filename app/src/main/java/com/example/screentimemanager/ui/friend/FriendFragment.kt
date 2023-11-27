package com.example.screentimemanager.ui.friend

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.screentimemanager.R
import com.example.screentimemanager.data.firebase.friend.FriendFirebaseDao
import com.example.screentimemanager.data.firebase.usage.UsageFirebaseDao
import com.example.screentimemanager.data.local.usage.UsageDao
import com.example.screentimemanager.data.local.usage.UsageDatabase
import com.example.screentimemanager.data.repository.FriendRepository
import com.example.screentimemanager.data.repository.UsageRepository
import com.example.screentimemanager.ui.authentication.Login
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import lecho.lib.hellocharts.model.Axis
import lecho.lib.hellocharts.model.Column
import lecho.lib.hellocharts.model.ColumnChartData
import lecho.lib.hellocharts.model.SubcolumnValue
import lecho.lib.hellocharts.view.ColumnChartView
import java.util.Calendar

class FriendFragment : Fragment() {
    private lateinit var btnAddFriend: FloatingActionButton
    private lateinit var friendList: ListView
    private lateinit var friendViewModel: FriendViewModel
    private lateinit var friends: ArrayList<Friend>
    private lateinit var leftArrow: TextView
    private lateinit var rightArrow: TextView
    private lateinit var displayDate: TextView

    private lateinit var chart: ColumnChartView

    private lateinit var usageDatabase: UsageDatabase
    private lateinit var usageDao: UsageDao

    private lateinit var firebaseDatabaseRef: DatabaseReference
    private lateinit var friendDao: FriendFirebaseDao
    private lateinit var usageFirebaseDao: UsageFirebaseDao
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var friendRepo: FriendRepository
    private lateinit var usageRepo: UsageRepository
    private lateinit var friendDbList: List<String>

    private lateinit var calendar: Calendar
    private var day = 1
    private var month = 1
    private var year = 2023
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val ret = inflater.inflate(R.layout.fragment_friend, container, false)
        // checking if the user is logged in or no
        // if they are not logged in , we will direct them
        // to the login activity
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        /*if (currentUser == null) {
            // if there's no user signed in
            startActivity(Intent(requireContext(), Login::class.java))
            //val transaction = requireFragmentManager().beginTransaction()
            //transaction.replace(R.id.container,LogFragment())
            //transaction.addToBackStack(null)
            //transaction.commit()
        }*/
        friends = ArrayList()
        val adapter = FriendListAdapter(requireActivity(), R.layout.layout_friend_list, friends)
        friendViewModel = ViewModelProvider(requireActivity()).get(FriendViewModel::class.java)
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

        friendDbList = listOf()
        displayDate.text = "$day-$month-$year"

        usageDatabase = UsageDatabase.getInstance(requireActivity())
        usageDao = usageDatabase.usageDao
        firebaseDatabaseRef = FirebaseDatabase.getInstance().reference
        usageFirebaseDao = UsageFirebaseDao(firebaseDatabaseRef)
        friendDao = FriendFirebaseDao(firebaseDatabaseRef)
        usageRepo = UsageRepository(usageFirebaseDao, usageDao)
        friendRepo = FriendRepository(friendDao)

        CoroutineScope(IO).launch {
            friendDbList = friendRepo.getFriendList()
        }

        chart.columnChartData = generateColumnData()

        //friendList is to show the list view to list out friends
        friendList.adapter = adapter

        friendViewModel.friends.observe(requireActivity()) {
            adapter.clear()
            adapter.addAll(it)
            friendList.adapter = adapter
        }

        leftArrow.setOnClickListener {
            calendar.add(Calendar.DATE, -1)
            day = calendar.get(Calendar.DAY_OF_MONTH)
            month = calendar.get(Calendar.MONTH)
            year = calendar.get(Calendar.YEAR)
            displayDate.text = "$day-$month-$year"
            chart.columnChartData = generateColumnData()
        }

        rightArrow.setOnClickListener {
            calendar.add(Calendar.DATE, 1)
            day = calendar.get(Calendar.DAY_OF_MONTH)
            month = calendar.get(Calendar.MONTH)
            year = calendar.get(Calendar.YEAR)
            displayDate.text = "$day-$month-$year"
            chart.columnChartData = generateColumnData()
        }

        //When clicked add friend button, if the user has signed in, it will show the AddFriendFragmentDialog
        //Otherwise, it will show a dialog to tell the user to sign in.
        //There will be a sign in button link to the sign in page.
        //If the user click cancel, the dialog will be dismissed.
        btnAddFriend.setOnClickListener {
            if (currentUser == null) {
                // if the user has not logged in
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
                val dialog = AddFriendFragmentDialog()
                dialog.show(requireActivity().supportFragmentManager, ADD_FRIEND_DIALOG_TAG)
            }
        }
        return ret
    }

    private fun generateColumnData(): ColumnChartData{
        val numColumns = friendDbList.size
        val columns = mutableListOf<Column>()
        val values = mutableListOf<SubcolumnValue>()

        for(i in 0 until numColumns){
            values.clear()
            val usages = usageFirebaseDao.getUsageData(friendDbList[i], day, month, year)
            var totalUsage: Long = 0
            for (usage in usages){
                totalUsage += usage.usage
            }
            values.add(SubcolumnValue(totalUsage.toFloat()))
            columns.add(Column(values).setHasLabels(true).setHasLabelsOnlyForSelected(true))
        }

        val columnChartData = ColumnChartData(columns)
        val axisX = Axis().setAutoGenerated(true)
        val axisY = Axis().setHasLines(true)
        columnChartData.axisXBottom = axisX
        columnChartData.axisYLeft = axisY

        return columnChartData
    }
}
