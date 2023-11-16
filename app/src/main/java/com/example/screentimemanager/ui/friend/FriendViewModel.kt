package com.example.screentimemanager.ui.friend

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FriendViewModel: ViewModel() {
    val signedIn = MutableLiveData<Boolean>()
}