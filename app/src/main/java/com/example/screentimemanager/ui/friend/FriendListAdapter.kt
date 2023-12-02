package com.example.screentimemanager.ui.friend

import android.content.Context
import android.content.Intent
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FriendListAdapter(
    private var context: Context,
    @LayoutRes private val layoutResource: Int,
    private var array: ArrayList<String>
) : ArrayAdapter<String>(context, layoutResource, array) {
    private lateinit var imgProfile: ImageView
    private lateinit var tvName: TextView

    // Listener to handle item clicks
    private var onItemClickListener: ((UserFirebase) -> Unit)? = null

    // setter for the item click listener
    fun setOnItemClickListener(listener: (UserFirebase) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItem(position: Int): String {
        return array[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val firebaseRef = FirebaseDatabase.getInstance().reference
        val userFirebaseDao = UserFirebaseDao(firebaseRef)
        val userRepository = UserRepository(userFirebaseDao)
        var friend: UserFirebase? = null

        val ret =
            LayoutInflater.from(context).inflate(R.layout.layout_friend_list, parent, false)

        CoroutineScope(Dispatchers.IO).launch {
            friend = userRepository.getUser(array[position])

            withContext(Dispatchers.Main) {
                // ui updates
                imgProfile = ret.findViewById(R.id.img_friend_profile_pics)
                tvName = ret.findViewById(R.id.tv_friend_username)

                // add pfp
                if (friend?.profilePicture != null) {
//                    imgProfile.setImageBitmap(friend?.profilePicture)
                }

                tvName.text = "${friend?.firstName} ${friend?.lastName}"

                // set click listener on the whole item
                ret.setOnClickListener {
                    onItemClickListener?.invoke(friend ?: return@setOnClickListener)
                }
            }
        }

        return ret
    }
}