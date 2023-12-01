package com.example.screentimemanager

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.screentimemanager.appusage.AppUsageService
import com.example.screentimemanager.data.firebase.user.UserFirebase
import com.example.screentimemanager.data.firebase.user.UserFirebaseDao
import com.example.screentimemanager.databinding.ActivityMainBinding
import com.example.screentimemanager.ui.authentication.Login
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.Manifest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userFirebaseDao: UserFirebaseDao

    private lateinit var navigationView: NavigationView
    private lateinit var menuName: TextView
    private lateinit var menuEmail: TextView
    private lateinit var signOutBtn: Button
    private lateinit var exitBtn:Button

    private val PERMISSION_REQUEST_CODE = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        askNotificationPermission()

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_friend
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //setting the firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference
        userFirebaseDao = UserFirebaseDao(databaseReference)

        // setting up the swipe bar menu
        navigationView = findViewById(R.id.navigation_view)
        menuName = findViewById(R.id.nav_header_user_name)
        menuEmail = findViewById(R.id.nav_header_email)
        signOutBtn = findViewById(R.id.signout_btn_menu)
        exitBtn = findViewById(R.id.exit_btn_menu)


        signOutBtn.setOnClickListener{
            onSignOutListener()
        }
        exitBtn.setOnClickListener{
            finishAffinity()
        }

        // update the user information on swipe menu
        updateUserSwipeMenu()


        // start the AppUsageService
        val serviceIntent = Intent(this, AppUsageService::class.java)
        this.startForegroundService(serviceIntent)
    }


    private fun onSignOutListener(){
        firebaseAuth.signOut()
        updateUserSwipeMenu()
    }

    private fun onSignInListener(){
        startActivity(Intent(this, Login::class.java))
    }

    private fun updateUserSwipeMenu(){
        // Set user information on swipe menu
        if(firebaseAuth.currentUser != null){
            val currentUserEmail = firebaseAuth.currentUser!!.email
            menuEmail.text = currentUserEmail
            GlobalScope.launch(Dispatchers.Main) {
                var user: UserFirebase? = userFirebaseDao.getUser(currentUserEmail!!)
                if (user != null){
                    menuName.text = user.firstName + " " + user.lastName
                }else{
                    menuName.text = "User"
                }
            }
        }else{
            menuName.text = "User"
            menuEmail.text= "user@exmaple.com"
            signOutBtn.text = "Sign In"
            signOutBtn.setOnClickListener{
                onSignInListener()
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT < 23) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
    }
}