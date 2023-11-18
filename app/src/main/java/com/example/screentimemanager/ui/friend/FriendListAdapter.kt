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

class FriendListAdapter(private var context: Context, @LayoutRes private val layoutResource: Int, private var array: ArrayList<Friend>): ArrayAdapter<Friend>(context, layoutResource, array) {
    private lateinit var imgProfile: ImageView
    private lateinit var tvName: TextView
    override fun getItem(position: Int): Friend? {
        return array[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val friend = array[position]
        val ret = LayoutInflater.from(context).inflate(R.layout.layout_friend_list, parent, false)
        imgProfile = ret.findViewById(R.id.img_friend_profile_pics)
        tvName = ret.findViewById(R.id.tv_friend_username)
        if(friend.profilePic != null){
            imgProfile.setImageBitmap(friend.profilePic)
        }
        if(friend.firstName != null && friend.lastName != null){
            tvName.text = "${friend.firstName} ${friend.lastName}"
        }
        return ret
    }

}