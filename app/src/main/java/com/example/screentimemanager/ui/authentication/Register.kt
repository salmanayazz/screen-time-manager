package com.example.screentimemanager.ui.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.screentimemanager.R
import com.example.screentimemanager.data.firebase.user.UserFirebase
import com.example.screentimemanager.data.firebase.user.UserFirebaseDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Register : AppCompatActivity() {
    private lateinit var loginTextView: TextView
    private lateinit var registerBtn: Button
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var user: UserFirebase
    private lateinit var userFirebaseDao: UserFirebaseDao
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference= FirebaseDatabase.getInstance().reference
        userFirebaseDao= UserFirebaseDao(databaseReference)

        loginTextView = findViewById<TextView>(R.id.login_button_text)
        registerBtn = findViewById<Button>(R.id.register_button)

        firstNameEditText = findViewById(R.id.first_name_register)
        lastNameEditText = findViewById(R.id.last_name_register)
        emailEditText = findViewById(R.id.email_register_form)
        passwordEditText = findViewById(R.id.password_register_form)

        registerBtn.setOnClickListener{
            registerListener()
        }
        loginTextView.setOnClickListener{
            finish() // to go back to login activity
        }
    }

    private fun registerListener() {
        val email: String = emailEditText.text.toString()
        val password: String = passwordEditText.text.toString()
        val fName: String = firstNameEditText.text.toString()
        val lName: String = lastNameEditText.text.toString()

        if (email.isNullOrEmpty() or password.isNullOrEmpty()
            or lName.isNullOrEmpty() or fName.isNullOrEmpty()){
            Toast.makeText(this,"Empty fields", Toast.LENGTH_SHORT).show()
        }else{
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
                if(it.isSuccessful){
                    user= UserFirebase(email,fName,lName,password,null)
                    GlobalScope.launch {
                        userFirebaseDao.addUser(user)
                    }
                    Toast.makeText(this,"Logged In", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else{
                    Toast.makeText(this,"Failed to create account", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}