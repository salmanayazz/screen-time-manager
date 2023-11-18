package com.example.screentimemanager.ui.appSetting

import android.content.pm.ApplicationInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.screentimemanager.R

class AppSetting : AppCompatActivity() {

    private lateinit var application : ApplicationInfo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_setting)

        // getting the application clicked

    }
}