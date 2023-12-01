package com.example.screentimemanager.friendRequestNotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.screentimemanager.R
import com.example.screentimemanager.data.firebase.user.UserFirebase
import com.example.screentimemanager.data.firebase.user.UserFirebaseDao
import com.example.screentimemanager.data.repository.UserRepository
import com.example.screentimemanager.ui.friend.AddFriendsActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

const val TAG = "New Token"
class FriendRequestNotificationService(): FirebaseMessagingService() {
    private val firebaseRef = FirebaseDatabase.getInstance().reference
    private val userFirebaseDao = UserFirebaseDao(firebaseRef)
    private val userRepository = UserRepository(userFirebaseDao)
    private lateinit var notificationManager: NotificationManager

    private val CHANNEL_ID = "channel id"
    private val CHANNEL_NAME = "channel name"
    private val NOTIFY_ID = 3
    private val REQUEST_CODE = 1
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        CoroutineScope(IO).launch{
            userRepository.updateToken(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        message.data.isNotEmpty().let {
            if (message.data["type"] == "friend_request") {
                val senderEmail = message.data["senderEmail"]
                showFriendRequestNotification(senderEmail)
            }
        }
    }

    private fun showFriendRequestNotification(senderEmail: String?){
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID) //(Context, channelId: String)
        val intent = Intent(applicationContext, AddFriendsActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE)
        var sender: UserFirebase? = null
        CoroutineScope(IO).launch{
            if(senderEmail != null){
                sender = userRepository.getUser(senderEmail)
            }
        }

        notificationBuilder.setContentTitle("New Friend Request")
        notificationBuilder.setContentText("${sender!!.firstName} ${sender!!.lastName} has sent you a friend request")
        notificationBuilder.setSmallIcon(R.drawable.baseline_friend_24)
        notificationBuilder.setContentIntent(pendingIntent)
        notificationBuilder.setAutoCancel(false)
        val notification = notificationBuilder.build()
        if(Build.VERSION.SDK_INT > 26){
            val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(NOTIFY_ID, notification)
    }
}