package com.example.screentimemanager.ui.friend

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

        etSearchFriend.isFocusableInTouchMode = true
        etSearchFriend.requestFocus()
        etSearchFriend.setOnKeyListener(object: OnKeyListener{
            override fun onKey(view: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if(event!!.action == KeyEvent.ACTION_DOWN
                    && keyCode == KeyEvent.KEYCODE_ENTER){
                    val friend = null //userRepository.getUser(etSearchFriend.text.toString())
                    /*if(friend != null){
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
                    }*/
                    return true
                }
                return false
            }

        })
        friendRepository.getFriendRequestList()
        val adapter = FriendRequestListAdapter(this, requestFriendName)
        friendRepository.friendRequestList.observe(this){
            CoroutineScope(IO).launch{
                friendRequests = it as ArrayList<String>
                requestFriendName = ArrayList()
                for(friend in friendRequests){
                    requestFriendName.add(userRepository.getUser(friend))
                }
                adapter.clear()
                adapter.addAll(requestFriendName)
                adapter.notifyDataSetChanged()
            }
        }
        lvFriendRequest.adapter = adapter

    }
}