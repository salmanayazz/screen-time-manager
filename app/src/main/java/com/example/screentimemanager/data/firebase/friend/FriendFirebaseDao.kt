package com.example.screentimemanager.data.firebase.friend

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.screentimemanager.friendRequestNotification.FriendRequestNotificationService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

open class FriendFirebaseDao(
    private val database: DatabaseReference,
    val userEmail: String = FirebaseAuth.getInstance().currentUser?.email?: "Not Logged In"
) {
    // firebase variables
    val IS_REQUEST = "request"
    val SENDER_EMAIL = "senderEmail"
    val RECEIVER_EMAIL = "receiverEmail"
    val sanitUserEmail = userEmail.replace("@", "(").replace(".", ")")

    private val friendsRef: DatabaseReference = database.child("friends")

    private val _friendList = MutableLiveData<List<String>>()
    val friendList: LiveData<List<String>> = _friendList

    private val _friendRequestList = MutableLiveData<List<String>>()
    val friendRequestList: LiveData<List<String>> = _friendRequestList

    /**
     * updates the friendList live data variable
     */
    fun getFriendList() {
        //using addValueEventListener to get the entire contents
        friendsRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                updateFriendList(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Database error on getFriendList()", error.message)
            }

        })
    }

    private fun updateFriendList(snapshot: DataSnapshot){
        val friends: ArrayList<String> = ArrayList()
        for (receiver in snapshot.children) {
            for (sender in receiver.children) {
                if (receiver.key == sanitUserEmail || sender.key == sanitUserEmail) {
                    // only return emails that have isRequest set to false
                    val isRequest = sender.child(IS_REQUEST).getValue(Boolean::class.java) ?: true
                    var friendEmail: String? = null
                    if(receiver.key == sanitUserEmail){
                        friendEmail = sender.key
                    }
                    else{
                        friendEmail = receiver.key
                    }
                    if (!isRequest && friendEmail != null) {
                        friends.add(friendEmail)
                    }
                }
            }
        }

        val unsanitFriends = friends.map {
            it.replace("(", "@").replace(")", ".")
        }

        _friendList.value = unsanitFriends
    }

    /**
     * updates the friendRequestList live data variable
     */
    fun getFriendRequestList() {
        //using addValueEventListener to get the entire contents
        friendsRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                updateFriendRequestList(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Database error on getFriendRequestList()", error.message)
            }

        })
    }

    private fun updateFriendRequestList(snapshot: DataSnapshot){
        val requestSenderList: ArrayList<String> = ArrayList()
        for (receiver in snapshot.children) {
            if (receiver.key == sanitUserEmail) {
                for (sender in receiver.children) {
                    val isRequest = sender.child(IS_REQUEST).getValue(Boolean::class.java) ?: false
                    val friendEmail = sender.key
                    if (isRequest && friendEmail != null) {
                        requestSenderList.add(friendEmail)
                    }
                }
            }
        }

        val unsanitReqSenderList = requestSenderList.map {
            it.replace("(", "@").replace(")", ".")
        }

        _friendRequestList.value = unsanitReqSenderList
    }


    /**
     * @param friendEmail
     * sends a friend request to the user with the given email
     */
    suspend fun sendFriendRequest(friendEmail: String) {
        val friendEmail = friendEmail.replace("@", "(").replace(".", ")")
        friendsRef.child(friendEmail).child(sanitUserEmail).setValue(FriendFirebase(friendEmail, sanitUserEmail, true))
    }

    /**
     * @param friendEmail
     * accepts the friend request from the user with the given email
     */
    suspend fun acceptFriendRequest(friendEmail: String) {
        val friendEmail = friendEmail.replace("@", "(").replace(".", ")")
        friendsRef.child(sanitUserEmail).child(friendEmail).setValue(FriendFirebase(sanitUserEmail, friendEmail, false))
    }

    /**
     * @param friendEmail
     * declines friend request/removes friend with the given email
     */
    suspend fun deleteFriend(friendEmail: String) {
        val friendEmail = friendEmail.replace("@", "(").replace(".", ")")
        // create a map for the updates
        val updates = HashMap<String, Any?>()

        // add paths to the map
        updates["/${sanitUserEmail}/${friendEmail}"] = null
        updates["/${friendEmail}/${sanitUserEmail}"] = null

        // update the database
        friendsRef.updateChildren(updates)
    }
}