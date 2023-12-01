package com.example.screentimemanager.friendRequestNotification

import com.google.firebase.messaging.FirebaseMessagingService

class FriendRequestNotificationService: FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}