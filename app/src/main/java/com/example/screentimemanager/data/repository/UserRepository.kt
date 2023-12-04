package com.example.screentimemanager.data.repository

import com.example.screentimemanager.data.firebase.user.UserFirebase
import com.example.screentimemanager.data.firebase.user.UserFirebaseDao
import com.google.firebase.auth.FirebaseAuth

class UserRepository(
    private val userFirebaseDao: UserFirebaseDao
) {
    /**
     * @param username
     * @return
     * return the User object with the given username
     */
    suspend fun getUser(username: String): UserFirebase? {
        return userFirebaseDao.getUser(username)
    }

    suspend fun updateToken(token: String){
        val userEmail: String = FirebaseAuth.getInstance().currentUser?.email?: "Not Logged In"
        userFirebaseDao.updateToken(userEmail, token)
    }

    // TODO: other functions for authentication
}