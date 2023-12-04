package com.example.screentimemanager.ui.friend

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.KeyListener
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.View.OnKeyListener
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import com.example.screentimemanager.R
import com.example.screentimemanager.data.firebase.friend.FriendFirebaseDao
import com.example.screentimemanager.data.firebase.user.UserFirebase
import com.example.screentimemanager.data.firebase.user.UserFirebaseDao
import com.example.screentimemanager.data.repository.FriendRepository
import com.example.screentimemanager.data.repository.UserRepository
import com.example.screentimemanager.friendRequestNotification.FriendRequestNotificationService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class AddFriendsActivity : AppCompatActivity() {
    private lateinit var etSearchFriend: EditText
    private lateinit var lvFriendRequest: ListView
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var userFirebaseDao: UserFirebaseDao
    private lateinit var userRepository: UserRepository
    private lateinit var friendFirebaseDao: FriendFirebaseDao
    private lateinit var friendRepository: FriendRepository
    private lateinit var friendRequests: ArrayList<String>
    private lateinit var requestFriendName: ArrayList<UserFirebase>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friends)
        etSearchFriend = findViewById(R.id.et_searchFriend)
        lvFriendRequest = findViewById(R.id.lv_friendRequest)
        firebaseRef = FirebaseDatabase.getInstance().reference
        userFirebaseDao = UserFirebaseDao(firebaseRef)
        userRepository = UserRepository(userFirebaseDao)
        friendFirebaseDao = FriendFirebaseDao(firebaseRef)
        friendRepository = FriendRepository(friendFirebaseDao)
        friendRequests = ArrayList()
        requestFriendName = ArrayList()

        etSearchFriend.isFocusableInTouchMode = true
        etSearchFriend.requestFocus()
        etSearchFriend.setOnKeyListener(object: OnKeyListener{
            override fun onKey(view: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if(event!!.action == KeyEvent.ACTION_DOWN
                    && keyCode == KeyEvent.KEYCODE_ENTER){
                    CoroutineScope(IO).launch {
                        val friend = userRepository.getUser(etSearchFriend.text.toString())
                        if (friend != null) {
                            val builder = AlertDialog.Builder(this@AddFriendsActivity)
                            builder.setTitle("Add friend")
                            builder.setMessage("Do you want to add ${friend.firstName} ${friend.lastName}?")
                            builder.setPositiveButton("Add") { _, _ ->
                                run {
                                    CoroutineScope(IO).launch {
                                        friendRepository.sendFriendRequest(friend.email)
                                        /*
                                        val recipientToken = userRepository.getUser(friend.email)!!.token
                                        val userEmail: String = FirebaseAuth.getInstance().currentUser?.email!!
                                        val messageId = "${userEmail}_to_${friend.email}"
                                        val message = RemoteMessage.Builder(recipientToken)
                                            .setMessageId(messageId)
                                            .addData("type", "friend_request")
                                            .addData("senderEmail", userEmail)
                                            .build()

                                        FirebaseMessaging.getInstance().send(message)
                                        */
                                    }
                                }
                            }
                            builder.setNegativeButton("Cancel") { dialog, _ ->
                                run {
                                    dialog.dismiss()
                                }
                            }
                            runOnUiThread() {
                                val alertDialog: AlertDialog = builder.create()
                                alertDialog.show()
                            }
                        }
                    }
                    return true
                }
                return false
            }

        })
        friendRepository.getFriendRequestList()
        val adapter = FriendRequestListAdapter(this, requestFriendName)
        friendRepository.friendRequestList.observe(this){
            friendRequests = it as ArrayList<String>
            requestFriendName = ArrayList()
            for(friend in friendRequests){
                CoroutineScope(IO).launch {
                    println("friend: $friend")
                    userRepository.getUser(friend)?.let {
                        it1 -> requestFriendName.add(it1)
                    }
                }
            }
            adapter.clear()
            adapter.addAll(requestFriendName)
            adapter.notifyDataSetChanged()
        }
        lvFriendRequest.adapter = adapter

    }
}