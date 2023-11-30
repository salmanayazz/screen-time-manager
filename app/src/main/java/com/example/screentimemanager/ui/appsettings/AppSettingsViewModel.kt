package com.example.screentimemanager.ui.appsettings

import androidx.lifecycle.ViewModel
import com.example.screentimemanager.data.local.app.App
import com.example.screentimemanager.data.repository.AppRepository
import com.example.screentimemanager.data.repository.UsageRepository
import com.example.screentimemanager.util.Util.getCurrentDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

open class AppSettingsViewModel(
    private val appRepository: AppRepository,
    private val usageRepository: UsageRepository
): ViewModel() {
    /**
     * get the usage of the app for the current day
     * @param appName
     * the name of the app
     * @return the usage of the app for the current day in milliseconds
     */
    open fun getAppUsage(appName: String): Long {
        val (day, month, year) = getCurrentDate()
        val usageData = usageRepository.getUsageData(day, month, year)
        return usageData.find {
            it.appName == appName
        }?.usage ?: 0
    }

    /**
     * set the time limit for the app
     * @param appName 
     * the name of the app
     * @param hasLimit 
     * whether the app has a time limit
     * @param timeLimit 
     * the time limit for the app
     */
    open fun setTimeLimit(appName: String, hasLimit: Boolean, timeLimit: Long) {
        CoroutineScope(IO).launch {
            appRepository.setAppLimit(appName, hasLimit, timeLimit)
        }
    }

    /**
     * get the app data
     * @param appName 
     * the name of the app
     * @return the app data
     */
    open fun getAppData(appName: String): App? {
        return appRepository.getApp(appName)
    }
}