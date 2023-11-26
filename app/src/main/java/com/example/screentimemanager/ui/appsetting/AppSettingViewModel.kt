package com.example.screentimemanager.ui.appsetting

import androidx.lifecycle.ViewModel
import com.example.screentimemanager.data.repository.AppRepository
import com.example.screentimemanager.data.repository.UsageRepository
import com.example.screentimemanager.util.Util.getCurrentDate

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


}