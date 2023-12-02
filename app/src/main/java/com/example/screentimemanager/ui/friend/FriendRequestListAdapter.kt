package com.example.screentimemanager.ui.friend

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.example.screentimemanager.R
import com.example.screentimemanager.data.firebase.friend.FriendFirebaseDao
import com.example.screentimemanager.data.firebase.user.UserFirebase
import com.example.screentimemanager.data.repository.FriendRepository
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class FriendRequestListAdapter(private var context: Context, private var array: ArrayList<UserFirebase>): BaseAdapter() {
    private lateinit var tvFriendName: TextView
    private lateinit var btnAcceptRequest: Button
    private lateinit var btnDeclineRequest: Button
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var friendFirebaseDao: FriendFirebaseDao
    private lateinit var friendRepository: FriendRepository
    override fun getCount(): Int {
        return array.size
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val friend = array[position]
        firebaseRef = FirebaseDatabase.getInstance().reference
        friendFirebaseDao = FriendFirebaseDao(firebaseRef)
        friendRepository = FriendRepository(friendFirebaseDao)
        val ret = LayoutInflater.from(context).inflate(R.layout.friend_request_layout, parent, false)
        tvFriendName = ret.findViewById(R.id.tv_friendRequestName)
        tvFriendName.text = "${friend.firstName} ${friend.lastName}"
        btnAcceptRequest = ret.findViewById(R.id.btn_acceptRequest)
        btnDeclineRequest = ret.findViewById(R.id.btn_declineRequest)
        btnAcceptRequest.setOnClickListener{
            CoroutineScope(IO).launch{
                //Accept friend request
                friendRepository.acceptFriendRequest(friend.email)
            }
        }
        btnDeclineRequest.setOnClickListener{
            CoroutineScope(IO).launch{
                //Decline friend request
                friendRepository.deleteFriend(friend.email)
            }
        }
        return ret
    }

    fun clear(){
        array.clear()
    }

    fun addAll(arrayList: ArrayList<UserFirebase>){
        array = arrayList
    }

}