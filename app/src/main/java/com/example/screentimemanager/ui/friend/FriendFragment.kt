package com.example.screentimemanager.ui.friend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
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

        //temp. adapter
//        val adapter = FriendListAdapter(requireActivity())
//        friendList.adapter = adapter

        btnAddFriend.setOnClickListener{
            if(friendViewModel.signedIn.value == false){
                val dialog = AlertDialog.Builder(requireActivity())
                dialog.setTitle(getString(R.string.sign_in_required))
                dialog.setMessage(getString(R.string.sign_in_required_message))
                dialog.setPositiveButton(getString(R.string.sign_in)){ _, _ ->
                    //Intent to sign in page
                }
                dialog.setNegativeButton(getString(R.string.cancel)){_, _ ->

                }
                dialog.setCancelable(false)
                dialog.show()
            }
            else{
                val dialog = AddFriendFragmentDialog()
                dialog.show(requireActivity().supportFragmentManager, ADD_FRIEND_DIALOG_TAG)
            }
        }
        return ret
    }
}