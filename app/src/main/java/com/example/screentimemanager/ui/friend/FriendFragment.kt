package com.example.screentimemanager.ui.friend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.screentimemanager.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FriendFragment : Fragment() {
    private lateinit var btnAddFriend: FloatingActionButton
    private lateinit var friendList: ListView
    private lateinit var friendViewModel: FriendViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val ret = inflater.inflate(R.layout.fragment_friend, container, false)
        friendViewModel = ViewModelProvider(requireActivity()).get(FriendViewModel::class.java)
        btnAddFriend = ret.findViewById(R.id.fab_addFriend)
        friendList = ret.findViewById(R.id.lv_friendList)

        return ret
    }
}