package com.example.screentimemanager.ui.profileSetting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.example.screentimemanager.R
import com.example.screentimemanager.data.firebase.user.UserFirebase
import com.example.screentimemanager.data.firebase.user.UserFirebaseDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ProfileSetting : AppCompatActivity() {
    private lateinit var profileImage: ImageView
    private lateinit var emailTextView: TextView
    private lateinit var nameTextView: TextView

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userFirebaseDao: UserFirebaseDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setting)

        profileImage = findViewById(R.id.profile_activity_image)
        emailTextView= findViewById(R.id.profile_activity_email)
        nameTextView = findViewById(R.id.profile_activity_name)

        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference
        userFirebaseDao = UserFirebaseDao(databaseReference)

        setUserProfileSetting()
    }
    private fun setUserProfileSetting(){
        if (firebaseAuth.currentUser != null){
            // TODO have to set up the profile picture as well
            val currentUserEmail = firebaseAuth.currentUser!!.email
            emailTextView.text = currentUserEmail
            GlobalScope.launch(Dispatchers.Main) {
                var user: UserFirebase? = userFirebaseDao.getUser(currentUserEmail!!)
                if (user != null){
                    nameTextView.text = user.firstName + " " + user.lastName
                }else{
                    nameTextView.text = "User"
                }
            }
        }
    }
}