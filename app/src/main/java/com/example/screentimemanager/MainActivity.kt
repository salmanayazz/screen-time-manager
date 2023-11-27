package com.example.screentimemanager

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.screentimemanager.appusage.AppUsageService
import com.example.screentimemanager.data.firebase.user.UserFirebase
import com.example.screentimemanager.data.firebase.user.UserFirebaseDao
import com.example.screentimemanager.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userFirebaseDao: UserFirebaseDao


    private lateinit var navigationView: NavigationView
    private lateinit var menuName: TextView
    private lateinit var menuEmail: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        var headerView = navigationView.getHeaderView(0)
        menuName = headerView.findViewById(R.id.nav_header_user_name)
        menuEmail = headerView.findViewById(R.id.nav_header_email)

        // update the user information on swipe menu
        updateUserSwipeMenu()


        // setting the sign out click listener
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_sign_out -> {
                    firebaseAuth.signOut()
                    updateUserSwipeMenu()
                    true
                }
                else -> false
            }
        }



        // start the AppUsageService
        val serviceIntent = Intent(this, AppUsageService::class.java)
        this.startForegroundService(serviceIntent)
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
        }
    }
}