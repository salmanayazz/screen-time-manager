package com.example.screentimemanager.ui.appsettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.screentimemanager.data.repository.AppRepository
import com.example.screentimemanager.data.repository.UsageRepository


/**
 * factory for creating ViewModels for use with the repositories
 */
open class AppSettingsViewModelFactory (
    private val appRepository: AppRepository,
    private val usageRepository: UsageRepository
) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create (modelClass: Class<T>) : T {
        if (modelClass.isAssignableFrom(AppSettingsViewModel::class.java)) {
            return AppSettingsViewModel(appRepository, usageRepository) as T
        }

        throw IllegalArgumentException("Error with AppSettingsViewModelFactory class")
    }
}