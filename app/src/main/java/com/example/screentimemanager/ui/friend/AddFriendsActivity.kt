package com.example.screentimemanager.ui.friend

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ListView
import com.example.screentimemanager.R

class AddFriendsActivity : AppCompatActivity() {
    private lateinit var etSearchFriend: EditText
    private lateinit var ltFriendRequest: ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friends)
    }
}