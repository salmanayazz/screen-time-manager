package com.example.screentimemanager.ui.authentication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.work.WorkManager
import com.example.screentimemanager.MainActivity
import com.example.screentimemanager.R
import com.example.screentimemanager.data.firebase.user.UserFirebaseDao
import com.example.screentimemanager.usageComparisonService.UsageComparisonNotificationScheduler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Login : AppCompatActivity() {
    private lateinit var registerTextView: TextView
    private lateinit var loginBtn: Button
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance()

        registerTextView = findViewById<TextView>(R.id.register_button_text)
        loginBtn = findViewById(R.id.login_btn)
        emailEditText = findViewById(R.id.email_login_form)
        passwordEditText = findViewById(R.id.password_login_form)


        registerTextView.setOnClickListener{
            var intent: Intent = Intent(this,Register::class.java)
            startActivity(intent)
        }
        loginBtn.setOnClickListener{
            logInListener()
        }
    }

    private fun logInListener() {
        val email: String = emailEditText.text.toString()
        val password: String = passwordEditText.text.toString()

        if (email.isNullOrEmpty() or password.isNullOrEmpty()){
            Toast.makeText(this,"Empty fields",Toast.LENGTH_SHORT).show()
        }else{
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
                if(it.isSuccessful){
                    Toast.makeText(this,"Logged In",Toast.LENGTH_SHORT).show()
                    // Reset notification setup
                    resetNotificationSetup()

                    // Schedule new notification
                    scheduleNewNotification()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra("isFriendTab", true)

                    startActivity(intent)
                }
                else{
                    Toast.makeText(this,"Invalid Credentials",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun resetNotificationSetup() {
        // Cancel any existing notification work
        WorkManager.getInstance(this).cancelUniqueWork("daily_usage_notification")

        // Reset shared preference value
        val sharedPreferences = getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            remove("lastScheduledDate")
            apply()
        }
    }

    private fun scheduleNewNotification() {
        // Assume you have a method to schedule notifications
        val userEmail = FirebaseAuth.getInstance().currentUser?.email
        if (userEmail != null) {
            val scheduler = UsageComparisonNotificationScheduler(this)
            scheduler.scheduleUsageNotificationWorker(userEmail)
        }
    }

}