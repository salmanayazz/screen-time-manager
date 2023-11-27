package com.example.screentimemanager.ui.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.screentimemanager.R

class Register : AppCompatActivity() {
    private lateinit var loginTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        loginTextView = findViewById<TextView>(R.id.login_button_text)
        loginTextView.setOnClickListener{
            finish() // to go back to login activity
        }
    }
}