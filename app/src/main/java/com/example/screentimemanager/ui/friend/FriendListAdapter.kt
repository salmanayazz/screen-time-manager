package com.example.screentimemanager.ui.friend

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.example.screentimemanager.R
import com.example.screentimemanager.data.firebase.friend.FriendFirebaseDao
import com.example.screentimemanager.data.firebase.user.UserFirebase
import com.example.screentimemanager.data.firebase.user.UserFirebaseDao
import com.example.screentimemanager.data.repository.FriendRepository
import com.example.screentimemanager.data.repository.UserRepository
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class FriendListAdapter(private var context: Context, @LayoutRes private val layoutResource: Int, private var array: ArrayList<String>): ArrayAdapter<String>(context, layoutResource, array) {
    private lateinit var imgProfile: ImageView
    private lateinit var tvName: TextView

    override fun getItem(position: Int): String? {
        return array[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val firebaseRef = FirebaseDatabase.getInstance().reference
        val userFirebaseDao = UserFirebaseDao(firebaseRef)
        val userRepository = UserRepository(userFirebaseDao)
        var friend: UserFirebase? = null
        CoroutineScope(IO).launch{
            friend = userRepository.getUser(array[position])
        }
        val ret = LayoutInflater.from(context).inflate(R.layout.layout_friend_list, parent, false)
        //Image view to show the friend's profile picture
        imgProfile = ret.findViewById(R.id.img_friend_profile_pics)
        //Text view to show the friend's first name + last name
        tvName = ret.findViewById(R.id.tv_friend_username)
        if(friend != null){
            if(friend!!.profilePicture != null){
//            imgProfile.setImageBitmap(friend.profilePicture)
            }
            if(friend!!.firstName != null && friend!!.lastName != null){
                tvName.text = "${friend!!.firstName} ${friend!!.lastName}"
            }
        }
        return ret
    }

}