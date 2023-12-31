package com.example.screentimemanager.data.firebase.friend

/**
 * (PK = sender + receiver)
 * a friend relationship between two users, will also include pending friend requests
 * @param receiverEmail
 * the email of user who received the friend request
 * (FK to User table)
 * @param senderEmail
 * the email of user who sent the friend request
 * (FK to User table)
 * @param isRequest
 * true if the friend request is still pending, false if the friend request is accepted
 */
data class FriendFirebase (
    val receiverEmail: String,
    val senderEmail: String,
    val isRequest: Boolean
)
