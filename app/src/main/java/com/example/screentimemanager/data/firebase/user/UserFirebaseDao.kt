package com.example.screentimemanager.data.firebase.user

import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserFirebaseDao(
    private val database: DatabaseReference
) {
    // use database.child.("users").child(userEmail) to query firebase

    /**
     * @param email
     * @return
     * return the User object with the given email
     */
    suspend fun getUser(email: String): UserFirebase? {
        return withContext(Dispatchers.IO){
            val userRef =
                database.child("users")
                .child(email.replace("@","(").replace(".",")"))
                .get().await()
            println("debug: UserFirebasedao getUser for email ${email} " +
                    "-> ${userRef.getValue(UserFirebase::class.java)}")
            userRef.getValue(UserFirebase::class.java)
        }
    }
    suspend fun addUser(user: UserFirebase){
        withContext(Dispatchers.IO){
            database.child("users")
                .child(user.email.replace("@","(").replace(".",")"))
                .setValue(user).await()
        }
    }

}