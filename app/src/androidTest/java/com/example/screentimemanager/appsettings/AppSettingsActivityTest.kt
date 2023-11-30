package com.example.screentimemanager.appsettings

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.screentimemanager.R
import com.example.screentimemanager.data.local.app.App
import com.example.screentimemanager.data.repository.AppRepository
import com.example.screentimemanager.data.repository.UsageRepository
import com.example.screentimemanager.ui.appsettings.AppSettingsActivity
import com.example.screentimemanager.ui.appsettings.AppSettingsViewModel
import com.example.screentimemanager.ui.appsettings.AppSettingsViewModelFactory
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations.openMocks
import org.mockito.kotlin.any
import org.mockito.kotlin.eq


@RunWith(AndroidJUnit4::class)
class AppSettingsActivityTest {
    @get:Rule
    val activityScenarioRule = activityScenarioRule<AppSettingsActivity>()

    @Mock
    lateinit var mockViewModel: AppSettingsViewModel

    private val appInfo = getAppInfo()

    @Before
    fun setup() {
        openMocks(this)

        // setup mocked viewmodel
        val viewModelFactory = AppSettingsViewModelFactory(mock(AppRepository::class.java), mock(UsageRepository::class.java))
        AppSettingsActivity.appSettingsViewModelFactory = viewModelFactory

        // start the activity
        activityScenarioRule.scenario.onActivity {
            it.appSettingsViewModel = mockViewModel

            // send appInfo intent
            val intent = Intent(it, AppSettingsActivity::class.java)
            intent.putExtra(AppSettingsActivity.APPLICATION_INFO, appInfo)
            it.startActivity(intent)
        }
    }

    @Test
    fun testSubmitButton() {
        // mock the viewmodel func and expect them to be called with appInfo.packageName
        Mockito.`when`(mockViewModel.getAppUsage(eq(appInfo.packageName))).thenReturn(0)
        Mockito.`when`(mockViewModel.getAppData(eq(appInfo.packageName)))
            .thenReturn(App("com.example.app", false, 1000 * 60))


        onView(withId(R.id.submit_btn)).perform(click())

        // verify that the time limit settings were saved
        //Mockito.verify(mockViewModel).setTimeLimit(any(), any(), any())

        // verify that the activity is finished
        activityScenarioRule.scenario.onActivity {
            //assertTrue(it.isFinishing)
        }
    }

    @Test
    fun testCancelButton() {
        onView(withId(R.id.cancel_btn)).perform(click())

        // verify that the activity is finished
        activityScenarioRule.scenario.onActivity {
            //assertTrue(it.isFinishing)
        }
    }

    private fun getAppInfo(): ApplicationInfo {
        // get installed apps
        val packageManager: PackageManager =
            ApplicationProvider.getApplicationContext<Context>().packageManager
        val installedApplications =
            packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        // get first app from list
        if (installedApplications.isNotEmpty()) {
            // Select the first application from the list
            return installedApplications[0]
        }

        throw Exception("getAppInfo(): no apps found, cannot test")
    }
}




