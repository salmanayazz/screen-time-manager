package com.example.screentimemanager.ui.appsettings

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.view.View
import android.widget.NumberPicker
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.screentimemanager.R
import com.example.screentimemanager.data.local.app.App
import com.example.screentimemanager.data.repository.AppRepository
import com.example.screentimemanager.data.repository.UsageRepository
import com.example.screentimemanager.ui.appsettings.AppSettingsActivity
import com.example.screentimemanager.ui.appsettings.AppSettingsActivity.Companion.APPLICATION_INFO
import com.example.screentimemanager.ui.appsettings.AppSettingsViewModel
import com.example.screentimemanager.ui.appsettings.AppSettingsViewModelFactory
import junit.framework.TestCase.assertTrue
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.MockitoAnnotations.openMocks
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify


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

        val intent = Intent(ApplicationProvider.getApplicationContext(), AppSettingsActivity::class.java)
        intent.putExtra(APPLICATION_INFO, appInfo)

        // Launch the activity with Intent
        val scenario = ActivityScenario.launch<AppSettingsActivity>(intent)

        scenario.onActivity {
            // Replace the ViewModel
            it.setSetupFunction {
                println("I have runned in the test")
                it.appSettingsViewModel = mockViewModel
            }
            // Invoke
            it.invokeSetup()
        }
    }

    @Test
    fun testLoad() {
        // mock the viewmodel function
        Mockito.`when`(mockViewModel.getAppUsage(any())).thenReturn(0)

        Mockito.`when`(mockViewModel.getAppData(any()))
            .thenReturn(App(
                "com.example.app",
                true,
                1000 * 60 * 60 * 2 + 1000 * 60 * 10 // 2h 10min
            ))

        val intent = Intent(ApplicationProvider.getApplicationContext(), AppSettingsActivity::class.java)
        intent.putExtra(APPLICATION_INFO, appInfo)

        // launch the activity with Intent
        val scenario = ActivityScenario.launch<AppSettingsActivity>(intent)

        scenario.onActivity {
            // replace the ViewModel
            it.setSetupFunction {
                it.appSettingsViewModel = mockViewModel
            }
            // run setup and setupUI
            it.invokeSetup()
            it.setupUI()
        }

        // verify that the viewmodel methods get called
        verify(mockViewModel).getAppUsage(any())
        verify(mockViewModel).getAppData(eq(appInfo.packageName))

        // check to see if the on-screen elements have been properly set
        onView(withId(R.id.enable_time_limit)).check(matches(isChecked()))
        onView(withId(R.id.application_hour_limit)).check(hasValue(2))
        onView(withId(R.id.application_minute_limit)).check(hasValue(10))
    }

    @Test
    fun testSubmitButton() {
        // set values to on-screen elements
        onView(withId(R.id.application_hour_limit)).perform(setNumber(1))
        onView(withId(R.id.application_minute_limit)).perform(setNumber(1))
        onView(withId(R.id.enable_time_limit)).perform(click())

        // test submit
        onView(withId(R.id.submit_btn)).perform(click())

        // verify that the time limit settings were saved
        Mockito.verify(mockViewModel).setTimeLimit(
            eq(appInfo.packageName),
            eq(true),
            eq(1000 * 60 * 60 + 1000 * 60) // 1h 1min
        )
    }

    /**
     * returns an application info object for the first installed app
     */
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

    /**
     * sets the number of the number picker
     */
    fun setNumber(num: Int): ViewAction {
        return object : ViewAction {
            override fun perform(uiController: UiController?, view: View) {
                val np = view as NumberPicker
                np.value = num
            }

            override fun getDescription(): String {
                return "Set the passed number into the NumberPicker"
            }

            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(NumberPicker::class.java)
            }
        }
    }

    /**
     * checks if the number picker has the expected value
     */
    fun hasValue(expectedValue: Int): ViewAssertion {
        return ViewAssertion { view, noViewFoundException ->
            if (noViewFoundException != null) {
                throw noViewFoundException
            }

            val numberPicker = view as NumberPicker
            val actualValue = numberPicker.value

            if (actualValue != expectedValue) {
                throw AssertionError("Expected value: $expectedValue but was: $actualValue")
            }
        }
    }
}





