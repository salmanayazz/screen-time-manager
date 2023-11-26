package com.example.screentimemanager.ui.appsetting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.screentimemanager.data.repository.AppRepository
import com.example.screentimemanager.data.repository.UsageRepository


/**
 * factory for creating ViewModels for use with the repositories
 */
class AppSettingViewModelFactory (
    private val appRepository: AppRepository?,
    private val usageRepository: UsageRepository
) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create (modelClass: Class<T>) : T {
        if (modelClass.isAssignableFrom(AppSettingViewModel::class.java)) {
            if (appRepository != null && usageRepository != null) {
                return AppSettingViewModel(appRepository, usageRepository) as T
            }
        }

        throw IllegalArgumentException("Error with AppSettingViewModelFactory class")
    }
}