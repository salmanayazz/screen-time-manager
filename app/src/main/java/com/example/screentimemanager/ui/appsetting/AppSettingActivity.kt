package com.example.screentimemanager.ui.appsetting

import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.widget.Button
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
import com.example.screentimemanager.ui.appSetting.AppSettingViewModel
import com.example.screentimemanager.ui.appSetting.AppSettingViewModelFactory
import com.example.screentimemanager.util.Compat.getParcelableExtraCompat

import com.google.android.material.switchmaterial.SwitchMaterial
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

    private val hourSelector: NumberPicker by lazy { this.findViewById(R.id.application_hour_limit)}
    private val minuteSelector: NumberPicker by lazy { this.findViewById(R.id.application_minute_limit)}
    private val todaysUsage: TextView by lazy { this.findViewById(R.id.todays_usage) }
    private val submitBtn: Button by lazy { this.findViewById(R.id.submit_btn) }
    private val cancelBtn: Button by lazy { this.findViewById(R.id.cancel_btn) }
    private val enableTimeLimit: SwitchMaterial by lazy { this.findViewById(R.id.enable_time_limit) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_setting)

        // getting the application clicked
        application = intent.getParcelableExtraCompat(APPLICATION_INFO, ApplicationInfo::class.java)

        setupMVVM()
        setupUI()
        setupListeners()
    }

    /**
     * sets up the view model
     */
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

            // create the view model
            appSettingViewModel = ViewModelProvider(
                this,
                appSettingViewModelFactory
            )[AppSettingViewModel::class.java]
    }

    /**
     * adds the data to the UI
     */
    private fun setupUI() {
        if (application == null) { return }
        CoroutineScope(IO).launch {
            // set the number picker max and min values
            hourSelector.minValue = 0
            hourSelector.maxValue = 23
            minuteSelector.minValue = 0
            minuteSelector.maxValue = 59

            // set the app usage data
            val usageMillisec = appSettingViewModel.getAppUsage(application!!.packageName)
            val hoursUsage = (usageMillisec / (1000 * 60 * 60))
            val minutesUsage = (usageMillisec / (1000 * 60)) - (hoursUsage * 60)

            val formattedHours = String.format("%02d", hoursUsage)
            val formattedMinutes = String.format("%02d", minutesUsage)
            todaysUsage.text = "$formattedHours : $formattedMinutes"

            // set the number pickers to the previous time limit the user picked
            val appData = appSettingViewModel.getAppData(application!!.packageName) ?: return@launch
            val hoursLimit = (appData.timeLimit / (1000 * 60 * 60))
            val minutesLimit = (appData.timeLimit / (1000 * 60)) - (hoursLimit * 60)
            hourSelector.value = hoursLimit.toInt()
            minuteSelector.value = minutesLimit.toInt()

            enableTimeLimit.isChecked = appData.hasLimit
        }
    }
    
    /**
     * sets up the listeners for the submit and cancel buttons
     */
    private fun setupListeners() {
        submitBtn.setOnClickListener() {
            if (application == null) { return@setOnClickListener }
            val timeLimit = (hourSelector.value * 60 * 60 * 1000 + minuteSelector.value * 60 * 1000).toLong()
            val hasLimit = enableTimeLimit.isChecked
            
            appSettingViewModel.setTimeLimit(application!!.packageName, hasLimit, timeLimit)
            finish()
        }
        cancelBtn.setOnClickListener() {
            finish()
        }
    }
}