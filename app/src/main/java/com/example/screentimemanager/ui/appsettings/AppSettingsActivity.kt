package com.example.screentimemanager.ui.appsettings

import android.content.ContentValues.TAG
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageView
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
import com.example.screentimemanager.util.Util

import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class AppSettingsActivity : AppCompatActivity() {
    companion object {
        val APPLICATION_INFO = "application-info"
    }

    private var application : ApplicationInfo? = null
    open lateinit var appSettingsViewModel: AppSettingsViewModel

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

        val appIcon: ImageView = findViewById(R.id.app_icon)
        val appName: TextView = findViewById(R.id.app_name)

        appIcon.setImageDrawable(application?.loadIcon(packageManager))
        appName.text = application?.loadLabel(packageManager).toString()
        supportActionBar?.hide()

        _setupMVVM()
        setupUI()
        setupListeners()
    }


    /**
     * sets up the view model
     */
    private var _setupMVVM: () -> Unit = {
        val appDatabase = AppDatabase.getInstance(this)
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val appFirebaseDao = AppFirebaseDao(firebaseDatabase.reference)
        val appDao = appDatabase.appDao
        val appRepository = AppRepository(appFirebaseDao, appDao)

        val usageDatabase = UsageDatabase.getInstance(this)
        val usageFirebaseDao = UsageFirebaseDao(firebaseDatabase.reference)
        val usageDao = usageDatabase.usageDao
        val usageRepository = UsageRepository(usageFirebaseDao, usageDao)
        val appSettingsViewModelFactory = AppSettingsViewModelFactory(appRepository, usageRepository)

        // create the view model
        appSettingsViewModel = ViewModelProvider(
            this,
            appSettingsViewModelFactory
        )[AppSettingsViewModel::class.java]
    }

    fun setSetupFunction(setupFunction: () -> Unit) {
        _setupMVVM = setupFunction
    }

    fun invokeSetup() {
        _setupMVVM()
    }

    /**
     * adds the data to the UI
     */
    fun setupUI() {
        if (application == null) { return }

        // set the number picker max and min values on the main thread
        runOnUiThread {
            hourSelector.minValue = 0
            hourSelector.maxValue = 23
            minuteSelector.minValue = 0
            minuteSelector.maxValue = 59
        }

        CoroutineScope(IO).launch {
            // set the app usage data
            val usageMillisec = appSettingsViewModel.getAppUsage(application!!.packageName)
            val (hours, mins) = Util.millisecToHoursAndMins(usageMillisec)

    
            runOnUiThread {
                var text = "$hours hour"
                if (hours != 1) { text += "s"}
                text += ", $mins minute"
                if (mins != 1) { text += "s"}

                todaysUsage.text = text
            }

            // set the number pickers to the previous time limit the user picked
            val appData = appSettingsViewModel.getAppData(application!!.packageName) ?: return@launch
            val hoursLimit = (appData.timeLimit / (1000 * 60 * 60))
            val minutesLimit = (appData.timeLimit / (1000 * 60)) - (hoursLimit * 60)

            runOnUiThread {
                hourSelector.value = hoursLimit.toInt()
                minuteSelector.value = minutesLimit.toInt()
                enableTimeLimit.isChecked = appData.hasLimit
            }
        }
    }




    /**
     * sets up the listeners for the submit and cancel buttons
     */
    private fun setupListeners() {
        submitBtn.setOnClickListener() {
            if (application == null) {
                Log.e(TAG, "application was null")
                return@setOnClickListener
            }
            Log.i(TAG, "Saving settings for app ${application!!.packageName}")
            val timeLimit = (hourSelector.value * 60 * 60 * 1000 + minuteSelector.value * 60 * 1000).toLong()
            val hasLimit = enableTimeLimit.isChecked
            
            appSettingsViewModel.setTimeLimit(application!!.packageName, hasLimit, timeLimit)
            finish()
        }
        cancelBtn.setOnClickListener() {
            finish()
        }
    }

}