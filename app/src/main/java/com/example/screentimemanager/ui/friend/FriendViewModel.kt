package com.example.screentimemanager.ui.friend

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FriendViewModel: ViewModel() {
    var signedIn = MutableLiveData<Boolean>()
    var friends = MutableLiveData<ArrayList<Friend>>()
    init{
        signedIn.value = false
        friends.value = ArrayList()
    }

    //Add friend to friends
    fun addFriend(friend: Friend){
        friends.value?.add(friend)
        //Forced the VM to notice the change
        friends.value = friends.value
    }

}