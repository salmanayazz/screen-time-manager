package com.example.screentimemanager.data.repository

import com.example.screentimemanager.data.firebase.user.UserFirebase
import com.example.screentimemanager.data.firebase.user.UserFirebaseDao

class UserRepository(
    private val userFirebaseDao: UserFirebaseDao
) {
    /**
     * @param username
     * @return
     * return the User object with the given username
     */
    fun getUser(username: String): UserFirebase {
        return userFirebaseDao.getUser(username)
    }

    // TODO: other functions for authentication
}