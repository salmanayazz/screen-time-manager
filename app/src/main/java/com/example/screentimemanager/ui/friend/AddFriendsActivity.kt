package com.example.screentimemanager.ui.friend

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import com.example.screentimemanager.R
import com.example.screentimemanager.data.firebase.friend.FriendFirebaseDao
import com.example.screentimemanager.data.firebase.user.UserFirebase
import com.example.screentimemanager.data.firebase.user.UserFirebaseDao
import com.example.screentimemanager.data.repository.FriendRepository
import com.example.screentimemanager.data.repository.UserRepository
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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
        for(request in friendRequests){
            requestFriendName.add(userRepository.getUser(request))
        }
        friendRepository.getFriendRequestList()
        friendRepository.friendRequestList.observe(this){
            friendRequests = it as ArrayList<String>
        }

        etSearchFriend.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //Do nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //Do nothing
            }

            override fun afterTextChanged(str: Editable?) {
                val friend = userRepository.getUser(str.toString())
                if(friend != null){
                    val builder = AlertDialog.Builder(this@AddFriendsActivity)
                    builder.setTitle("Add friend")
                    builder.setMessage("Do you want to add ${friend.firstName} ${friend.lastName}?")
                    builder.setPositiveButton("Add"){
                            _, _ -> {
                                CoroutineScope(IO).launch {
                                    friendRepository.sendFriendRequest(friend.email)
                                }
                            }
                    }
                    builder.setNegativeButton("Cancel"){
                            dialog, _ -> {
                                dialog.dismiss()
                            }
                    }
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.show()
                }
            }
        })

        val adapter = FriendRequestListAdapter(this, requestFriendName)
        lvFriendRequest.adapter = adapter

    }
}