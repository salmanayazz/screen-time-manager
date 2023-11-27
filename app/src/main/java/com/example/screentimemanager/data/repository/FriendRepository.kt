package com.example.screentimemanager.data.repository

import com.example.screentimemanager.data.firebase.friend.FriendFirebaseDao
import com.example.screentimemanager.data.firebase.user.UserFirebase

class FriendRepository(
    private val friendDao: FriendFirebaseDao
) {
    /**
     * @return
     * return list of the user's friends
     */
    fun getFriendList(): List<String> {
        return friendDao.getFriendList()
    }

    /**
     * @return
     * return list of the user's friend requests
     */
    fun getFriendRequestList(): List<String> {
        return friendDao.getFriendRequestList()
    }

    /**
     * @param email
     * sends a friend request to the user with the given email
     */
    suspend fun sendFriendRequest(email: String) {
        return friendDao.sendFriendRequest(email)
    }

    /**
     * @param email
     * accepts the friend request from the user with the given email
     */
    suspend fun acceptFriendRequest(email: String) {
        return friendDao.acceptFriendRequest(email)
    }

    /**
     * @param email
     * declines friend request/removes friend with the given email
     */
    suspend fun deleteFriend(email: String) {
        friendDao.deleteFriend(email)
    }
}