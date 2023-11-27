package com.example.screentimemanager.data.firebase.user

/**
 * (PK = email)
 * a user of the app
 * @param email
 * email of the user (unique)
 * @param firstName
 * first name of the user
 * @param lastName
 * last name of the user
 */
data class UserFirebase (
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String,
    val profilePicture: String?
)
