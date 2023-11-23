package com.example.screentimemanager.data.firebase.user

import com.google.firebase.database.DatabaseReference

class UserFirebaseDao(
    private val database: DatabaseReference
) {
    /**
     * @param email
     * @return
     * return the User object with the given email
     */
    suspend fun getUser(email: String): UserFirebase {
        return UserFirebase("", "", "")
    }

    // TODO: other functions for authentication
}