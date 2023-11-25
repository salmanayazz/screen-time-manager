package com.example.screentimemanager.data.firebase.friend

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class FriendFirebaseDao(
    private val database: DatabaseReference
) {
    // use database.child.("friends") to query firebase
    private val friendsRef: DatabaseReference = database.child("friends")
    val userEmail = FirebaseAuth.getInstance().currentUser?.email!!

    /**
     * @return
     * return list of the user's friends
     */
    suspend fun getFriendList(): List<String> {
        val friends: ArrayList<String> = ArrayList()
        friendsRef.addListenerForSingleValueEvent(
            object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(receiver in snapshot.children){
                        for(sender in receiver.children){
                            if(receiver.getValue(String::class.java)!!.compareTo(userEmail) == 0
                                && sender.child("isRequest").getValue(Boolean::class.java) == false){
                                friends.add(sender.getValue(String::class.java)!!)
                            }
                            else if(sender.getValue(String::class.java)!!.compareTo(userEmail) == 0
                                && sender.child("isRequest").getValue(Boolean::class.java) == false){
                                friends.add(receiver.getValue(String::class.java)!!)
                            }
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.i("getFriendList() error", error.message)
                }

            }
        )
        return friends
    }

    /**
     * @return
     * return list of the user's friend requests
     */
    suspend fun getFriendRequestList(): List<String> {
        val requestSenderList: ArrayList<String> = ArrayList()
        friendsRef.orderByChild("receiverEmail").equalTo(userEmail).addListenerForSingleValueEvent(
            object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(receiver in snapshot.children){
                        if(receiver.getValue(String::class.java)!!.compareTo(userEmail) == 0){
                            for (sender in receiver.children){
                                if(sender.child("isRequest").getValue(Boolean::class.java) == true){
                                    requestSenderList.add(sender.getValue(String::class.java)!!)
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i("getFriendRequestList() error", error.message)
                }
            }
        )
        return requestSenderList
    }

    /**
     * @param friendEmail
     * sends a friend request to the user with the given email
     */
    suspend fun sendFriendRequest(friendEmail: String) {
        friendsRef.child("receiver").child("sender").setValue(FriendFirebase(friendEmail, userEmail, true))
    }

    /**
     * @param friendEmail
     * accepts the friend request from the user with the given email
     */
    suspend fun acceptFriendRequest(friendEmail: String) {
        friendsRef.child("receiver").child("sender").setValue(FriendFirebase(userEmail, friendEmail, false))
    }

    /**
     * @param friendEmail
     * declines friend request/removes friend with the given email
     */
    suspend fun deleteFriend(friendEmail: String) {
        friendsRef.addListenerForSingleValueEvent(
            object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(receiver in snapshot.children){
                        for(sender in receiver.children){
                            if(receiver.getValue(String::class.java)!!.compareTo(friendEmail) == 0
                                && sender.getValue(String::class.java)!!.compareTo(userEmail) == 0){
                                friendsRef.child("receiver").child("sender").removeValue()
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i("deleteFriend() error", error.message)
                }

            }
        )
    }
}