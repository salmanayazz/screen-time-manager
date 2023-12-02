package com.example.screentimemanager.data.firebase.friend

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val friendsRef: DatabaseReference = database.child("friends")

    private val _friendList = MutableLiveData<List<String>>()
    val friendList: LiveData<List<String>> = _friendList

    private val _friendRequestList = MutableLiveData<List<String>>()
    val friendRequestList: LiveData<List<String>> = _friendRequestList

    /**
     * updates the friendList live data variable
     */
    fun getFriendList() {
        friendsRef.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                //Will be triggered when added new friend request
                updateFriendList(snapshot)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                //Will be triggered when accepted friend request
                updateFriendList(snapshot)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                //Will be triggered when removed friend
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                //Do nothing
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
                if (receiver.key == userEmail || sender.key == userEmail) {
                    // only return emails that have isRequest set to false
                    val isRequest = sender.child(IS_REQUEST).getValue(Boolean::class.java) ?: true
                    var friendEmail: String? = null
                    if(receiver.key == userEmail){
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
        _friendList.value = friends
    }

    /**
     * updates the friendRequestList live data variable
     */
    fun getFriendRequestList() {
        friendsRef.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                // Will be triggered when added a friend request
                updateFriendRequestList(snapshot)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                //Will be triggered when accepted friend request
                updateFriendRequestList(snapshot)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                //Will be triggered when removed friend
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                //Do nothing
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Database error on getFriendRequestList()", error.message)
            }

        })
    }

    private fun updateFriendRequestList(snapshot: DataSnapshot){
        val requestSenderList: ArrayList<String> = ArrayList()
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
        // create a map for the updates
        val updates = HashMap<String, Any?>()

        // add paths to the map
        updates["/${userEmail}/${friendEmail}"] = null
        updates["/${friendEmail}/${userEmail}"] = null

        // update the database
        friendsRef.updateChildren(updates)
    }
}