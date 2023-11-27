package com.example.screentimemanager.ui

import androidx.lifecycle.ViewModel
import com.example.screentimemanager.data.local.app.App
import com.example.screentimemanager.data.repository.AppRepository
import com.example.screentimemanager.data.repository.UsageRepository
import com.example.screentimemanager.util.Util.getCurrentDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class AppSettingViewModel(
    private val appRepository: AppRepository,
    private val usageRepository: UsageRepository
): ViewModel() {
    fun getAppUsage(appName: String): Long {
        val (day, month, year) = getCurrentDate()
        val usageData = usageRepository.getUsageData(day, month, year)
        return usageData.find {
            it.appName == appName
        }?.usage ?: 0
    }

    fun setTimeLimit(appName: String, hasLimit: Boolean, timeLimit: Long) {
        CoroutineScope(IO).launch {
            appRepository.setAppLimit(appName, hasLimit, timeLimit)
        }
    }

    fun getAppData(appName: String): App? {
        return appRepository.getApp(appName)
    }
}