package com.example.screentimemanager.data.firebase.friend

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class FriendFirebaseDao(
    private val database: DatabaseReference
) {
    // firebase variables
    val IS_REQUEST = "request"
    val SENDER_EMAIL = "senderEmail"
    val RECEIVER_EMAIL = "receiverEmail"

    private val friendsRef: DatabaseReference = database.child("friends")
    val userEmail = FirebaseAuth.getInstance().currentUser?.email!!

    private val _friendList = MutableLiveData<List<String>>()
    val friendList: LiveData<List<String>> = _friendList

    private val _friendRequestList = MutableLiveData<List<String>>()
    val friendRequestList: LiveData<List<String>> = _friendRequestList

    /**
     * updates the friendList live data variable
     */
    fun getFriendList() {
        val friends: ArrayList<String> = ArrayList()
        friendsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (receiver in snapshot.children) {
                    for (sender in receiver.children) {
                        if (receiver.key == userEmail || sender.key == userEmail) {
                            // only return emails that have isRequest set to false
                            val isRequest = sender.child(IS_REQUEST).getValue(Boolean::class.java) ?: true
                            val friendEmail = sender.key
                            if (!isRequest && friendEmail != null) {
                                friends.add(friendEmail)
                            }
                        }
                    }
                }
                _friendList.value = friends
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("getFriendRequestList() error", error.message)
            }
        })
    }

    /**
     * updates the friendRequestList live data variable
     */
    fun getFriendRequestList() {
        val requestSenderList: ArrayList<String> = ArrayList()

        friendsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (receiver in snapshot.children) {
                    println("FOUND A PERSON")
                    if (receiver.key == userEmail) {
                        println("FOUND PERSON")
                        for (sender in receiver.children) {
                            println("THERES A FRIEND")
                            val isRequest = sender.child(IS_REQUEST).getValue(Boolean::class.java) ?: false
                            val friendEmail = sender.key
                            println("isRequest $isRequest")
                            println("friendEmail $friendEmail")
                            if (isRequest && friendEmail != null) {
                                requestSenderList.add(friendEmail)
                            }
                        }
                    }
                }
                _friendRequestList.value = requestSenderList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("getFriendRequestList() error", error.message)
            }
        })
    }


    /**
     * @param friendEmail
     * sends a friend request to the user with the given email
     */
    suspend fun sendFriendRequest(friendEmail: String) {
        friendsRef.child(friendEmail).child(userEmail).setValue(FriendFirebase(friendEmail, userEmail, true))
    }

    /**
     * @param friendEmail
     * accepts the friend request from the user with the given email
     */
    suspend fun acceptFriendRequest(friendEmail: String) {
        friendsRef.child(userEmail).child(friendEmail).setValue(FriendFirebase(userEmail, friendEmail, false))
    }

    /**
     * @param friendEmail
     * declines friend request/removes friend with the given email
     */
    suspend fun deleteFriend(friendEmail: String) {
        // delete both possible variations
        friendsRef.child(userEmail).child(friendEmail).removeValue()
        friendsRef.child(friendEmail).child(userEmail).removeValue()
    }
}