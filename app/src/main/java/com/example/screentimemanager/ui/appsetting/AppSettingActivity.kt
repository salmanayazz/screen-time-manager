package com.example.screentimemanager.ui.appsetting

import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.screentimemanager.R
import com.example.screentimemanager.data.firebase.app.AppFirebaseDao
import com.example.screentimemanager.data.firebase.usage.UsageFirebaseDao
import com.example.screentimemanager.data.local.app.AppDatabase
import com.example.screentimemanager.data.local.usage.UsageDatabase
import com.example.screentimemanager.data.repository.AppRepository
import com.example.screentimemanager.data.repository.UsageRepository
import com.example.screentimemanager.util.Compat.getParcelableExtraCompat
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch


class AppSettingActivity : AppCompatActivity() {
    companion object {
        val APPLICATION_INFO = "application-info"
    }

    private var application : ApplicationInfo? = null
    private lateinit var appSettingViewModel: AppSettingViewModel

    private val hourLimit: NumberPicker by lazy { this.findViewById(R.id.application_hour_limit)}
    private val minuteLimit: NumberPicker by lazy { this.findViewById(R.id.application_minute_limit)}
    private val todaysUsage: TextView by lazy { this.findViewById(R.id.todays_usage) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_setting)

        // getting the application clicked
        application = intent.getParcelableExtraCompat(APPLICATION_INFO, ApplicationInfo::class.java)

        if (application == null) {
            println("application is null")
        }

        setupMVVM()
        setupUI()
    }

    private fun setupMVVM() {
            val appDatabase = AppDatabase.getInstance(this)
            val firebaseDatabase = FirebaseDatabase.getInstance()
            val appFirebaseDao = AppFirebaseDao(firebaseDatabase.reference)
            val appDao = appDatabase.appDao
            val appRepository = AppRepository(appFirebaseDao, appDao)

            val usageDatabase = UsageDatabase.getInstance(this)
            val usageFirebaseDao = UsageFirebaseDao(firebaseDatabase.reference)
            val usageDao = usageDatabase.usageDao
            val usageRepository = UsageRepository(usageFirebaseDao, usageDao)
            val appSettingViewModelFactory = AppSettingViewModelFactory(appRepository, usageRepository)
            appSettingViewModel = ViewModelProvider(
                this,
                appSettingViewModelFactory
            )[AppSettingViewModel::class.java]
    }

    private fun setupUI() {
        CoroutineScope(IO).launch {
            hourLimit.minValue = 0
            hourLimit.maxValue = 23

            minuteLimit.minValue = 0
            minuteLimit.maxValue = 59
            if (application != null) {
                val usageMillisec = appSettingViewModel.getAppUsage(application!!.packageName)
                val hours = (usageMillisec / (1000 * 60 * 60)).toInt()
                val minutes = (usageMillisec / (1000 * 60)).toInt() - (hours * 60)

                val formattedHours = String.format("%02d", hours)
                val formattedMinutes = String.format("%02d", minutes)

                todaysUsage.text = "$formattedHours : $formattedMinutes"
            }
        }
    }
}