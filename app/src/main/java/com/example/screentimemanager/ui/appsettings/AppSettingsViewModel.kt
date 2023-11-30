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
    private val usageRepository: UsageRepository?
): ViewModel() {
    open fun getAppUsage(appName: String): Long {
        val (day, month, year) = getCurrentDate()
        val usageData = usageRepository?.getUsageData(day, month, year)
        return usageData?.find {
            it.appName == appName
        }?.usage ?: 0
    }

    open fun setTimeLimit(appName: String, hasLimit: Boolean, timeLimit: Long) {
        CoroutineScope(IO).launch {
            appRepository.setAppLimit(appName, hasLimit, timeLimit)
        }
    }

    open fun getAppData(appName: String): App? {
        return appRepository.getApp(appName)
    }
}