package com.example.screentimemanager.ui.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.screentimemanager.R

class Login : AppCompatActivity() {
    private lateinit var registerTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        registerTextView = findViewById<TextView>(R.id.register_button_text)
        registerTextView.setOnClickListener{
            var intent:Intent= Intent(this,Register::class.java)
            startActivity(intent)
        }
    }
}