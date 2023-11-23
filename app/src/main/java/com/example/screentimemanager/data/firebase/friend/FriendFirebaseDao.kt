package com.example.screentimemanager.data.firebase.friend

import com.example.screentimemanager.data.firebase.user.UserFirebase
import com.google.firebase.database.DatabaseReference

class FriendFirebaseDao(
    private val database: DatabaseReference
) {

    // use database.child.("friends") to query firebase

    /**
     * @return
     * return list of the user's friends
     */
    suspend fun getFriendList(): List<UserFirebase> {
        return listOf()
    }

    /**
     * @return
     * return list of the user's friend requests
     */
    suspend fun getFriendRequestList(): List<UserFirebase> {
        return listOf()
    }

    /**
     * @param email
     * sends a friend request to the user with the given email
     */
    suspend fun sendFriendRequest(email: String) {
        
    }

    /**
     * @param email
     * accepts the friend request from the user with the given email
     */
    suspend fun acceptFriendRequest(email: String) {
        
    }

    /**
     * @param email
     * declines friend request/removes friend with the given email
     */
    suspend fun deleteFriend(email: String) {

    }
}