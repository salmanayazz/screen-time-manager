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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
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
    private val IS_REQUEST = "request"
    private val SENDER_EMAIL = "senderEmail"
    private val RECEIVER_EMAIL = "receiverEmail"
    override fun onCreate() {
        Log.i("angus: notification service", "onCreate() called")
        val userEmail: String = FirebaseAuth.getInstance().currentUser?.email?: "Not Logged In"
        val sanitUserEmail = userEmail.replace("@", "(").replace(".", ")")
        val friendsRef: DatabaseReference = firebaseRef.child("friends")
        friendsRef.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                for(receiver in snapshot.children){
                    for(sender in receiver.children){
                        val receiverEmail = snapshot.key
                        val senderEmail = receiver.key
                        if(receiverEmail == sanitUserEmail){
                            Log.i("angus: notification", "receiverEmail == userEmail")
                            val unsanitSenderEmail = senderEmail!!.replace("(", "@").replace(")", ".")
                            showFriendRequestNotification(unsanitSenderEmail)
                        }
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                //Do nothing
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                //Do nothing
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                //Do nothing
            }

            override fun onCancelled(error: DatabaseError) {
                //Do nothing
            }

        })
    }
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        CoroutineScope(IO).launch{
            userRepository.updateToken(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.i("angus: notification service", "onMessageReceived() called")
        message.data.isNotEmpty().let {
            if (message.data["type"] == "friend_request") {
                val senderEmail = message.data["senderEmail"]
                Log.i("angus: notification service", "senderEmail: $senderEmail")
                if(senderEmail != null){
                    showFriendRequestNotification(senderEmail)
                }
            }
        }
    }

    private fun showFriendRequestNotification(senderEmail: String?){
        Log.i("angus: notification service", "notification() senderEmail: $senderEmail")
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID) //(Context, channelId: String)
        val intent = Intent(applicationContext, AddFriendsActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE)
        var sender: UserFirebase? = null
        CoroutineScope(IO).launch{
            if(senderEmail != null){
                sender = userRepository.getUser(senderEmail)
                if(sender != null){
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
                else{
                    Log.e("angus: notification service", "sender not found")
                }
            }
        }
    }
}